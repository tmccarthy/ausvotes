package au.id.tmm.ausvotes.data_sources.nswec.legco

import au.id.tmm.ausvotes.data_sources.common.CsvParsing.{noneIfBlank, parsePossibleInt, parsePossibleString}
import au.id.tmm.ausvotes.data_sources.nswec.legco.NswLegCoDataImpl.PreferencesRow
import au.id.tmm.ausvotes.data_sources.nswec.legco.NswLegCoStreams.SpreadsheetCell
import au.id.tmm.ausvotes.model
import au.id.tmm.ausvotes.model.nsw.legco._
import au.id.tmm.ausvotes.model.stv.BallotGroup.{Code => BallotGroupCode}
import au.id.tmm.ausvotes.model.{Name, Party, nsw, stv}
import au.id.tmm.bfect.BMonad
import au.id.tmm.bfect.BMonad.AbsolveOptionOps
import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.effects.Die
import au.id.tmm.bfect.effects.Die.Ops
import au.id.tmm.bfect.fs2interop.Fs2Compiler
import au.id.tmm.utilities.hashing.Pairing
import cats.instances.option._
import cats.instances.string.catsKernelStdOrderForString
import cats.syntax.traverse._

class NswLegCoDataImpl[F[+_, +_] : Die : Fs2Compiler](streams: NswLegCoStreams[F]) extends NswLegCoData[F] {

  private val namePattern = """^([A-Z\-c\s]+)\s([A-Z][a-z].*)$""".r

  override def fetchGroupsAndCandidatesFor(election: NswLegCoElection): F[Exception, GroupsAndCandidates] =
    for {
      groupRows <- BMonad.pure(streams.groupRows(election))

      groups   <- groupRows
        .drop(1)
        .evalMap(row => parseGroupRowFromSpreadsheetRow(election, row))
        .flatMap {
          case _: Ungrouped => fs2.Stream.empty
          case g: Group     => fs2.Stream.emit(g)
        }
        .compile.toVector
        .refineToExceptionOrDie

      groupLookupByCode = groups.groupBy(_.code).mapValues(_.head)

      candidateRows = streams.candidateRows(election)

      candidates   <- candidateRows
        .drop(1)
        .evalMap(row => parseCandidateRowFromSpreadsheet(election, groupLookupByCode, row))
        .compile.toVector
        .refineToExceptionOrDie

    } yield GroupsAndCandidates(groups.toSet, candidates.toSet)

  private def parseGroupRowFromSpreadsheetRow(election: NswLegCoElection, row: Vector[SpreadsheetCell]): F[Exception, BallotGroup] =
    for {
      rawCode  <- row.lift(1).traverse(takeStringFrom).absolveOption(new Exception("Missing group code"))
      rawParty <- row.lift(2).traverse(takeStringFrom)
      code <- parseGroupCode(rawCode)

      party = rawParty.map(_.trim).filter(_.nonEmpty).map(Party.apply)

      group <- code match {
        case stv.Ungrouped.code => BMonad.pure(Ungrouped(election)): F[Exception, BallotGroup]
        case groupCode => BMonad.fromEither(Group(election, groupCode, party)): F[Exception, BallotGroup]
      }
    } yield group

  private def parseCandidateRowFromSpreadsheet(
    election: NswLegCoElection,
    groupLookupByCode: Map[BallotGroupCode, Group],
    row: Vector[SpreadsheetCell],
  ): F[Exception, Candidate] =
    for {
      rawBallotPos <- row.lift(0).traverse(takeIntFrom).absolveOption(new Exception("Missing ballot position"))
      rawGroupCode <- row.lift(2).traverse(takeStringFrom).absolveOption(new Exception("Missing group code"))
      rawName      <- row.lift(3).traverse(takeStringFrom).absolveOption(new Exception("Missing name"))

      groupCode    <- parseGroupCode(rawGroupCode)
      group        <- (groupCode match {
        case stv.Ungrouped.code => BMonad.pure(Ungrouped(election))
        case groupCode          => BMonad.fromOption(groupLookupByCode.get(groupCode), new Exception(s"Group code $groupCode did not appear in groups section of spreadsheet"))
      }): F[Exception, BallotGroup]

      candidatePos  = CandidatePosition(group, rawBallotPos - 1)
      party         = group match {
        case stv.Group(_, _, party) => party
        case stv.Ungrouped(_)       => None
      }

      name          = rawName match {
        case namePattern(surname, givenNames) => Name(givenNames, surname)
        case _                                => Name("", rawName)
      }
      id            = model.CandidateDetails.Id(Pairing.Szudzik.pair(
        candidatePos.group.code.index,
        candidatePos.indexInGroup,
      ))
    } yield Candidate(election, CandidateDetails(election, name, party, id), candidatePos)

  private def takeStringFrom(spreadsheetCell: SpreadsheetCell): F[Exception, String] =
    spreadsheetCell match {
      case SpreadsheetCell.WithString(string) => BMonad.pure(string)
      case cell                               => BMonad.leftPure(new Exception(s"Cannot extract string from $cell"))
    }

  private def takeIntFrom(spreadsheetCell: SpreadsheetCell): F[Exception, Int] =
    spreadsheetCell match {
      case SpreadsheetCell.WithDouble(double) => BMonad.pureCatchException(double.toInt)
      case cell                               => BMonad.leftPure(new Exception(s"Cannot extract int from $cell"))
    }

  private def parseGroupCode(rawCode: String): F[Exception, BallotGroupCode] = BMonad.fromEither(BallotGroupCode(rawCode))
    .leftMap { case BallotGroupCode.InvalidCode(badCode) => new Exception(s"Invalid ballot code $badCode") }

  override def fetchPreferencesFor(
    election: NswLegCoElection,
    groupsAndCandidates: GroupsAndCandidates,
  ): F[Exception, fs2.Stream[F[Throwable, +?], Ballot]] =
    for {
      csvRows <- BMonad.pure(streams.preferenceRows(election))

      ballots = csvRows
        .zipWithIndex
        .drop(1)
        .evalMap { case (row, index) =>
          BMonad.pureCatchException {
            PreferencesRow(
              row(0).toInt,
              row(1),
              row(2),
              row(3),
              row(4),
              parsePossibleString(row(5)),
              parsePossibleInt(row(6)),
              noneIfBlank(row(7)),
              noneIfBlank(row(8)),
              parsePossibleInt(row(9)),
              row(10) == "Formal",
              parsePossibleString(row(11)),
            )
          }.leftMap(cause => throw new Exception(s"Error when parsing row $index", cause))
        }
        .groupAdjacentBy(_.ballotPaperID)
        .map { case (_, chunk) =>
          val rowsForBallot = chunk.toVector
          val headRow = rowsForBallot.head

          Ballot(
            election,
            BallotJurisdiction(
              nsw.District(
                election.stateElection,
                headRow.districtName,
              ),
              vcp = ???,
            ),
            BallotId(headRow.sequenceNumber),
            groupPreferences = ???,
            candidatePreferences = ???,
          )
        }

    } yield ballots

}

object NswLegCoDataImpl {
  private final case class PreferencesRow(
    sequenceNumber: Int,
    districtName: String,
    voteTypeName: String,
    venueName: String,
    ballotPaperID: String,
    preferenceMark: Option[String],
    preferenceNumber: Option[Int],
    candidateName: Option[String],
    groupCode: Option[String],
    drawOrder: Option[Int],
    formal: Boolean,
    typeName: Option[String],
  )
}

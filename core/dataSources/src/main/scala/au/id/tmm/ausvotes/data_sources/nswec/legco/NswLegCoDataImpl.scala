package au.id.tmm.ausvotes.data_sources.nswec.legco

import java.net.URL
import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.common.CsvParsing.{noneIfBlank, parsePossibleInt, parsePossibleString}
import au.id.tmm.ausvotes.data_sources.common.UrlUtils.StringOps
import au.id.tmm.ausvotes.data_sources.common.streaming.{OpeningInputStreams, ReadingInputStreams}
import au.id.tmm.ausvotes.data_sources.nswec.legco.NswLegCoDataImpl.PreferencesRow
import au.id.tmm.ausvotes.model
import au.id.tmm.ausvotes.model.nsw.NswElection
import au.id.tmm.ausvotes.model.nsw.legco._
import au.id.tmm.ausvotes.model.stv.BallotGroup.{Code => BallotGroupCode}
import au.id.tmm.ausvotes.model.{Name, Party, nsw, stv}
import au.id.tmm.bfect.effects.Sync.Ops
import au.id.tmm.bfect.effects.{Bracket, Sync}
import au.id.tmm.bfect.fs2interop.Fs2Compiler
import au.id.tmm.utilities.hashing.Pairing
import cats.instances.string.catsKernelStdOrderForString
import com.github.tototoshi.csv.TSVFormat
import org.apache.poi.ss.usermodel.{Row => ExcelRow}

class NswLegCoDataImpl[F[+_, +_] : Sync : Bracket : Fs2Compiler](
  resourceStoreLocation: Path,
  replaceExisting: Boolean,
) extends NswLegCoData[F] {

  private val namePattern = """^([A-Z\-c\s]+)\s([A-Z][a-z].*)$""".r

  override def fetchGroupsAndCandidatesFor(election: NswLegCoElection): F[Exception, GroupsAndCandidates] =
    for {
      url <- election match {
        case NswLegCoElection(NswElection.`2019`) => Sync.pureCatchException(new URL("https://vtrprodragrsstorage01-secondary.blob.core.windows.net/vtrdata-sg1901/lc/SGE2019%20LC%20Candidates.xlsx?st=2019-03-01T01%3A00%3A00Z&se=2020-03-01T01%3A00%3A00Z&sp=r&sv=2018-03-28&sr=c&sig=KPBiRIYtRCT3aWxdLhdcPWb3qbC3wHubyftHBwIjg2Q%3D"))
        case _ => Sync.leftPure(new Exception(s"Unsupported election $election"))
      }

      localPath <- OpeningInputStreams.downloadToDirectory(url, resourceStoreLocation, replaceExisting)

      groupRows = ReadingInputStreams.streamExcel(OpeningInputStreams.openFile(localPath), sheetIndex = 1)

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

      candidateRows = ReadingInputStreams.streamExcel(OpeningInputStreams.openFile(localPath), sheetIndex = 0)

      candidates   <- candidateRows
        .drop(1)
        .evalMap(row => parseCandidateRowFromSpreadsheet(election, groupLookupByCode, row))
        .compile.toVector
        .refineToExceptionOrDie

    } yield GroupsAndCandidates(groups.toSet, candidates.toSet)

  private def parseGroupRowFromSpreadsheetRow(election: NswLegCoElection, row: ExcelRow): F[Exception, BallotGroup] =
    for {
      rawCode  <- Sync.pureCatchException(row.getCell(1).getStringCellValue)
      rawParty <- Sync.pureCatchException(Option(row.getCell(2)).map(_.getStringCellValue))
      code <- parseGroupCode(rawCode)

      party = rawParty.map(_.trim).filter(_.nonEmpty).map(Party.apply)

      group <- code match {
        case stv.Ungrouped.code => Sync.pure(Ungrouped(election)): F[Exception, BallotGroup]
        case groupCode => Sync.fromEither(Group(election, groupCode, party)): F[Exception, BallotGroup]
      }
    } yield group

  private def parseCandidateRowFromSpreadsheet(
    election: NswLegCoElection,
    groupLookupByCode: Map[BallotGroupCode, Group],
    row: ExcelRow,
  ): F[Exception, Candidate] =
    for {
      rawBallotPos <- Sync.pureCatchException(row.getCell(0).getNumericCellValue.toInt)
      rawGroupCode <- Sync.pureCatchException(row.getCell(2).getStringCellValue)
      rawName      <- Sync.pureCatchException(row.getCell(3).getStringCellValue)

      groupCode    <- parseGroupCode(rawGroupCode)
      group        <- (groupCode match {
        case stv.Ungrouped.code => Sync.pure(Ungrouped(election))
        case groupCode          => Sync.fromOption(groupLookupByCode.get(groupCode), new Exception(s"Group code $groupCode did not appear in groups section of spreadsheet"))
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

  private def parseGroupCode(rawCode: String): F[Exception, BallotGroupCode] = Sync.fromEither(BallotGroupCode(rawCode))
    .leftMap { case BallotGroupCode.InvalidCode(badCode) => new Exception(s"Invalid ballot code $badCode") }

  override def fetchPreferencesFor(
    election: NswLegCoElection,
    groupsAndCandidates: GroupsAndCandidates,
  ): F[Exception, fs2.Stream[F[Throwable, +?], Ballot]] =
    for {
      urlAndZipName <- election match {
        case NswLegCoElection(NswElection.`2019`) =>
          for {
            url <- Sync.fromEither("https://vtrprodragrsstorage01-secondary.blob.core.windows.net/vtrdata-sg1901/lc/SGE2019%20LC%20Pref%20Data%20Statewide.zip?st=2019-03-01T01%3A00%3A00Z&se=2020-03-01T01%3A00%3A00Z&sp=r&sv=2018-03-28&sr=c&sig=KPBiRIYtRCT3aWxdLhdcPWb3qbC3wHubyftHBwIjg2Q%3D".parseUrl): F[Exception, URL]
            zipEntryName = "SGE2019 LC Pref Data_NA_State.txt"
          } yield (url, zipEntryName)

        case _ => Sync.leftPure(new RuntimeException(s"Cannot download resource for $election"))
      }

      url = urlAndZipName._1
      zipEntryName = urlAndZipName._2

      localPath <- OpeningInputStreams.downloadToDirectory(url, resourceStoreLocation, replaceExisting)

      lines <- ReadingInputStreams.streamLines(OpeningInputStreams.openZipEntry(localPath, zipEntryName))

      csvRows = ReadingInputStreams.streamCsv(lines, new TSVFormat {})

      ballots = csvRows
        .zipWithIndex
        .drop(1)
        .evalMap { case (row, index) =>
          Sync.pureCatchException {
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

package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.FetchSenateGroupsAndCandidates
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.FetchSenateGroupsAndCandidatesFromRaw.{ACandidate, AGroup, GroupOrCandidate, UnrecognisedGroup}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawSenateFirstPreferences
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop._
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.stv.{BallotGroup, Group, Ungrouped}
import au.id.tmm.ausvotes.model.{Candidate, ExceptionCaseClass, Name, Party}
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{SyncEffects, BifunctorMonadError => BME}
import au.id.tmm.utilities.collection.Flyweight
import au.id.tmm.utilities.geo.australia.State

final class FetchSenateGroupsAndCandidatesFromRaw[F[+_, +_] : FetchRawSenateFirstPreferences : SyncEffects] private() extends FetchSenateGroupsAndCandidates[F] {

  private val groupFlyweight: Flyweight[(SenateElectionForState, BallotGroup.Code, Option[Party]), Either[Group.InvalidGroupCode.type, SenateGroup]] = Flyweight { tuple =>
    SenateGroup(tuple._1, tuple._2, tuple._3)
  }

  override def senateGroupsAndCandidatesFor(
                                             electionForState: SenateElectionForState,
                                           ): F[FetchSenateGroupsAndCandidates.Error, SenateGroupsAndCandidates] =
    senateGroupsAndCandidatesFor(electionForState.election).map(_.findFor(electionForState))

  override def senateGroupsAndCandidatesFor(
                                             election: SenateElection,
                                           ): F[FetchSenateGroupsAndCandidates.Error, SenateGroupsAndCandidates] =
    for {
      firstPreferencesRows <- implicitly[FetchRawSenateFirstPreferences[F]].senateFirstPreferencesFor(election)
        .leftMap(FetchSenateGroupsAndCandidates.Error)

      streamOfGroupsAndCandidatesWithBallotGroupMap = firstPreferencesRows.evalMapAccumulate(Map.empty[BallotGroup.Code, SenateGroup]) { case (groupsByCode, firstPreferencesRow) =>
        for {
          groupOrCandidate <- BME.fromEither(fromFirstPreferencesRow(groupsByCode, election, firstPreferencesRow))
        } yield {
          groupOrCandidate match {
            case AGroup(group) => (groupsByCode + (group.code -> group), groupOrCandidate)
            case ACandidate(_) => (groupsByCode, groupOrCandidate)
          }
        }
      }

      groupsAndCandidatesWithBallotGroupMap <- streamOfGroupsAndCandidatesWithBallotGroupMap.compile.toVector
        .swallowThrowablesAndWrapIn(FetchSenateGroupsAndCandidates.Error)

    } yield {
      val candidatesBuilder = Set.newBuilder[SenateCandidate]
      val groupsBuilder = Set.newBuilder[SenateGroup]

      groupsAndCandidatesWithBallotGroupMap.foreach {
        case (_, AGroup(group)) => groupsBuilder += group
        case (_, ACandidate(candidate)) => candidatesBuilder += candidate
      }

      SenateGroupsAndCandidates(groupsBuilder.result(), candidatesBuilder.result())
    }

  private def fromFirstPreferencesRow(
                                       groupsByCode: Map[BallotGroup.Code, SenateGroup],
                                       election: SenateElection,
                                       rawRow: FetchRawSenateFirstPreferences.Row,
                                     ): Either[FetchSenateGroupsAndCandidates.Error, GroupOrCandidate] =
    for {
      state <- stateFrom(rawRow)
        .left.map(FetchSenateGroupsAndCandidates.Error)

      electionForState <- election.electionForState(state)
        .toRight(FetchSenateGroupsAndCandidates.Error(SenateElectionForState.NoElectionForState(election, state)))

      groupOrCandidate <-
        if (rawRow.positionInGroup == 0) {
          groupFromTicketRow(electionForState, rawRow)
            .map(AGroup)
            .left.map(FetchSenateGroupsAndCandidates.Error)
        } else {
          candidateFrom(groupsByCode, electionForState, rawRow)
            .map(ACandidate)
            .left.map(FetchSenateGroupsAndCandidates.Error)
        }

    } yield groupOrCandidate

  private def groupFromTicketRow(
                                  election: SenateElectionForState,
                                  rawRow: FetchRawSenateFirstPreferences.Row,
                                ): Either[Group.InvalidGroupCode.type, SenateGroup] = {
    assert(rawRow.positionInGroup == 0)

    val party = partyFrom(rawRow)
    val groupCode = BallotGroup.Code(rawRow.ticket.trim) match {
      case Right(success) => success
      case Left(failure) => throw failure
    }

    groupFlyweight(election, groupCode, party)
  }

  private def stateFrom(rawRow: FetchRawSenateFirstPreferences.Row): Either[CommonParsing.BadState, State] =
    CommonParsing.parseState(rawRow.state)

  private def partyFrom(rawRow: FetchRawSenateFirstPreferences.Row): Option[Party] = {
    val partyName = rawRow.party.trim

    partyName.toLowerCase match {
      case "" | "independent" => None
      case _ => Some(Party(partyName))
    }
  }

  private def candidateFrom(
                             groupsByCode: Map[BallotGroup.Code, SenateGroup],
                             election: SenateElectionForState,
                             rawRow: FetchRawSenateFirstPreferences.Row,
                           ): Either[ExceptionCaseClass, SenateCandidate] =
    for {
      position <- candidatePositionFrom(election, groupsByCode, rawRow)
      name = candidateNameFrom(rawRow)
      party = partyFrom(rawRow)
    } yield SenateCandidate(election, SenateCandidateDetails(election, name, party, Candidate.Id(rawRow.candidateId.trim.toInt)), position)

  private def candidateNameFrom(rawRow: FetchRawSenateFirstPreferences.Row): Name = {
    val commaSeparatedName = rawRow.candidateDetails
    val commaIndex = commaSeparatedName.indexOf(',')

    val surname = commaSeparatedName.substring(0, commaIndex).trim
    val givenNames = commaSeparatedName.substring(commaIndex + 1).trim

    Name(givenNames, surname)
  }

  private def candidatePositionFrom(
                                     election: SenateElectionForState,
                                     groupsByCode: Map[BallotGroup.Code, SenateGroup],
                                     rawRow: FetchRawSenateFirstPreferences.Row,
                                   ): Either[ExceptionCaseClass, SenateCandidatePosition] = {

    groupFromCandidateRow(election, groupsByCode, rawRow).map { group =>
      val positionInGroup = rawRow.positionInGroup - 1 // To make it zero indexed

      SenateCandidatePosition(group, positionInGroup)
    }
  }

  private def groupFromCandidateRow(
                                     election: SenateElectionForState,
                                     groupsByCode: Map[BallotGroup.Code, SenateGroup],
                                     rawRow: FetchRawSenateFirstPreferences.Row,
                                   ): Either[ExceptionCaseClass, SenateBallotGroup] = {

    for {
      code <- BallotGroup.Code(rawRow.ticket.trim)

      group <- code match {
        case Ungrouped.code => Right(Ungrouped(election))
        case code => groupsByCode.get(code)
          .toRight(UnrecognisedGroup(code))
      }
    } yield group
  }

}

object FetchSenateGroupsAndCandidatesFromRaw {

  def apply[F[+_, +_] : FetchRawSenateFirstPreferences : SyncEffects]: FetchSenateGroupsAndCandidatesFromRaw[F] = new FetchSenateGroupsAndCandidatesFromRaw()

  final case class UnrecognisedGroup(code: BallotGroup.Code) extends ExceptionCaseClass

  private sealed trait GroupOrCandidate

  private case class AGroup(group: SenateGroup) extends GroupOrCandidate
  private case class ACandidate(candidate: SenateCandidate) extends GroupOrCandidate

}

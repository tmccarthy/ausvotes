package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.model.GroupsAndCandidates
import au.id.tmm.ausvotes.core.rawdata.model.FirstPreferencesRow
import au.id.tmm.ausvotes.model.Flyweights.GroupFlyweight
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.stv.{BallotGroup, Ungrouped}
import au.id.tmm.ausvotes.model.{Candidate, Name, Party}

import scala.collection.mutable

object GroupAndCandidateGeneration {

  def fromFirstPreferencesRows(
                                election: SenateElection,
                                rows: TraversableOnce[FirstPreferencesRow],
                                groupFlyweight: GroupFlyweight[SenateElectionForState] = GroupFlyweight[SenateElectionForState](),
                              ): GroupsAndCandidates = {
    val groupsByCode: mutable.Map[BallotGroup.Code, SenateGroup] = mutable.Map()

    val groups: mutable.ArrayBuffer[SenateGroup] = mutable.ArrayBuffer()
    val candidates: mutable.ArrayBuffer[SenateCandidate] = mutable.ArrayBuffer()

    for (row <- rows) {
      val groupOrCandidate = fromFirstPreferencesRow(groupFlyweight, groupsByCode, election, row)

      groupOrCandidate.left.foreach(group => {
        groupsByCode.put(group.code, group)
        groups.append(group)
      })
      groupOrCandidate.right.foreach(candidates.append(_))
    }

    GroupsAndCandidates(groups.toSet, candidates.toSet)
  }

  private def fromFirstPreferencesRow(groupFlyweight: GroupFlyweight[SenateElectionForState],
                                      groupsByCode: mutable.Map[BallotGroup.Code, SenateGroup],
                                      election: SenateElection,
                                      rawRow: FirstPreferencesRow): Either[SenateGroup, SenateCandidate] = {
    val state = stateFrom(rawRow)
    val electionForState = SenateElectionForState(election, state) match {
      case Right(success) => success
      case Left(failure) => throw failure
    }

    if (rawRow.positionInGroup == 0) {
      Left(groupFromTicketRow(groupFlyweight, electionForState, rawRow))
    } else {
      Right(candidateFrom(groupFlyweight, groupsByCode, electionForState, rawRow))
    }
  }

  private def groupFromTicketRow(groupFlyweight: GroupFlyweight[SenateElectionForState],
                                 election: SenateElectionForState,
                                 rawRow: FirstPreferencesRow): SenateGroup = {
    assert(rawRow.positionInGroup == 0)

    val party = partyFrom(rawRow)
    val groupCode = BallotGroup.Code(rawRow.ticket.trim) match {
      case Right(success) => success
      case Left(failure) => throw failure
    }

    groupFlyweight.make(election, groupCode, party) match {
      case Right(success) => success
      case Left(failure) => throw failure
    }
  }

  private def stateFrom(rawRow: FirstPreferencesRow) = GenerationUtils.stateFrom(rawRow.state, rawRow)

  private def partyFrom(rawRow: FirstPreferencesRow): Option[Party] = {
    val partyName = rawRow.party.trim

    if (partyName.isEmpty || ("Independent" equalsIgnoreCase partyName)) {
      None
    } else {
      Some(Party(partyName))
    }
  }

  private def candidateFrom(groupFlyweight: GroupFlyweight[SenateElectionForState],
                            groupsByCode: mutable.Map[BallotGroup.Code, SenateGroup],
                            election: SenateElectionForState,
                            rawRow: FirstPreferencesRow): SenateCandidate = {
    assert(rawRow.positionInGroup != 0)

    val party = partyFrom(rawRow)
    val name = candidateNameFrom(rawRow)
    val position = candidatePositionFrom(election, groupsByCode, rawRow)

    SenateCandidate(election, SenateCandidateDetails(election, name, party, Candidate.Id(rawRow.candidateId.trim.toInt)), position)
  }

  private def candidateNameFrom(rawRow: FirstPreferencesRow): Name = {
    val commaSeparatedName = rawRow.candidateDetails
    val commaIndex = commaSeparatedName.indexOf(',')

    val surname = commaSeparatedName.substring(0, commaIndex).trim
    val givenNames = commaSeparatedName.substring(commaIndex + 1).trim

    Name(givenNames, surname)
  }

  private def candidatePositionFrom(election: SenateElectionForState,
                                    groupsByCode: mutable.Map[BallotGroup.Code, SenateGroup],
                                    rawRow: FirstPreferencesRow): SenateCandidatePosition = {
    val group = groupFromCandidateRow(election, groupsByCode, rawRow)
    val positionInGroup = rawRow.positionInGroup - 1 // To make it zero indexed

    SenateCandidatePosition(group, positionInGroup)
  }

  private def groupFromCandidateRow(election: SenateElectionForState,
                                    groupsByCode: mutable.Map[BallotGroup.Code, SenateGroup],
                                    rawRow: FirstPreferencesRow): SenateBallotGroup = {
    val groupCode = BallotGroup.Code(rawRow.ticket.trim) match {
      case Right(success) => success
      case Left(failure) => throw failure
    }

    if (Ungrouped.code == groupCode) {
      Ungrouped(election)
    } else {
      groupsByCode(groupCode)
    }
  }
}

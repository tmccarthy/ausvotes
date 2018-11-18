package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.model.flyweights.{GroupFlyweight, RegisteredPartyFlyweight}
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.ausvotes.core.model.parsing.Party.Independent
import au.id.tmm.ausvotes.core.model.parsing._
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.core.rawdata.model.FirstPreferencesRow

import scala.collection.mutable

object GroupAndCandidateGeneration {

  def fromFirstPreferencesRows(election: SenateElection,
                               rows: TraversableOnce[FirstPreferencesRow],
                               groupFlyweight: GroupFlyweight = GroupFlyweight(),
                               partyFlyweight: RegisteredPartyFlyweight = RegisteredPartyFlyweight()): GroupsAndCandidates = {
    val groupsByCode: mutable.Map[String, Group] = mutable.Map()

    val groups: mutable.ArrayBuffer[Group] = mutable.ArrayBuffer()
    val candidates: mutable.ArrayBuffer[Candidate] = mutable.ArrayBuffer()

    for (row <- rows) {
      val groupOrCandidate = fromFirstPreferencesRow(groupFlyweight, partyFlyweight, groupsByCode, election, row)

      groupOrCandidate.left.foreach(group => {
        groupsByCode.put(group.code, group)
        groups.append(group)
      })
      groupOrCandidate.right.foreach(candidates.append(_))
    }

    GroupsAndCandidates(groups.toSet, candidates.toSet)
  }

  private def fromFirstPreferencesRow(groupFlyweight: GroupFlyweight,
                                      partyFlyweight: RegisteredPartyFlyweight,
                                      groupsByCode: mutable.Map[String, Group],
                                      election: SenateElection,
                                      rawRow: FirstPreferencesRow): Either[Group, Candidate] = {
    if (rawRow.positionInGroup == 0) {
      Left(groupFromTicketRow(groupFlyweight, partyFlyweight, election, rawRow))
    } else {
      Right(candidateFrom(groupFlyweight, partyFlyweight, groupsByCode, election, rawRow))
    }
  }

  private def groupFromTicketRow(groupFlyweight: GroupFlyweight,
                                 partyFlyweight: RegisteredPartyFlyweight,
                                 election: SenateElection,
                                 rawRow: FirstPreferencesRow): Group = {
    assert(rawRow.positionInGroup == 0)

    val state = stateFrom(rawRow)
    val party = partyFrom(partyFlyweight, rawRow)

    groupFlyweight(election, state, rawRow.ticket.trim, party)
  }

  private def stateFrom(rawRow: FirstPreferencesRow) = GenerationUtils.stateFrom(rawRow.state, rawRow)

  private def partyFrom(partyFlyweight: RegisteredPartyFlyweight,
                        rawRow: FirstPreferencesRow): Party = {
    val partyName = rawRow.party.trim

    if (partyName.isEmpty || ("Independent" equalsIgnoreCase partyName)) {
      Independent
    } else {
      partyFlyweight(partyName)
    }
  }

  private def candidateFrom(groupFlyweight: GroupFlyweight,
                            partyFlyweight: RegisteredPartyFlyweight,
                            groupsByCode: mutable.Map[String, Group],
                            election: SenateElection,
                            rawRow: FirstPreferencesRow): Candidate = {
    assert(rawRow.positionInGroup != 0)

    val state = stateFrom(rawRow)
    val party = partyFrom(partyFlyweight, rawRow)
    val name = candidateNameFrom(rawRow)
    val position = candidatePositionFrom(election, groupsByCode, rawRow)

    Candidate(election, state, AecCandidateId(rawRow.candidateId.trim), name, party, position)
  }

  private def candidateNameFrom(rawRow: FirstPreferencesRow): Name = {
    Name.parsedFrom(rawRow.candidateDetails)
  }

  private def candidatePositionFrom(election: SenateElection,
                                    groupsByCode: mutable.Map[String, Group],
                                    rawRow: FirstPreferencesRow): CandidatePosition = {
    val group = groupFromCandidateRow(election, groupsByCode, rawRow)
    val positionInGroup = rawRow.positionInGroup - 1 // To make it zero indexed

    CandidatePosition(group, positionInGroup)
  }

  private def groupFromCandidateRow(election: SenateElection,
                                    groupsByCode: mutable.Map[String, Group],
                                    rawRow: FirstPreferencesRow): BallotGroup = {
    val groupCode = rawRow.ticket.trim

    if (Ungrouped.code == groupCode) {
      Ungrouped(election, GenerationUtils.stateFrom(rawRow.state, rawRow))
    } else {
      groupsByCode(groupCode)
    }
  }
}

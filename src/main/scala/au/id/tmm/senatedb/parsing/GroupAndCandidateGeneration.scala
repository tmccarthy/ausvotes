package au.id.tmm.senatedb.parsing

import au.id.tmm.senatedb.model.flyweights.{GroupFlyweight, PartyFlyweight}
import au.id.tmm.senatedb.model.parsing._
import au.id.tmm.senatedb.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.rawdata.model.FirstPreferencesRow
import au.id.tmm.utilities.geo.australia.State

import scala.collection.mutable

object GroupAndCandidateGeneration {

  def fromFirstPreferencesRows(election: SenateElection,
                               rows: TraversableOnce[FirstPreferencesRow],
                               groupFlyweight: GroupFlyweight = GroupFlyweight(),
                               partyFlyweight: PartyFlyweight = PartyFlyweight()): GroupsAndCandidates = {
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
                                      partyFlyweight: PartyFlyweight,
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
                                 partyFlyweight: PartyFlyweight,
                                 election: SenateElection,
                                 rawRow: FirstPreferencesRow): Group = {
    assert(rawRow.positionInGroup == 0)

    val state = stateFrom(rawRow)
    val party = partyFrom(partyFlyweight, rawRow)

    groupFlyweight(election, state, rawRow.ticket.trim, party)
  }

  private def stateFrom(rawRow: FirstPreferencesRow) = State.fromAbbreviation(rawRow.state.trim)
    .getOrElse(throw new BadDataException(s"Encountered bad state value ${rawRow.state} in row $rawRow"))

  private def partyFrom(partyFlyweight: PartyFlyweight, rawRow: FirstPreferencesRow): Option[Party] = {
    val partyName = rawRow.party.trim

    if (partyName.isEmpty || ("Independent" equalsIgnoreCase partyName)) {
      None
    } else {
      Some(partyFlyweight(partyName))
    }
  }

  private def candidateFrom(groupFlyweight: GroupFlyweight,
                            partyFlyweight: PartyFlyweight,
                            groupsByCode: mutable.Map[String, Group],
                            election: SenateElection,
                            rawRow: FirstPreferencesRow): Candidate = {
    assert(rawRow.positionInGroup != 0)

    val state = stateFrom(rawRow)
    val party = partyFrom(partyFlyweight, rawRow)
    val name = candidateNameFrom(rawRow)
    val position = candidatePositionFrom(groupsByCode, rawRow)

    Candidate(election, state, rawRow.candidateId.trim, name, party, position)
  }

  private def candidateNameFrom(rawRow: FirstPreferencesRow): Name = {
    Name.parsedFrom(rawRow.candidateDetails)
  }

  private def candidatePositionFrom(groupsByCode: mutable.Map[String, Group],
                                    rawRow: FirstPreferencesRow): CandidatePosition = {
    val group = groupFromCandidateRow(groupsByCode, rawRow)
    val positionInGroup = rawRow.positionInGroup - 1 // To make it zero indexed

    CandidatePosition(group, positionInGroup)
  }

  private def groupFromCandidateRow(groupsByCode: mutable.Map[String, Group],
                                    rawRow: FirstPreferencesRow): BallotGroup = {
    val groupCode = rawRow.ticket.trim

    if (Ungrouped.code == groupCode) {
      Ungrouped
    } else {
      groupsByCode(groupCode)
    }
  }
}
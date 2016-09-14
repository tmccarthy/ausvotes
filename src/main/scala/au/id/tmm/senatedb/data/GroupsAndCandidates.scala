package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.model.{CandidatesRow, GroupsRow}
import au.id.tmm.senatedb.model.{SenateElection, State}

final case class GroupsAndCandidates(groups: Set[GroupsRow], candidates: Set[CandidatesRow]) {
  def addGroup(group: GroupsRow): GroupsAndCandidates = copy(groups = groups + group)
  def addCandidate(candidate: CandidatesRow): GroupsAndCandidates = copy(candidates = candidates + candidate)

  def filteredTo(election: SenateElection, state: State): GroupsAndCandidates = {
    val relevantGroups = groups.filter(_.election == election.aecID).filter(_.state == state.shortName)
    val relevantCandidates = candidates.filter(_.election == election.aecID).filter(_.state == state.shortName)

    GroupsAndCandidates(relevantGroups, relevantCandidates)
  }
}

object GroupsAndCandidates {
  def apply(): GroupsAndCandidates = GroupsAndCandidates(Set(), Set())
}
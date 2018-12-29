package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateElectionForState, SenateGroup}

final case class GroupsAndCandidates(groups: Set[SenateGroup], candidates: Set[SenateCandidate]) {
  def contains(group: SenateGroup): Boolean = groups.contains(group)

  def contains(candidate: SenateCandidate): Boolean = candidates.contains(candidate)

  def findFor(election: SenateElectionForState): GroupsAndCandidates = GroupsAndCandidates(
    groups = groups.toStream
      .filter(_.election == election)
      .toSet,

    candidates = candidates.toStream
      .filter(_.election == election)
      .toSet
  )
}

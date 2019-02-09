package au.id.tmm.ausvotes.model.federal.senate

final case class SenateGroupsAndCandidates(groups: Set[SenateGroup], candidates: Set[SenateCandidate]) {
  def contains(group: SenateGroup): Boolean = groups.contains(group)

  def contains(candidate: SenateCandidate): Boolean = candidates.contains(candidate)

  def findFor(election: SenateElectionForState): SenateGroupsAndCandidates = SenateGroupsAndCandidates(
    groups = groups.toStream
      .filter(_.election == election)
      .toSet,

    candidates = candidates.toStream
      .filter(_.election == election)
      .toSet
  )
}

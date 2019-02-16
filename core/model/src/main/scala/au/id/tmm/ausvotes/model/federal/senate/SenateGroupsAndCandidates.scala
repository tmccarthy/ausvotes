package au.id.tmm.ausvotes.model.federal.senate

import cats.kernel.CommutativeMonoid

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

object SenateGroupsAndCandidates {

  implicit val monoidForSenateGroupsAndCandidates: CommutativeMonoid[SenateGroupsAndCandidates] = new CommutativeMonoid[SenateGroupsAndCandidates] {
    override def empty: SenateGroupsAndCandidates = SenateGroupsAndCandidates(Set.empty, Set.empty)

    override def combine(left: SenateGroupsAndCandidates, right: SenateGroupsAndCandidates): SenateGroupsAndCandidates =
      SenateGroupsAndCandidates(left.groups ++ right.groups, left.candidates ++ right.candidates)
  }

}

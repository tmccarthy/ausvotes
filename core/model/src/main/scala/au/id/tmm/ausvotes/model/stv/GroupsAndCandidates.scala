package au.id.tmm.ausvotes.model.stv

import cats.kernel.CommutativeMonoid

final case class GroupsAndCandidates[E](groups: Set[Group[E]], candidates: Set[StvCandidate[E]]) {
  def contains(group: Group[E]): Boolean = groups.contains(group)

  def contains(candidate: StvCandidate[E]): Boolean = candidates.contains(candidate)

  def findFor(election: E): GroupsAndCandidates[E] = GroupsAndCandidates(
    groups = groups.toStream
      .filter(_.election == election)
      .toSet,

    candidates = candidates.toStream
      .filter(_.election == election)
      .toSet
  )
}

object GroupsAndCandidates {
  implicit def monoidForGroupsAndCandidates[E]: CommutativeMonoid[GroupsAndCandidates[E]] = new CommutativeMonoid[GroupsAndCandidates[E]] {
    override def empty: GroupsAndCandidates[E] = GroupsAndCandidates(Set.empty, Set.empty)

    override def combine(left: GroupsAndCandidates[E], right: GroupsAndCandidates[E]): GroupsAndCandidates[E] =
      GroupsAndCandidates(left.groups ++ right.groups, left.candidates ++ right.candidates)
  }
}

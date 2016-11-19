package au.id.tmm.senatedb.model

import au.id.tmm.senatedb.model.parsing.{Candidate, Group}
import au.id.tmm.utilities.geo.australia.State

final case class GroupsAndCandidates(groups: Set[Group], candidates: Set[Candidate]) {
  def contains(group: Group): Boolean = groups.contains(group)

  def contains(candidate: Candidate): Boolean = candidates.contains(candidate)

  def findFor(election: SenateElection, state: State): GroupsAndCandidates = GroupsAndCandidates(
    groups = groups.toStream
      .filter(_.election == election)
      .filter(_.state == state)
      .toSet,

    candidates = candidates.toStream
      .filter(_.election == election)
      .filter(_.state == state)
      .toSet
  )
}

package au.id.tmm.senatedb.model

import au.id.tmm.senatedb.model.parsing.{Candidate, Group}

final case class GroupsAndCandidates(groups: Set[Group], candidates: Set[Candidate]) {
  def contains(group: Group): Boolean = groups.contains(group)

  def contains(candidate: Candidate): Boolean = candidates.contains(candidate)
}

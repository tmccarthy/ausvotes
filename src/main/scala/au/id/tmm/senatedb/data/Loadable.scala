package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.model.{SenateElection, State}

sealed trait Loadable {
  def requires: Set[Loadable]
}

object Loadable {
  case class GroupsAndCandidates(election: SenateElection) extends Loadable {
    override val requires: Set[Loadable] = Set.empty
  }

  case class CountData(election: SenateElection, state: State) extends Loadable {
    override val requires: Set[Loadable] = Set(GroupsAndCandidates(election))
  }

  case class Ballots(election: SenateElection, state: State) extends Loadable {
    override val requires: Set[Loadable] = Set(CountData(election, state))
  }
}

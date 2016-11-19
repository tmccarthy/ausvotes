package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.model
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

object GroupsAndCandidates {

  trait GroupsAndCandidatesFixture {
    def election: SenateElection = SenateElection.`2016`
    def state: State

    def groupsAndCandidates: model.GroupsAndCandidates
  }

  object ACT extends GroupsAndCandidatesFixture {
    override val state = State.ACT

    val groupsAndCandidates = model.GroupsAndCandidates(Groups.ACT.groups, Candidates.ACT.candidates)
  }

}

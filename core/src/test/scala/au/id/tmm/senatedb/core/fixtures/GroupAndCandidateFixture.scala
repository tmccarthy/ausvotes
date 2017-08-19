package au.id.tmm.senatedb.core.fixtures

import au.id.tmm.senatedb.core.model
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

object GroupAndCandidateFixture {

  trait GroupsAndCandidatesFixture {
    def election: SenateElection = SenateElection.`2016`
    def state: State

    def groupsAndCandidates: au.id.tmm.senatedb.core.model.GroupsAndCandidates
  }

  object ACT extends GroupsAndCandidatesFixture {
    override val state = State.ACT

    val groupsAndCandidates = model.GroupsAndCandidates(GroupFixture.ACT.groups, CandidateFixture.ACT.candidates)
  }

}

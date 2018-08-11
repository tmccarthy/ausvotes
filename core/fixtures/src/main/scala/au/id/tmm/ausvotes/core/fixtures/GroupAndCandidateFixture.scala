package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.model
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

object GroupAndCandidateFixture {

  trait GroupsAndCandidatesFixture {
    def election: SenateElection = SenateElection.`2016`
    def state: State

    def groupsAndCandidates: au.id.tmm.ausvotes.core.model.GroupsAndCandidates
  }

  object ACT extends GroupsAndCandidatesFixture {
    override val state: State = State.ACT

    val groupsAndCandidates = model.GroupsAndCandidates(GroupFixture.ACT.groups, CandidateFixture.ACT.candidates)
  }

  object WA extends GroupsAndCandidatesFixture {
    override val state: State = State.WA

    val groupsAndCandidates = model.GroupsAndCandidates(GroupFixture.WA.groups, CandidateFixture.WA.candidates)
  }

}

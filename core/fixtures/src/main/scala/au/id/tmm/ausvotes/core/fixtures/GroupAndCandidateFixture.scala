package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.model.federal.senate
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState, SenateGroupsAndCandidates}
import au.id.tmm.ausgeo.State

object GroupAndCandidateFixture {

  trait GroupsAndCandidatesFixture {
    def senateElection: SenateElection = SenateElection.`2016`
    def state: State
    def election: SenateElectionForState = senateElection.electionForState(state).get

    def groupsAndCandidates: SenateGroupsAndCandidates
  }

  object ACT extends GroupsAndCandidatesFixture {
    override val state: State = State.ACT

    val groupsAndCandidates = senate.SenateGroupsAndCandidates(GroupFixture.ACT.groups, CandidateFixture.ACT.candidates)
  }

  object WA extends GroupsAndCandidatesFixture {
    override val state: State = State.WA

    val groupsAndCandidates = senate.SenateGroupsAndCandidates(GroupFixture.WA.groups, CandidateFixture.WA.candidates)
  }

}

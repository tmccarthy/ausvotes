package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.model.federal
import au.id.tmm.ausvotes.model.federal.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State

object DivisionAndPollingPlaceFixture {

  trait DivisionsAndPollingPlacesFixture {
    def senateElection: SenateElection.`2016`.type = SenateElection.`2016`
    def state: State
    def election: SenateElectionForState = senateElection.electionForState(state).get

    def divisionsAndPollingPlaces: DivisionsAndPollingPlaces
  }

  object ACT extends DivisionsAndPollingPlacesFixture {
    override val state: State = State.ACT

    override val divisionsAndPollingPlaces = federal.DivisionsAndPollingPlaces(
      DivisionFixture.ACT.divisions,
      PollingPlaceFixture.ACT.pollingPlaces,
    )
  }

  object WA extends DivisionsAndPollingPlacesFixture {
    override def state: State = State.WA

    override def divisionsAndPollingPlaces: DivisionsAndPollingPlaces = federal.DivisionsAndPollingPlaces(
      DivisionFixture.WA.divisions,
      PollingPlaceFixture.WA.pollingPlaces,
    )
  }
}

package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.model
import au.id.tmm.ausvotes.core.model.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State

object DivisionAndPollingPlaceFixture {

  trait DivisionsAndPollingPlacesFixture {
    def senateElection: SenateElection.`2016`.type = SenateElection.`2016`
    def state: State
    def election: SenateElectionForState = SenateElectionForState(senateElection, state).right.get

    def divisionsAndPollingPlaces: au.id.tmm.ausvotes.core.model.DivisionsAndPollingPlaces
  }

  object ACT extends DivisionsAndPollingPlacesFixture {
    override val state: State = State.ACT

    override val divisionsAndPollingPlaces = model.DivisionsAndPollingPlaces(
      DivisionFixture.ACT.divisions,
      PollingPlaceFixture.ACT.pollingPlaces,
    )
  }

  object WA extends DivisionsAndPollingPlacesFixture {
    override def state: State = State.WA

    override def divisionsAndPollingPlaces: DivisionsAndPollingPlaces = model.DivisionsAndPollingPlaces(
      DivisionFixture.WA.divisions,
      PollingPlaceFixture.WA.pollingPlaces,
    )
  }
}

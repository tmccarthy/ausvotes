package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.model
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

object DivisionAndPollingPlaceFixture {

  trait DivisionsAndPollingPlacesFixture {
    def election = SenateElection.`2016`
    def state: State

    def divisionsAndPollingPlaces: au.id.tmm.ausvotes.core.model.DivisionsAndPollingPlaces
  }

  object ACT extends DivisionsAndPollingPlacesFixture {
    override val state = State.ACT

    override val divisionsAndPollingPlaces = model.DivisionsAndPollingPlaces(DivisionFixture.ACT.divisions, PollingPlaceFixture.ACT.pollingPlaces)
  }
}

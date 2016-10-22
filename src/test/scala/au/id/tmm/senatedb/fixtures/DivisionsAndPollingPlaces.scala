package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.model
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

object DivisionsAndPollingPlaces {

  trait DivisionsAndPollingPlacesFixture {
    def election = SenateElection.`2016`
    def state: State

    def divisionsAndPollingPlaces: model.DivisionsAndPollingPlaces
  }

  object ACT extends DivisionsAndPollingPlacesFixture {
    override val state = State.ACT

    override val divisionsAndPollingPlaces = model.DivisionsAndPollingPlaces(Divisions.ACT.divisions, PollingPlaces.ACT.pollingPlaces)
  }
}

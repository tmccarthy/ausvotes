package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.model
import au.id.tmm.ausvotes.core.model.{DivisionsAndPollingPlaces, SenateElection}
import au.id.tmm.utilities.geo.australia.State

object DivisionAndPollingPlaceFixture {

  trait DivisionsAndPollingPlacesFixture {
    def election: SenateElection.`2016`.type = SenateElection.`2016`
    def state: State

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

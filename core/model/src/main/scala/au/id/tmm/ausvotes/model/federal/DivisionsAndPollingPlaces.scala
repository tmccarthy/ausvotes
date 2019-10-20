package au.id.tmm.ausvotes.model.federal

import au.id.tmm.ausvotes.model.federal.FederalVoteCollectionPoint.FederalPollingPlace
import au.id.tmm.ausgeo.State

import scala.collection.{MapView, mutable}

final case class DivisionsAndPollingPlaces(divisions: Set[Division],
                                           pollingPlaces: Set[FederalPollingPlace]) {

  lazy val lookupDivisionByName: MapView[String, Division] = divisions.groupBy(_.name).view.mapValues(_.head)

  lazy val lookupPollingPlaceByName: MapView[(State, String), FederalPollingPlace] = pollingPlaces
    .groupBy(pollingPlace => (pollingPlace.state, pollingPlace.name))
    .view.mapValues(_.head)

  def findFor(election: FederalElection, state: State): DivisionsAndPollingPlaces = DivisionsAndPollingPlaces(
    divisions = divisions.to(LazyList)
      .filter(_.election == election)
      .filter(_.jurisdiction == state)
      .toSet,
    pollingPlaces = pollingPlaces.to(LazyList)
      .filter(_.election == election)
      .filter(_.state == state)
      .toSet
  )

}

object DivisionsAndPollingPlaces {
  final case class DivisionAndPollingPlace(division: Division, pollingPlace: FederalPollingPlace)

  def from(divisionsAndPollingPlaces: Iterable[DivisionAndPollingPlace]): DivisionsAndPollingPlaces = {
    val divisions = mutable.ArrayBuffer[Division]()
    val pollingPlaces = mutable.ArrayBuffer[FederalPollingPlace]()

    for (DivisionAndPollingPlace(division, pollingPlace) <- divisionsAndPollingPlaces) {
      divisions.append(division)
      pollingPlaces.append(pollingPlace)
    }

    DivisionsAndPollingPlaces(divisions.toSet, pollingPlaces.toSet)
  }
}

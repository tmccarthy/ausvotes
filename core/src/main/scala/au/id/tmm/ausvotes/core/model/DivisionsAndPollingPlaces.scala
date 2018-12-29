package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.model.federal.{Division, FederalElection, FederalPollingPlace}
import au.id.tmm.utilities.geo.australia.State

import scala.collection.mutable

final case class DivisionsAndPollingPlaces(divisions: Set[Division],
                                           pollingPlaces: Set[FederalPollingPlace]) {

  lazy val lookupDivisionByName: Map[String, Division] = divisions.groupBy(_.name).mapValues(_.head)

  lazy val lookupPollingPlaceByName: Map[(State, String), FederalPollingPlace] = pollingPlaces
    .groupBy(pollingPlace => (pollingPlace.jurisdiction.state, pollingPlace.name))
    .mapValues(_.head)

  def findFor(election: FederalElection, state: State): DivisionsAndPollingPlaces = DivisionsAndPollingPlaces(
    divisions = divisions.toStream
      .filter(_.election == election)
      .filter(_.jurisdiction == state)
      .toSet,
    pollingPlaces = pollingPlaces.toStream
      .filter(_.election == election)
      .filter(_.jurisdiction.state == state)
      .toSet
  )

}

object DivisionsAndPollingPlaces {
  final case class DivisionAndPollingPlace(division: Division, pollingPlace: FederalPollingPlace)

  def from(divisionsAndPollingPlaces: TraversableOnce[DivisionAndPollingPlace]): DivisionsAndPollingPlaces = {
    val divisions = mutable.ArrayBuffer[Division]()
    val pollingPlaces = mutable.ArrayBuffer[FederalPollingPlace]()

    for (DivisionAndPollingPlace(division, pollingPlace) <- divisionsAndPollingPlaces) {
      divisions.append(division)
      pollingPlaces.append(pollingPlace)
    }

    DivisionsAndPollingPlaces(divisions.toSet, pollingPlaces.toSet)
  }
}

package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.core.model.parsing.{Division, PollingPlace}
import au.id.tmm.utilities.geo.australia.State

import scala.collection.mutable

final case class DivisionsAndPollingPlaces(divisions: Set[Division],
                                           pollingPlaces: Set[PollingPlace]) {

  lazy val lookupDivisionByName: Map[String, Division] = divisions.groupBy(_.name).mapValues(_.head)

  lazy val lookupPollingPlaceByName: Map[(State, String), PollingPlace] = pollingPlaces
    .groupBy(pollingPlace => (pollingPlace.state, pollingPlace.name))
    .mapValues(_.head)

  def findFor(election: SenateElection, state: State): DivisionsAndPollingPlaces = DivisionsAndPollingPlaces(
    divisions = divisions.toStream
      .filter(_.election == election)
      .filter(_.state == state)
      .toSet,
    pollingPlaces = pollingPlaces.toStream
      .filter(_.election == election)
      .filter(_.state == state)
      .toSet
  )

}

object DivisionsAndPollingPlaces {
  final case class DivisionAndPollingPlace(division: Division, pollingPlace: PollingPlace)

  def from(divisionsAndPollingPlaces: TraversableOnce[DivisionAndPollingPlace]): DivisionsAndPollingPlaces = {
    val divisions = mutable.ArrayBuffer[Division]()
    val pollingPlaces = mutable.ArrayBuffer[PollingPlace]()

    for (DivisionAndPollingPlace(division, pollingPlace) <- divisionsAndPollingPlaces) {
      divisions.append(division)
      pollingPlaces.append(pollingPlace)
    }

    DivisionsAndPollingPlaces(divisions.toSet, pollingPlaces.toSet)
  }
}
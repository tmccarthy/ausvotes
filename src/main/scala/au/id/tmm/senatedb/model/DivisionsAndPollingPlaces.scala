package au.id.tmm.senatedb.model

import au.id.tmm.senatedb.model.parsing.{Division, PollingPlace}

import scala.collection.mutable

final case class DivisionsAndPollingPlaces(divisions: Set[Division],
                                           pollingPlaces: Set[PollingPlace]) {

  lazy val lookupDivisionByName: Map[String, Division] = divisions.groupBy(_.name).mapValues(_.head)

  lazy val lookupPollingPlaceByName: Map[String, PollingPlace] = pollingPlaces.groupBy(_.name).mapValues(_.head)

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
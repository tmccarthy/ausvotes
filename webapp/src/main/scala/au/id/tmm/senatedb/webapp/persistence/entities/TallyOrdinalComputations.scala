package au.id.tmm.senatedb.webapp.persistence.entities

import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.senatedb.core.tallies.Tally
import au.id.tmm.utilities.geo.australia.State

object TallyOrdinalComputations {

  def ordinalNationally[A](tally: Tally[A]): Map[A, Int] = {
    ordinalWithinJurisdiction(tally.values)
  }

  def ordinalWithinState[A](tally: Tally[A], computeState: A => State): Map[A, Int] = {
    ordinalWithinJurisdiction(tally, computeState)
  }

  def ordinalWithinDivision[A](tally: Tally[A], computeDivision: A => Division): Map[A, Int] = {
    ordinalWithinJurisdiction(tally, computeDivision)
  }

  private def ordinalWithinJurisdiction[A, B](tally: Tally[A], outerJurisdictionToOrdinalJurisdiction: A => B): Map[A, Int] = {
    tally.values
      .toStream
      .groupBy {
        case (outerJurisdiction, count) => outerJurisdictionToOrdinalJurisdiction(outerJurisdiction)
      }
      .flatMap {
        case (innerJurisdiction, outerJurisdictionsWithCounts) => ordinalWithinJurisdiction(outerJurisdictionsWithCounts)
      }
  }

  private def ordinalWithinJurisdiction[A](jurisdictionsWithCounts: Iterable[(A, Double)]): Map[A, Int] = {
    jurisdictionsWithCounts.toStream
      .sortBy {
        case (jurisdiction, count) => count
      }
      .reverse
      .map {
        case (jurisdiction, count) => jurisdiction
      }
      .zipWithIndex
      .toMap
  }

}

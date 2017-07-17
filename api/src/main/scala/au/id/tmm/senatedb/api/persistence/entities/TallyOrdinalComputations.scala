package au.id.tmm.senatedb.api.persistence.entities

import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.senatedb.core.tallies.{Tally0, Tally1}
import au.id.tmm.utilities.geo.australia.State

object TallyOrdinalComputations {

  def ordinalNationally[A](tally: Tally1[A]): Map[A, Int] = {
    ordinalWithinJurisdiction(tally.asMap)
  }

  def ordinalWithinState[A](tally: Tally1[A], computeState: A => State): Map[A, Int] = {
    ordinalWithinJurisdiction(tally, computeState)
  }

  def ordinalWithinDivision[A](tally: Tally1[A], computeDivision: A => Division): Map[A, Int] = {
    ordinalWithinJurisdiction(tally, computeDivision)
  }

  private def ordinalWithinJurisdiction[A, B](tally: Tally1[A], outerJurisdictionToOrdinalJurisdiction: A => B): Map[A, Int] = {
    tally.asMap
      .toStream
      .groupBy {
        case (outerJurisdiction, count) => outerJurisdictionToOrdinalJurisdiction(outerJurisdiction)
      }
      .flatMap {
        case (innerJurisdiction, outerJurisdictionsWithCounts) => ordinalWithinJurisdiction(outerJurisdictionsWithCounts)
      }
  }

  private def ordinalWithinJurisdiction[A](jurisdictionsWithCounts: Iterable[(A, Tally0)]): Map[A, Int] = {
    jurisdictionsWithCounts.toStream
      .sortBy {
        case (jurisdiction, count) => count.value
      }
      .reverse
      .map {
        case (jurisdiction, count) => jurisdiction
      }
      .zipWithIndex
      .toMap
  }

}

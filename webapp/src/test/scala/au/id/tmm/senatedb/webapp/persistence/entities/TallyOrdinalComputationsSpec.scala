package au.id.tmm.senatedb.webapp.persistence.entities

import au.id.tmm.senatedb.core.fixtures.{Divisions, PollingPlaces}
import au.id.tmm.senatedb.core.model.parsing.{Division, VoteCollectionPoint}
import au.id.tmm.senatedb.core.tallies.Tally.MapOps
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TallyOrdinalComputationsSpec extends ImprovedFlatSpec {

  "a tally ordinal map" can "be computed for a group of divisions nationally" in {
    val tally = Map(
      Divisions.ACT.CANBERRA -> 50d,
      Divisions.NT.LINGIARI -> 40d,
      Divisions.NT.SOLOMON -> 30d
    ).toTally

    val actualOrdinalsNationally = TallyOrdinalComputations.ordinalNationally(tally)

    val expectedOrdinalsNationally = Map(
      Divisions.ACT.CANBERRA -> 0,
      Divisions.NT.LINGIARI -> 1,
      Divisions.NT.SOLOMON -> 2
    )

    assert(expectedOrdinalsNationally === actualOrdinalsNationally)
  }

  it can "be computed for a group of divisions by state" in {
    val tally = Map(
      Divisions.ACT.CANBERRA -> 50d,
      Divisions.NT.LINGIARI -> 40d,
      Divisions.NT.SOLOMON -> 30d
    ).toTally

    val actualOrdinals = TallyOrdinalComputations.ordinalWithinState[Division](tally, _.state)

    val expectedOrdinals = Map(
      Divisions.ACT.CANBERRA -> 0,
      Divisions.NT.LINGIARI -> 0,
      Divisions.NT.SOLOMON -> 1
    )

    assert(expectedOrdinals === actualOrdinals)
  }

  it can "be computed for a group of vote collection points by state" in {
    val tally = Map[VoteCollectionPoint, Double](
      PollingPlaces.ACT.BARTON -> 50d,
      PollingPlaces.ACT.WODEN_PRE_POLL -> 40d,
      PollingPlaces.NT.ALICE_SPRINGS -> 30d
    ).toTally

    val actualOrdinals = TallyOrdinalComputations.ordinalWithinDivision[VoteCollectionPoint](tally, _.division)

    val expectedOrdinals = Map(
      PollingPlaces.ACT.BARTON -> 0,
      PollingPlaces.ACT.WODEN_PRE_POLL -> 1,
      PollingPlaces.NT.ALICE_SPRINGS -> 0
    )

    assert(expectedOrdinals === actualOrdinals)
  }
}

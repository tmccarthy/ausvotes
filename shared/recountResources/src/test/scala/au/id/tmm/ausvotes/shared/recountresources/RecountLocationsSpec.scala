package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.shared.aws.data.S3ObjectKey
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountLocationsSpec extends ImprovedFlatSpec {

  "The recount location" can "be computed when there are no ineligible candidates" in {
    val recountLocation = RecountLocations.locationOfRecountFor(RecountRequest(
      SenateElection.`2016`,
      State.VIC,
      12,
      Set.empty,
    ))

    val expectedLocation = S3ObjectKey(
      "recounts",
      "2016",
      "VIC",
      "12-vacancies",
      "none-ineligible",
      "result.json",
    )

    assert(recountLocation === expectedLocation)
  }

  it can "be computed when there are ineligible candidates" in {
    val recountLocation = RecountLocations.locationOfRecountFor(RecountRequest(
      SenateElection.`2014 WA`,
      State.WA,
      6,
      Set("123", "456"),
    ))

    val expectedLocation = S3ObjectKey(
      "recounts",
      "2014WA",
      "WA",
      "6-vacancies",
      "123-456-ineligible",
      "result.json",
    )

    assert(recountLocation === expectedLocation)
  }

  it can "be computed when there are ineligible candidates with special characters" in {
    val recountLocation = RecountLocations.locationOfRecountFor(RecountRequest(
      SenateElection.`2014 WA`,
      State.WA,
      6,
      Set("$&%?/ "),
    ))

    val expectedLocation = S3ObjectKey(
      "recounts",
      "2014WA",
      "WA",
      "6-vacancies",
      "%24%26%25%3F%2F+-ineligible",
      "result.json",
    )

    assert(recountLocation === expectedLocation)
  }

  it can "be computed when there are ineligible candidates with hyphens" in {
    val recountLocation = RecountLocations.locationOfRecountFor(RecountRequest(
      SenateElection.`2014 WA`,
      State.WA,
      6,
      Set("123-456"),
    ))

    val expectedLocation = S3ObjectKey(
      "recounts",
      "2014WA",
      "WA",
      "6-vacancies",
      "123%2D456-ineligible",
      "result.json",
    )

    assert(recountLocation === expectedLocation)
  }

}

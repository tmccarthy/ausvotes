package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.model.CandidateDetails
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.shared.aws.data.S3ObjectKey
import au.id.tmm.ausgeo.State
import org.scalatest.FlatSpec

class RecountLocationsSpec extends FlatSpec {

  "The recount location" can "be computed when there are no ineligible candidates" in {
    val recountLocation = RecountLocations.locationOfRecountFor(RecountRequest(
      SenateElection.`2016`.electionForState(State.VIC).get,
      12,
      Set.empty,
      doRounding = true,
    ))

    val expectedLocation = S3ObjectKey(
      "recounts",
      "2016",
      "VIC",
      "12-vacancies",
      "none-ineligible",
      "with-rounding",
      "result.json",
    )

    assert(recountLocation === expectedLocation)
  }

  it can "be computed when there are ineligible candidates" in {
    val recountLocation = RecountLocations.locationOfRecountFor(RecountRequest(
      SenateElection.`2014 WA`.electionForState(State.WA).get,
      6,
      Set(CandidateDetails.Id(123), CandidateDetails.Id(456)),
      doRounding = false,
    ))

    val expectedLocation = S3ObjectKey(
      "recounts",
      "2014WA",
      "WA",
      "6-vacancies",
      "123-456-ineligible",
      "no-rounding",
      "result.json",
    )

    assert(recountLocation === expectedLocation)
  }

}

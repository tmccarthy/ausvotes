package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest
import au.id.tmm.ausvotes.model.Candidate
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountRoutesSpec extends ImprovedFlatSpec {

  "constructing a recount request" should "use path and query params" in {
    val actualRecountRequest = RecountRoutes.buildRecountRequest(
      "2016",
      "VIC",
      Map(
        "vacancies" -> List("6", "12"),
        "ineligibleCandidates" -> List("1234"),
        "doRounding" -> List("true", "false")
      ),
    )

    val expectedRecountRequest = RecountApiRequest(
      SenateElection.`2016`.electionForState(State.VIC).get,
      numVacancies = Some(6),
      ineligibleCandidates = Some(Set(Candidate.Id(1234))),
      doRounding = Some(true),
    )

    assert(actualRecountRequest === Right(expectedRecountRequest))
  }

}

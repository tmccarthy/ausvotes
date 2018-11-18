package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
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
      ),
    )

    val expectedRecountRequest = RecountApiRequest(
      SenateElection.`2016`,
      State.VIC,
      numVacancies = Some(6),
      ineligibleCandidates = Some(Set(AecCandidateId("1234"))),
    )

    assert(actualRecountRequest === Right(expectedRecountRequest))
  }

}

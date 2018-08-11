package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.core.model.parsing.Name
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.RecountRequestError.InvalidCandidateIds
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CandidateActualisationSpec extends ImprovedFlatSpec {

  private val candidateFixture = CandidateFixture.NT

  "the actualisation of ineligible candidates" should "lookup candidates by aec id" in {
    assert(
      CandidateActualisation.actualiseIneligibleCandidates(Set("28820", "28575"), candidateFixture.candidates) ===
      Right(Set(
        candidateFixture.candidateWithName(Name("Malarndirri", "MCCARTHY")),
        candidateFixture.candidateWithName(Name("Nigel", "SCULLION")),
      ))
    )
  }

  it should "fail if any ids are unrecognised" in {
    assert(
      CandidateActualisation.actualiseIneligibleCandidates(Set("28820", "invalid1", "invalid2"), candidateFixture.candidates) ===
        Left(InvalidCandidateIds(Set("invalid1", "invalid2")))
    )
  }

}

package au.id.tmm.ausvotes.shared.recountresources.recount

import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.ausvotes.core.model.parsing.Name
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CandidateActualisationSpec extends ImprovedFlatSpec {

  private val candidateFixture = CandidateFixture.NT

  private val mMccarthy = candidateFixture.candidateWithName(Name("Malarndirri", "MCCARTHY"))
  private val nScullion = candidateFixture.candidateWithName(Name("Nigel", "SCULLION"))

  "the actualisation of ineligible candidates" should "lookup candidates by aec id" in {
    val candidateIds = Set(AecCandidateId("28820"), AecCandidateId("28575"), AecCandidateId("invalid"))

    val actualResult = CandidateActualisation.actualiseCandidates(candidateFixture.candidates)(candidateIds)

    val expectedActualisedCandidates = Set(
      mMccarthy,
      nScullion,
    )

    assert(actualResult === CandidateActualisation.Result(expectedActualisedCandidates, Set(AecCandidateId("invalid"))))
  }

  "a candidate actualisation result" can "be converted to an either if all candidates were matched" in {
    assert(
      CandidateActualisation.Result(Set(mMccarthy, nScullion), Set.empty).invalidCandidateIdsOrCandidates ===
        Right(Set(mMccarthy, nScullion))
    )
  }

  it can "be converted to an either if a candidate was unmatched" in {
    assert(
      CandidateActualisation.Result(Set(mMccarthy, nScullion), Set(AecCandidateId("invalid"))).invalidCandidateIdsOrCandidates ===
        Left(Set(AecCandidateId("invalid")))
    )
  }

}

package au.id.tmm.ausvotes.shared.recountresources.recount

import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.model.Name
import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateElection}
import au.id.tmm.ausvotes.shared.recountresources.RecountRequest
import au.id.tmm.ausvotes.shared.recountresources.entities.testing.EntitiesTestData
import au.id.tmm.countstv.model.CompletedCount
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.ausgeo.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import org.scalatest.FlatSpec

class RunRecountSpec extends FlatSpec {

  private val candidateFixture = CandidateFixture.ACT

  private val katyGallagher = candidateFixture.candidateWithName(Name("Katy", "GALLAGHER"))
  private val zedSeselja = candidateFixture.candidateWithName(Name("Zed", "SESELJA"))

  "performing a recount" should "successfully complete a count" in {

    val election = SenateElection.`2016`.electionForState(State.ACT).get

    val request = RecountRequest(
      election = election,
      vacancies = 2,
      ineligibleCandidateAecIds = Set.empty,
      doRounding = true,
    )

    val logicUnderTest = RunRecount.runRecountRequest[EntitiesTestData.TestIO](request)

    val testData = EntitiesTestData(
      groups = Map(election -> candidateFixture.groupFixture.groups),
      candidates = Map(election -> candidateFixture.candidates),
      ballots = Map(election -> candidateFixture.candidates.toVector.flatMap { candidate =>
        if (candidate == katyGallagher) {
          Vector.fill(100)(Vector(candidate))
        } else if (candidate == zedSeselja) {
          Vector.fill(99)(Vector(candidate))
        } else {
          Vector.fill(0)(Vector(candidate))
        }
      })
    )

    val actualResult: Either[RunRecount.Error, ProbabilityMeasure[CompletedCount[SenateCandidate]]] = logicUnderTest.runEither(testData)

    assert(actualResult.map(_.onlyOutcomeUnsafe.outcomes.electedCandidates) === Right(DupelessSeq(
      katyGallagher,
      zedSeselja,
    )))
  }

}

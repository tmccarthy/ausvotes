package au.id.tmm.ausvotes.shared.recountresources.recount

import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Name
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.recountresources.entities.testing.EntitiesTestData
import au.id.tmm.ausvotes.shared.recountresources.{CountResult, RecountRequest}
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RunRecountSpec extends ImprovedFlatSpec {

  private val candidateFixture = CandidateFixture.ACT

  private val katyGallagher = candidateFixture.candidateWithName(Name("Katy", "GALLAGHER"))
  private val zedSeselja = candidateFixture.candidateWithName(Name("Zed", "SESELJA"))

  "performing a recount" should "successfully complete a count" in {

    val election = SenateElection.`2016`
    val state = State.ACT

    val request = RecountRequest(
      election = election,
      state = state,
      vacancies = 2,
      ineligibleCandidateAecIds = Set.empty,
      doRounding = true,
    )

    val logicUnderTest = RunRecount.runRecountRequest[TestIO[EntitiesTestData, +?, +?]](request)

    val testData = EntitiesTestData(
      groups = Map((election, state) -> candidateFixture.groupFixture.groups),
      candidates = Map((election, state) -> candidateFixture.candidates),
      ballots = Map((election, state) -> candidateFixture.candidates.toVector.flatMap { candidate =>
        if (candidate == katyGallagher) {
          Vector.fill(100)(Vector(candidate))
        } else if (candidate == zedSeselja) {
          Vector.fill(99)(Vector(candidate))
        } else {
          Vector.fill(0)(Vector(candidate))
        }
      })
    )

    val actualResult: Either[RunRecount.Error, CountResult] = logicUnderTest.run(testData).result

    assert(actualResult.map(_.outcomePossibilities.onlyOutcomeUnsafe.elected) === Right(DupelessSeq(
      katyGallagher,
      zedSeselja,
    )))
  }

}

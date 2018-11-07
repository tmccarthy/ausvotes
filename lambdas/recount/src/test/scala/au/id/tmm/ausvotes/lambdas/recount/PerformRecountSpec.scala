package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Name
import au.id.tmm.countstv.model.preferences.PreferenceTree
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PerformRecountSpec extends ImprovedFlatSpec {

  private val candidateFixture = CandidateFixture.ACT

  private val katyGallagher = candidateFixture.candidateWithName(Name("Katy", "GALLAGHER"))
  private val zedSeselja = candidateFixture.candidateWithName(Name("Zed", "SESELJA"))

  "performing a recount" should "successfully complete a count" in {

    val ballotPapers = candidateFixture.candidates.toVector.flatMap { candidate =>
      val btlPosition = candidate.btlPosition

      if (candidate == katyGallagher) {
        Vector.fill(100)(Vector(btlPosition))
      } else if (candidate == zedSeselja) {
        Vector.fill(99)(Vector(btlPosition))
      } else {
        Vector.fill(0)(Vector(btlPosition))
      }
    }

    val preferenceTree = PreferenceTree.from(allCandidates = candidateFixture.candidates.map(_.btlPosition))(ballotPapers)

    val actualResult = PerformRecount.performRecount(
      election = SenateElection.`2016`,
      state = State.ACT,
      allCandidates = candidateFixture.candidates,
      preferenceTree = preferenceTree,
      ineligibleCandidates = Set.empty,
      numVacancies = 2,
    )

    assert(actualResult.map(_.candidateOutcomeProbabilities.onlyOutcomeUnsafe.electedCandidates) === Right(DupelessSeq(
      katyGallagher,
      zedSeselja,
    )))
  }

}

package au.id.tmm.senatedb.computations.expiry

import au.id.tmm.senatedb.computations.BallotTestingUtilities
import au.id.tmm.senatedb.computations.expiry.ExhaustionCalculator.Count
import au.id.tmm.senatedb.data.TestData
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences.UsesDopData
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ExhaustionCalculationSpec extends ImprovedFlatSpec with UsesDopData {

  val sut = ExhaustionCalculator(TestData.allTasCandidates, parsedTasCountData)

  "expiry calculation" should "return the count step when a ballot expires" in {
    val exhaustingBallot = BallotTestingUtilities.normalisedBallot("UG0", "UG1", "E1", "J1", "B1", "I1")

    val Count(count, _) = sut.computeExhaustionOf(exhaustingBallot).get

    assert(count === 175)
  }

  it should "return the number of candidates elected when a ballot expires" in {
    val exhaustingBallot = BallotTestingUtilities.normalisedBallot("UG0", "UG1", "E1", "J1", "B1", "I1")

    val Count(_, candidatesElected) = sut.computeExhaustionOf(exhaustingBallot).get

    assert(candidatesElected === 8)
  }

  it should "return nothing if a ballot didn't expire" in {
    val notExhaustingBallot = BallotTestingUtilities.normalisedBallot("C0", "C1", "F0", "F1", "H0", "H1")

    val actualExpiry = sut.computeExhaustionOf(notExhaustingBallot)

    assert(actualExpiry === None)
  }
}

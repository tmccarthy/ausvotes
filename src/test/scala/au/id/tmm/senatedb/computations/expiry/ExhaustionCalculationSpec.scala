package au.id.tmm.senatedb.computations.expiry

import au.id.tmm.senatedb.computations.BallotTestingUtilities
import au.id.tmm.senatedb.computations.expiry.ExhaustionCalculator.Expiry
import au.id.tmm.senatedb.data.TestData
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences.UsesDopData
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ExhaustionCalculationSpec extends ImprovedFlatSpec with UsesDopData {

  val sut = ExhaustionCalculator(TestData.allActCandidates, parsedActCountData)

  "expiry calculation" should "return the count step when a ballot expires" in {
    val expiringBallot = BallotTestingUtilities.normalisedBallot("UG0", "UG1", "E1", "J1", "B1", "I1")

    val Expiry(count, _) = sut.computeExhaustionOf(expiringBallot).get

    assert(count === 20)
  }

  it should "return the number of candidates elected when a ballot expires" in {
    val expiringBallot = BallotTestingUtilities.normalisedBallot("UG0", "UG1", "E1", "J1", "B1", "I1")

    val Expiry(_, candidatesElected) = sut.computeExhaustionOf(expiringBallot).get

    assert(candidatesElected === 1)
  }

  it should "return nothing if a ballot didn't expire" in {
    val notExhaustingBallot = BallotTestingUtilities.normalisedBallot("C0", "C1", "F0", "F1", "H0", "H1")

    val actualExpiry = sut.computeExhaustionOf(notExhaustingBallot)

    assert(actualExpiry === None)
  }
}

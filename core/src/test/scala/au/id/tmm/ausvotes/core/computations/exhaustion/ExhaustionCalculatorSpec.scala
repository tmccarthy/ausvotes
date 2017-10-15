package au.id.tmm.ausvotes.core.computations.exhaustion

import au.id.tmm.ausvotes.core.fixtures.{BallotFixture, TestsBallotFacts}
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion
import au.id.tmm.ausvotes.core.model.parsing.Ballot
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ExhaustionCalculatorSpec extends ImprovedFlatSpec with TestsBallotFacts {

  private def exhaustionOf(ballot: Ballot) = {
    val normalisedBallot = normaliser.normalise(ballot)

    ExhaustionCalculator.exhaustionsOf(countData, Vector((ballot, normalisedBallot)))(ballot)
  }

  "the exhaustion calculator" should "correctly identify the exhaustion of a ballot" in {
    val exhaustion = exhaustionOf(BallotFixture.ACT.exhaustingBallot)

    assert(exhaustion === BallotExhaustion.Exhausted(16, 0.113066455002141d, 1))
  }

  it should "identify when a ballot did not exhaust" in {
    val exhaustion = exhaustionOf(BallotFixture.ACT.nonExhaustingBallot)

    assert(exhaustion === BallotExhaustion.NotExhausted)
  }

}

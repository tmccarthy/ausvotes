package au.id.tmm.senatedb.computations.exhaustion

import au.id.tmm.senatedb.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.senatedb.model.computation.BallotExhaustion
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ExhaustionCalculatorSpec extends ImprovedFlatSpec with TestsBallotFacts {

  private def exhaustionOf(ballot: Ballot) = {
    val normalisedBallot = normaliser.normalise(ballot)

    ExhaustionCalculator.exhaustionsOf(countData, Vector((ballot, normalisedBallot)))(ballot)
  }

  "the exhaustion calculator" should "correctly identify the exhaustion of a ballot" in {
    val exhaustion = exhaustionOf(Ballots.ACT.exhaustingBallot)

    assert(exhaustion === BallotExhaustion.Exhausted(16, 0.113066455002141d, 1))
  }

  it should "identify when a ballot did not exhaust" in {
    val exhaustion = exhaustionOf(Ballots.ACT.nonExhaustingBallot)

    assert(exhaustion === BallotExhaustion.NotExhausted)
  }

}

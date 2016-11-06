package au.id.tmm.senatedb.computations.exhaustion

import au.id.tmm.senatedb.fixtures.TestsBallotFacts
import au.id.tmm.senatedb.model.computation.BallotExhaustion
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ExhaustionCalculatorSpec extends ImprovedFlatSpec with TestsBallotFacts {

  import au.id.tmm.senatedb.fixtures.Ballots.ACT.ballotMaker
  import ballotMaker.makeBallot

  private def exhaustionOf(ballot: Ballot) = {
    val normalisedBallot = normaliser.normalise(ballot)

    ExhaustionCalculator.exhaustionsOf(countData, Vector((ballot, normalisedBallot)))(ballot)
  }

  "the exhaustion calculator" should "correctly identify the exhaustion of a ballot" in {
    val exhaustingBallot = makeBallot(
      atlPreferences = Map.empty,
      btlPreferences = ballotMaker.orderedBtlPreferences("C0", "UG1", "E1", "J1", "B1", "I1")
    )

    val exhaustion = exhaustionOf(exhaustingBallot)

    assert(exhaustion === BallotExhaustion.Exhausted(16, 0.113066455002141d, 1))
  }

  it should "identify when a ballot did not exhaust" in {
    val ballot = makeBallot(
      atlPreferences = Map.empty,
      btlPreferences = ballotMaker.orderedBtlPreferences("C0", "C1", "F0", "F1", "H0", "H1")
    )

    val exhaustion = exhaustionOf(ballot)

    assert(exhaustion === BallotExhaustion.NotExhausted)
  }

}

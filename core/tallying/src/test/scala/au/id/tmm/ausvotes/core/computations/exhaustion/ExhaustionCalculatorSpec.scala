package au.id.tmm.ausvotes.core.computations.exhaustion

import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils
import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion
import au.id.tmm.ausvotes.model.federal.senate.SenateBallot
import au.id.tmm.countstv.model.values.{Count, TransferValue}
import org.scalatest.FlatSpec

class ExhaustionCalculatorSpec extends FlatSpec {

  private def exhaustionOfBallot(ballotFactsTestUtils: BallotFactsTestUtils)(ballot: SenateBallot): BallotExhaustion = {
    val normalisedBallot = ballotFactsTestUtils.normaliser.normalise(ballot)

    val exhaustionsPerBallot = ExhaustionCalculator.exhaustionsOf(
      ballotFactsTestUtils.countData,
      Vector(ballot -> normalisedBallot),
    )

    exhaustionsPerBallot(ballot)
  }

  private def exhaustionOfActBallot(ballot: SenateBallot) = exhaustionOfBallot(BallotFactsTestUtils.ACT)(ballot)

  private def exhaustionOfWaBallot(ballot: SenateBallot) = exhaustionOfBallot(BallotFactsTestUtils.WA)(ballot)

  "the exhaustion calculator" should "correctly identify the exhaustion of a ballot" in {
    val exhaustion = exhaustionOfActBallot(BallotFixture.ACT.exhaustingBallot)

    assert(exhaustion === BallotExhaustion.Exhausted(Count(16), TransferValue(0.113066455002141d), 1))
  }

  it should "identify when a ballot did not exhaust" in {
    val exhaustion = exhaustionOfActBallot(BallotFixture.ACT.nonExhaustingBallot)

    assert(exhaustion === BallotExhaustion.NotExhausted)
  }

  it should "identify the exhaustion of a ballot that preferenced an inelligible candidate first" in {
    val exhaustion = exhaustionOfWaBallot(BallotFixture.WA.firstPreferenceIneligible)

    assert(exhaustion === BallotExhaustion.Exhausted(Count(321), TransferValue(1.0d), 8))
  }

  it should "identify the exhaustion of a ballot that preferenced an inelligible candidate second" in {
    val exhaustion = exhaustionOfWaBallot(BallotFixture.WA.secondPreferenceIneligible)

    assert(exhaustion === BallotExhaustion.Exhausted(Count(321), TransferValue(1.0d), 8))
  }

}

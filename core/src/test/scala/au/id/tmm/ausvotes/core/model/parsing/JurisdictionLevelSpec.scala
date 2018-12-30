package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.ausvotes.core.fixtures.BallotFixture
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class JurisdictionLevelSpec extends ImprovedFlatSpec {

  private val testBallot = BallotFixture.ACT.formalAtl

  "the national jurisdiction level" should "extract the senate election of a ballot" in {
    assert(JurisdictionLevel.Nation.ofBallot(testBallot) === testBallot.election.election.federalElection)
  }

  "the state jurisdiction level" should "extract the state of a ballot" in {
    assert(JurisdictionLevel.State.ofBallot(testBallot) === testBallot.jurisdiction.state)
  }

  "the division jurisdiction level" should "extract the division of a ballot" in {
    assert(JurisdictionLevel.Division.ofBallot(testBallot) === testBallot.jurisdiction.electorate)
  }

  "the vote collection point jurisdiction level" should "extract the vote collection point of a ballot" in {
    assert(JurisdictionLevel.VoteCollectionPoint.ofBallot(testBallot) === testBallot.jurisdiction.voteCollectionPoint)
  }
}

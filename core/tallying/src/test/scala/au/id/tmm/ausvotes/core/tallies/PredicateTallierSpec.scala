package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.StvBallotWithFacts
import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.core.tallies.SenateElectionTalliers.BallotGrouping
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.FederalBallotJurisdiction
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotId, SenateElectionForState}
import au.id.tmm.ausgeo.State
import org.scalatest.FlatSpec

class PredicateTallierSpec extends FlatSpec {

  import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils.ACT._

  private val testBallot = factsFor(BallotFixture.ACT.formalAtl)

  private val ballotMaker = BallotMaker(CandidateFixture.ACT)

  import ballotMaker.group

  private def groupsFor[G](ballotGrouper: BallotGrouping[G])(ballotWithFacts: StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]): Set[G] =
    ballotGrouper.groupsOf(ballotWithFacts)

  "the national count per first preference" should "produce the right tally per party" in {
    assert(groupsFor(BallotGrouping.FirstPreferencedPartyNationalEquivalent)(testBallot) === Set(Some(Party("Liberal Democratic Party"))))
  }

  "the count per state" should "produce the right tally per state" in {
    assert(groupsFor(BallotGrouping.State)(testBallot) === Set(State.ACT))
  }

  "the count per division" should "produce the right tally per division" in {
    assert(groupsFor(BallotGrouping.Division)(testBallot) === Set(DivisionFixture.ACT.CANBERRA))
  }

  "the count per polling place" should "produce the right tally per polling place" in {
    assert(groupsFor(BallotGrouping.VoteCollectionPoint)(testBallot) === Set(PollingPlaceFixture.ACT.BARTON))
  }

  "the count per first preferenced group" should "produce the right tally per state per group" in {
    assert(groupsFor(BallotGrouping.FirstPreferencedGroup)(testBallot) === Set(group("A")))
  }
}

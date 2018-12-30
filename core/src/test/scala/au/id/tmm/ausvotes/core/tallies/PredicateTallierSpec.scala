package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.BallotWithFacts
import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PredicateTallierSpec extends ImprovedFlatSpec {

  import BallotFactsTestUtils.ACT._

  private val countedBallot = BallotFixture.ACT.formalAtl
  private val notCountedBallot = BallotFixture.ACT.formalBtl

  private val ballotMaker = BallotMaker(CandidateFixture.ACT)

  import ballotMaker.group

  private object TestCounter extends BallotCounter.PredicateBallotCounter {
    override def isCounted(ballotWithFacts: BallotWithFacts): Boolean = ballotWithFacts.ballot == countedBallot

    override def name: String = "counted ballots"
  }

  private val sut = TallierBuilder.counting(TestCounter)

  private val testBallotsWithFacts = factsFor(Vector(countedBallot, notCountedBallot))

  "the national count" should "produce a simple tally" in {
    val tally = sut.overall().tally(testBallotsWithFacts)

    assert(tally === Tally0(1))
  }

  "the national count per first preference" should "produce the right tally per party" in {
    val tally = sut.groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent).tally(testBallotsWithFacts)

    assert(tally === Tally1(Some(Party("Liberal Democratic Party")) -> 1d))
  }

  "the count per state" should "produce the right tally per state" in {
    val tally = sut.groupedBy(BallotGrouping.State).tally(testBallotsWithFacts)

    assert(tally === Tally1(State.ACT -> 1d))
  }

  "the count per division" should "produce the right tally per division" in {
    val tally = sut.groupedBy(BallotGrouping.Division).tally(testBallotsWithFacts)

    assert(tally === Tally1(DivisionFixture.ACT.CANBERRA -> 1d))
  }

  "the count per polling place" should "produce the right tally per polling place" in {
    val tally = sut.groupedBy(BallotGrouping.VoteCollectionPoint).tally(testBallotsWithFacts)

    assert(tally === Tally1(PollingPlaceFixture.ACT.BARTON -> 1d))
  }

  "the count per first preferenced group" should "produce the right tally per state per group" in {
    val tieredTally = sut
      .groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedGroup)
      .tally(testBallotsWithFacts)

    assert(tieredTally === Tally2(State.ACT -> Tally1(group("A") -> 1d)))
  }
}

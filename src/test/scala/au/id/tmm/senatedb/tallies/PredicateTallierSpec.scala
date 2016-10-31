package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.fixtures._
import au.id.tmm.senatedb.model.parsing.RegisteredParty
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PredicateTallierSpec extends ImprovedFlatSpec with TestsBallotFacts {

  private val countedBallot = Ballots.ACT.formalAtl
  private val notCountedBallot = Ballots.ACT.formalBtl

  private val ballotMaker = BallotMaker(Candidates.ACT)

  import ballotMaker.group

  private object sut extends PredicateTallier {
    override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = ballotWithFacts.ballot == countedBallot
  }

  private val testBallotsWithFacts = factsFor(Vector(countedBallot, notCountedBallot))

  "the national count" should "produce a simple tally" in {
    val tally = sut.Nationally.tally(testBallotsWithFacts)

    assert(tally === SimpleTally(1))
  }

  "the national count per first preference" should "produce the right tally per party" in {
    val tally = sut.NationallyByFirstPreference.tally(testBallotsWithFacts)

    assert(tally === Tally(RegisteredParty("Liberal Democratic Party") -> 1d))
  }

  "the count per state" should "produce the right tally per state" in {
    val tally = sut.ByState.tally(testBallotsWithFacts)

    assert(tally === Tally(State.ACT -> 1d))
  }

  "the count per division" should "produce the right tally per division" in {
    val tally = sut.ByDivision.tally(testBallotsWithFacts)

    assert(tally === Tally(Divisions.ACT.CANBERRA -> 1d))
  }

  "the count per polling place" should "produce the right tally per polling place" in {
    val tally = sut.ByVoteCollectionPoint.tally(testBallotsWithFacts)

    assert(tally === Tally(PollingPlaces.ACT.BARTON -> 1d))
  }

  "the count per first preferenced group" should "produce the right tally per state per group" in {
    val tieredTally = sut.ByFirstPreferencedGroup.tally(testBallotsWithFacts)

    assert(tieredTally === TieredTally(State.ACT -> Tally(group("A") -> 1d)))
  }
}

package au.id.tmm.ausvotes.core.computations.donkeyvotes

import au.id.tmm.ausvotes.core.fixtures.{BallotFixture, BallotMaker, CandidateFixture}
import org.scalatest.FlatSpec

class DonkeyVoteDetectorSpec extends FlatSpec {

  private val ballotMaker = BallotMaker(CandidateFixture.ACT)

  "the donkey vote detector" should "identify a donkey vote" in {
    val ballot = ballotMaker.makeBallot(
      atlPreferences = ballotMaker.orderedAtlPreferences("A", "B", "C", "D", "E", "F")
    )

    assert(DonkeyVoteDetector.isDonkeyVote(ballot))
  }

  it should "identify a non-donkey vote" in {
    val ballot = ballotMaker.makeBallot(
      atlPreferences = ballotMaker.orderedAtlPreferences("A", "B", "C", "D", "J", "F")
    )

    assert(!DonkeyVoteDetector.isDonkeyVote(ballot))
  }

  it should "identify a non-donkey vote starting with group 'D'" in {
    val ballot = ballotMaker.makeBallot(
      atlPreferences = ballotMaker.orderedAtlPreferences("D", "E", "F", "G", "H", "I")
    )

    assert(!DonkeyVoteDetector.isDonkeyVote(ballot))
  }

  it should "identify a non-donkey vote starting with group 'A'" in {
    val ballot = ballotMaker.makeBallot(
      atlPreferences = ballotMaker.orderedAtlPreferences("A", "B", "C", "D", "E", "G")
    )

    assert(!DonkeyVoteDetector.isDonkeyVote(ballot))
  }

  it should "not mark as a donkey vote it less than 4 squares are numbered" in {
    val ballot = ballotMaker.makeBallot(
      atlPreferences = ballotMaker.orderedAtlPreferences("A", "B", "C")
    )

    assert(!DonkeyVoteDetector.isDonkeyVote(ballot))
  }

  it should "not mark as a donkey vote if preferences have been marked below the line" in {
    assert(!DonkeyVoteDetector.isDonkeyVote(BallotFixture.ACT.donkeyAtlFormalBtl))
  }

  it should "not count ballots using marks" in {
    assert(!DonkeyVoteDetector.isDonkeyVote(BallotFixture.ACT.tickedAtl))
  }
}

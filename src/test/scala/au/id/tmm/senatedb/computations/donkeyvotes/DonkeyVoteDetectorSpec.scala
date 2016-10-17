package au.id.tmm.senatedb.computations.donkeyvotes

import au.id.tmm.senatedb.fixtures.{BallotMaker, Candidates}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DonkeyVoteDetectorSpec extends ImprovedFlatSpec {

  private val ballotMaker = BallotMaker(Candidates.ACT)

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

  it should "not mark as a donkey vote it less than 4 squares are numbered" in {
    val ballot = ballotMaker.makeBallot(
      atlPreferences = ballotMaker.orderedAtlPreferences("A", "B", "C")
    )

    assert(!DonkeyVoteDetector.isDonkeyVote(ballot))
  }

  it should "not mark as a donkey vote if preferences have been marked below the line" in {
    val ballot = ballotMaker.makeBallot(
      atlPreferences = ballotMaker.orderedAtlPreferences("A", "B", "C", "D", "E", "F"),
      btlPreferences = ballotMaker.orderedBtlPreferences("C0", "C1", "A0", "A1", "F1", "I0")
    )

    assert(!DonkeyVoteDetector.isDonkeyVote(ballot))
  }
}

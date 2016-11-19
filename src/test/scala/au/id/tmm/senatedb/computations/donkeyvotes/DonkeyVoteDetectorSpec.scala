package au.id.tmm.senatedb.computations.donkeyvotes

import au.id.tmm.senatedb.fixtures.{BallotMaker, Ballots, Candidates}
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
    assert(!DonkeyVoteDetector.isDonkeyVote(Ballots.ACT.donkeyAtlFormalBtl))
  }

  it should "not count ballots using marks" in {
    assert(!DonkeyVoteDetector.isDonkeyVote(Ballots.ACT.tickedAtl))
  }
}

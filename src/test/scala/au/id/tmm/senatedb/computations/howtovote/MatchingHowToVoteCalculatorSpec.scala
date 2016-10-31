package au.id.tmm.senatedb.computations.howtovote

import au.id.tmm.senatedb.fixtures.{BallotMaker, Ballots, Candidates, Groups}
import au.id.tmm.senatedb.model.{HowToVoteCard, SenateElection}
import au.id.tmm.senatedb.parsing.HowToVoteCardGeneration
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class MatchingHowToVoteCalculatorSpec extends ImprovedFlatSpec {

  private val actHtvs = HowToVoteCardGeneration.from(SenateElection.`2016`, Groups.ACT.groups)
  private val sut = MatchingHowToVoteCalculator(actHtvs)

  private val ballotMaker = BallotMaker(Candidates.ACT)

  import ballotMaker.groupOrder

  "the matching how to vote card calculator" should "identify the how to vote card used" in {
    val expected = HowToVoteCard(SenateElection.`2016`, State.ACT, groupOrder("H").head,
      groupOrder("H", "B", "J", "G", "C", "E"))

    val actual = sut.findMatchingHowToVoteCard(Ballots.ACT.usesHtv)

    assert(actual contains expected)
  }

  it should "ignore ballots using the how to vote if they continue numbering" in {
    val ballot = ballotMaker.makeBallot(
      ballotMaker.orderedAtlPreferences("H", "B", "J", "G", "C", "E", "D"),
      Map.empty
    )

    val actual = sut.findMatchingHowToVoteCard(ballot)

    assert(actual === None)
  }

  it should "ignore ballots using the how to vote that also number below the line" in {
    val ballot = ballotMaker.makeBallot(
      ballotMaker.orderedAtlPreferences("H", "B", "J", "G", "C", "E", "D"),
      ballotMaker.orderedBtlPreferences("A0", "A1")
    )

    val actual = sut.findMatchingHowToVoteCard(ballot)

    assert(actual === None)
  }

  it should "ignore ballots that use a tick instead of a 1" in {
    val ballot = ballotMaker.makeBallot(
      ballotMaker.atlPreferences("H" -> "/", "B" -> "2", "J" -> "3", "G" -> "4", "C" -> "5", "E" -> "6"),
      Map.empty
    )

    val actual = sut.findMatchingHowToVoteCard(ballot)

    assert(actual === None)
  }

  it should "ignore ballots that did not use the how to vote" in {
    val ballot = ballotMaker.makeBallot(
      ballotMaker.orderedAtlPreferences("B", "J", "G", "H", "C", "E", "D"),
      ballotMaker.orderedBtlPreferences("A0", "A1")
    )

    val actual = sut.findMatchingHowToVoteCard(ballot)

    assert(actual === None)
  }
}

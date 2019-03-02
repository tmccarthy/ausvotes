package au.id.tmm.ausvotes.core.computations.howtovote

import au.id.tmm.ausvotes.core.fixtures.{BallotMaker, CandidateFixture, GroupFixture}
import au.id.tmm.ausvotes.data_sources.aec.federal.extras.htv.HowToVoteCardGeneration
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, _}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class MatchingHowToVoteCalculatorSpec extends ImprovedFlatSpec {

  private val actHtvs = HowToVoteCardGeneration.from(SenateElection.`2016`, GroupFixture.ACT.groups)
  private val sut = MatchingHowToVoteCalculator(actHtvs)

  private val candidateFixture = CandidateFixture.ACT
  private val election = candidateFixture.election
  private val ballotMaker = BallotMaker(candidateFixture)

  import ballotMaker.groupOrder

  "the matching how to vote card calculator" should "identify the how to vote card used" in {
    val expected = SenateHtv(ballotMaker.candidateFixture.election, groupOrder("H").head,
      groupOrder("H", "B", "J", "G", "C", "E"))

    val actual = sut.findMatchingHowToVoteCard(groupOrder("H", "B", "J", "G", "C", "E").toVector, election)

    assert(actual contains expected)
  }

  it should "ignore ballots using the how to vote if they continue numbering" in {
    val actual = sut.findMatchingHowToVoteCard(groupOrder("H", "B", "J", "G", "C", "E", "D").toVector, election)

    assert(actual === None)
  }

  it should "ignore ballots that did not use the how to vote" in {
    val actual = sut.findMatchingHowToVoteCard(groupOrder("B", "J", "G", "H", "C", "E", "D").toVector, election)

    assert(actual === None)
  }
}

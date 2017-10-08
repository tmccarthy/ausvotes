package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.fixtures.{BallotMaker, CandidateFixture, GroupFixture}
import au.id.tmm.ausvotes.core.model.{HowToVoteCard, SenateElection}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class HowToVoteCardGenerationSpec extends ImprovedFlatSpec {

  private val ballotMaker = BallotMaker(CandidateFixture.ACT)

  import ballotMaker.groupOrder

  "the htv card generator" should "produce the Greens ACT htv" in {
    val expected = HowToVoteCard(SenateElection.`2016`, State.ACT, groupOrder("H").head,
      groupOrder("H", "B", "J", "G", "C", "E"))

    val generatedHtvs = HowToVoteCardGeneration.from(SenateElection.`2016`, GroupFixture.ACT.groups)

    assert(generatedHtvs contains expected)
  }

  it should "fail if asked for the 2013 election" in {
    intercept[IllegalArgumentException](HowToVoteCardGeneration.from(SenateElection.`2013`, GroupFixture.ACT.groups))
  }
}

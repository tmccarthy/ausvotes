package au.id.tmm.senatedb.parsing

import au.id.tmm.senatedb.fixtures.Groups
import au.id.tmm.senatedb.model.parsing.Group
import au.id.tmm.senatedb.model.{HowToVoteCard, SenateElection}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class HowToVoteCardGenerationSpec extends ImprovedFlatSpec {

  private val groupLookup = Groups.ACT.groupLookup

  "the htv card generator" should "produce the Greens ACT htv" in {
    val expected = HowToVoteCard(SenateElection.`2016`, State.ACT, groupLookup("H").asInstanceOf[Group], Vector(
      groupLookup("H").asInstanceOf[Group],
      groupLookup("B").asInstanceOf[Group],
      groupLookup("J").asInstanceOf[Group],
      groupLookup("G").asInstanceOf[Group],
      groupLookup("C").asInstanceOf[Group],
      groupLookup("E").asInstanceOf[Group]
    ))

    val generatedHtvs = HowToVoteCardGeneration.from(SenateElection.`2016`, Groups.ACT.groups)

    assert(generatedHtvs contains expected)
  }

  it should "fail if asked for the 2013 election" in {
    intercept[IllegalArgumentException](HowToVoteCardGeneration.from(SenateElection.`2013`, Groups.ACT.groups))
  }
}

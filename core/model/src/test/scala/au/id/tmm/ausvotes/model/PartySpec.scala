package au.id.tmm.ausvotes.model

import org.scalatest.FlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class PartySpec extends FlatSpec {

  "a party" can "be encoded to json" in {
    assert(Party("Australian Labor Party").asJson === Json.fromString("Australian Labor Party"))
  }

  it can "be decoded from json" in {
    assert(Json.fromString("Australian Labor Party").as[Party] === Right(Party("Australian Labor Party")))
  }

  private def constructionSpec(partyName: String, expectedParty: Party): Unit = {
    s"""A party with name "${partyName}"""" should s"be parsed as $expectedParty" in {
      assert(Party(partyName) === expectedParty)
    }
  }

  constructionSpec("Australian Labor Party",                             expectedParty = Party.ALP)
  constructionSpec("Labor",                                              expectedParty = Party.ALP)
  constructionSpec("Australian Labor Party (Northern Territory) Branch", expectedParty = Party.ALPNTBranch)
  constructionSpec("Australian Antipaedophile Party",                    expectedParty = Party.Antipaedophile)
  constructionSpec("Antipaedophile Party",                               expectedParty = Party.Antipaedophile)
  constructionSpec("Citizens Electoral Council of Australia",            expectedParty = Party.CitizensElectoralCouncil)
  constructionSpec("Citizens Electoral Council",                         expectedParty = Party.CitizensElectoralCouncil)
  constructionSpec("Democratic Labour Party",                            expectedParty = Party.DLP)
  constructionSpec("DLP Democratic Labour",                              expectedParty = Party.DLP)
  constructionSpec("Democratic Labour Party (DLP)",                      expectedParty = Party.DLP)
  constructionSpec("Family First Party",                                 expectedParty = Party.FamilyFirst)
  constructionSpec("Family First",                                       expectedParty = Party.FamilyFirst)
  constructionSpec("The Greens",                                         expectedParty = Party.Greens)
  constructionSpec("The Greens (WA)",                                    expectedParty = Party.GreensWA)
  constructionSpec("Liberal Party of Australia",                         expectedParty = Party.Liberal)
  constructionSpec("Liberal",                                            expectedParty = Party.Liberal)
  constructionSpec("Country Liberals (NT)",                              expectedParty = Party.CountryLiberalsNT)
  constructionSpec("Liberal National Party of Queensland",               expectedParty = Party.LNP)
  constructionSpec("Liberal & Nationals",                                expectedParty = Party.LiberalWithNationals)
  constructionSpec("Liberal/The Nationals",                              expectedParty = Party.LiberalWithNationals)
  constructionSpec("The Nationals",                                      expectedParty = Party.Nationals)
  constructionSpec("Liberal Democratic Party",                           expectedParty = Party.LibDems)
  constructionSpec("Liberal Democrats",                                  expectedParty = Party.LibDems)
  constructionSpec("Pirate Party Australia",                             expectedParty = Party.Pirate)
  constructionSpec("Pirate Party",                                       expectedParty = Party.Pirate)
  constructionSpec("Science Party / Cyclists Party",                     expectedParty = Party.ScienceWithCyclists)
  constructionSpec("Science Party/Cyclists Party",                       expectedParty = Party.ScienceWithCyclists)
  constructionSpec("Australian Sex Party/Marijuana (HEMP) Party",        expectedParty = Party.SexPartyWithHempParty)
  constructionSpec("Marijuana (HEMP) Party/Australian Sex Party",        expectedParty = Party.SexPartyWithHempParty)
  constructionSpec("Pauline Hanson's One Nation",                        expectedParty = Party.OneNation)
  constructionSpec("Party Party",                                        expectedParty = Party.Other("Party Party"))
}

package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.stv.BallotGroup
import au.id.tmm.ausgeo.State
import au.id.tmm.utilities.testing.syntax._

object GroupFixture {

  trait GroupFixture {
    val senateElection: SenateElection = SenateElection.`2016`
    def state: State
    def election: SenateElectionForState = senateElection.electionForState(state).get
    def groups: Set[SenateGroup]

    lazy val ungrouped: SenateUngrouped = SenateUngrouped(election)

    lazy val groupLookup: Map[String, SenateBallotGroup] = (groups ++ Set(ungrouped)).groupBy(_.code.asString).view.mapValues(_.head).toMap
  }

  object NT extends GroupFixture {
    override val state: State = State.NT

    override val groups: Set[SenateGroup] = Set(
      SenateGroup(election, BallotGroup.Code("C").get, Some(Party("Citizens Electoral Council of Australia"))).get,
      SenateGroup(election, BallotGroup.Code("B").get, Some(Party("Marijuana (HEMP) Party/Australian Sex Party"))).get,
      SenateGroup(election, BallotGroup.Code("A").get, Some(Party("Rise Up Australia Party"))).get,
      SenateGroup(election, BallotGroup.Code("D").get, Some(Party("The Greens"))).get,
      SenateGroup(election, BallotGroup.Code("F").get, Some(Party("Australian Labor Party (Northern Territory) Branch"))).get,
      SenateGroup(election, BallotGroup.Code("E").get, Some(Party("Country Liberals (NT)"))).get,
      SenateGroup(election, BallotGroup.Code("G").get, Some(Party("Christian Democratic Party (Fred Nile Group)"))).get,
    )
  }

  object ACT extends GroupFixture {
    override val state: State = State.ACT

    override val groups: Set[SenateGroup] = Set(
      SenateGroup(election, BallotGroup.Code("A").get, Some(Party("Liberal Democratic Party"))).get,
      SenateGroup(election, BallotGroup.Code("B").get, Some(Party("Secular Party of Australia"))).get,
      SenateGroup(election, BallotGroup.Code("C").get, Some(Party("Australian Labor Party"))).get,
      SenateGroup(election, BallotGroup.Code("E").get, Some(Party("Sustainable Australia"))).get,
      SenateGroup(election, BallotGroup.Code("D").get, Some(Party("Rise Up Australia Party"))).get,
      SenateGroup(election, BallotGroup.Code("F").get, Some(Party("Liberal"))).get,
      SenateGroup(election, BallotGroup.Code("G").get, Some(Party("Animal Justice Party"))).get,
      SenateGroup(election, BallotGroup.Code("H").get, Some(Party("The Greens"))).get,
      SenateGroup(election, BallotGroup.Code("I").get, Some(Party("Christian Democratic Party (Fred Nile Group)"))).get,
      SenateGroup(election, BallotGroup.Code("J").get, Some(Party("Australian Sex Party"))).get,
    )

    val ALP_GROUP: SenateGroup = groups.find(_.party.contains(Party("Australian Labor Party"))).get
  }

  object TAS extends GroupFixture {
    override val state: State = State.TAS

    //noinspection NotImplementedCode
    override lazy val groups: Nothing = ???
  }

  object WA extends GroupFixture {
    override val state: State = State.WA

    override lazy val groups: Set[SenateGroup] = Set(
      SenateGroup(election, BallotGroup.Code("J").get, Some(Party("The Greens (WA)"))).get,
      SenateGroup(election, BallotGroup.Code("A").get, Some(Party("Christian Democratic Party (Fred Nile Group)"))).get,
      SenateGroup(election, BallotGroup.Code("D").get, Some(Party("Australian Labor Party"))).get,
      SenateGroup(election, BallotGroup.Code("N").get, Some(Party("Australian Cyclists Party"))).get,
      SenateGroup(election, BallotGroup.Code("C").get, Some(Party("Nick Xenophon Team"))).get,
      SenateGroup(election, BallotGroup.Code("AA").get, Some(Party("VOTEFLUX.ORG | Upgrade Democracy!"))).get,
      SenateGroup(election, BallotGroup.Code("Q").get, Some(Party("Rise Up Australia Party"))).get,
      SenateGroup(election, BallotGroup.Code("AB").get, Some(Party("Family First Party"))).get,
      SenateGroup(election, BallotGroup.Code("S").get, Some(Party("Australian Sex Party/Marijuana (HEMP) Party"))).get,
      SenateGroup(election, BallotGroup.Code("V").get, party = None).get,
      SenateGroup(election, BallotGroup.Code("L").get, Some(Party("Mature Australia"))).get,
      SenateGroup(election, BallotGroup.Code("X").get, Some(Party("Liberal Party of Australia"))).get,
      SenateGroup(election, BallotGroup.Code("M").get, Some(Party("The Arts Party"))).get,
      SenateGroup(election, BallotGroup.Code("Z").get, Some(Party("Liberal Democratic Party"))).get,
      SenateGroup(election, BallotGroup.Code("O").get, Some(Party("Renewable Energy Party"))).get,
      SenateGroup(election, BallotGroup.Code("T").get, Some(Party("Democratic Labour Party"))).get,
      SenateGroup(election, BallotGroup.Code("U").get, Some(Party("Health Australia Party"))).get,
      SenateGroup(election, BallotGroup.Code("H").get, Some(Party("Derryn Hinch's Justice Party"))).get,
      SenateGroup(election, BallotGroup.Code("R").get, Some(Party("Pauline Hanson's One Nation"))).get,
      SenateGroup(election, BallotGroup.Code("E").get, Some(Party("Citizens Electoral Council of Australia"))).get,
      SenateGroup(election, BallotGroup.Code("P").get, Some(Party("Australian Liberty Alliance"))).get,
      SenateGroup(election, BallotGroup.Code("B").get, Some(Party("Shooters, Fishers and Farmers"))).get,
      SenateGroup(election, BallotGroup.Code("K").get, Some(Party("Animal Justice Party"))).get,
      SenateGroup(election, BallotGroup.Code("F").get, Some(Party("The Nationals"))).get,
      SenateGroup(election, BallotGroup.Code("G").get, Some(Party("Socialist Alliance"))).get,
      SenateGroup(election, BallotGroup.Code("W").get, Some(Party("Australian Christians"))).get,
      SenateGroup(election, BallotGroup.Code("Y").get, Some(Party("Australia First Party"))).get,
      SenateGroup(election, BallotGroup.Code("I").get, Some(Party("Palmer United Party"))).get,
    )
  }
}

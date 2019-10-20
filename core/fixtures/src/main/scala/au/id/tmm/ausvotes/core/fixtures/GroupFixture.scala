package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.stv.BallotGroup
import au.id.tmm.ausgeo.State

object GroupFixture {

  trait GroupFixture {
    val senateElection: SenateElection = SenateElection.`2016`
    def state: State
    def election: SenateElectionForState = senateElection.electionForState(state).get
    def groups: Set[SenateGroup]

    lazy val ungrouped: SenateUngrouped = SenateUngrouped(election)

    lazy val groupLookup: Map[String, SenateBallotGroup] = (groups ++ Set(ungrouped)).groupBy(_.code.asString).mapValues(_.head)
  }

  object NT extends GroupFixture {
    override val state: State = State.NT

    override val groups: Set[SenateGroup] = Set(
      SenateGroup(election, BallotGroup.Code("C").right.get, Some(Party("Citizens Electoral Council of Australia"))).right.get,
      SenateGroup(election, BallotGroup.Code("B").right.get, Some(Party("Marijuana (HEMP) Party/Australian Sex Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("A").right.get, Some(Party("Rise Up Australia Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("D").right.get, Some(Party("The Greens"))).right.get,
      SenateGroup(election, BallotGroup.Code("F").right.get, Some(Party("Australian Labor Party (Northern Territory) Branch"))).right.get,
      SenateGroup(election, BallotGroup.Code("E").right.get, Some(Party("Country Liberals (NT)"))).right.get,
      SenateGroup(election, BallotGroup.Code("G").right.get, Some(Party("Christian Democratic Party (Fred Nile Group)"))).right.get,
    )
  }

  object ACT extends GroupFixture {
    override val state: State = State.ACT

    override val groups: Set[SenateGroup] = Set(
      SenateGroup(election, BallotGroup.Code("A").right.get, Some(Party("Liberal Democratic Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("B").right.get, Some(Party("Secular Party of Australia"))).right.get,
      SenateGroup(election, BallotGroup.Code("C").right.get, Some(Party("Australian Labor Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("E").right.get, Some(Party("Sustainable Australia"))).right.get,
      SenateGroup(election, BallotGroup.Code("D").right.get, Some(Party("Rise Up Australia Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("F").right.get, Some(Party("Liberal"))).right.get,
      SenateGroup(election, BallotGroup.Code("G").right.get, Some(Party("Animal Justice Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("H").right.get, Some(Party("The Greens"))).right.get,
      SenateGroup(election, BallotGroup.Code("I").right.get, Some(Party("Christian Democratic Party (Fred Nile Group)"))).right.get,
      SenateGroup(election, BallotGroup.Code("J").right.get, Some(Party("Australian Sex Party"))).right.get,
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
      SenateGroup(election, BallotGroup.Code("J").right.get, Some(Party("The Greens (WA)"))).right.get,
      SenateGroup(election, BallotGroup.Code("A").right.get, Some(Party("Christian Democratic Party (Fred Nile Group)"))).right.get,
      SenateGroup(election, BallotGroup.Code("D").right.get, Some(Party("Australian Labor Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("N").right.get, Some(Party("Australian Cyclists Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("C").right.get, Some(Party("Nick Xenophon Team"))).right.get,
      SenateGroup(election, BallotGroup.Code("AA").right.get, Some(Party("VOTEFLUX.ORG | Upgrade Democracy!"))).right.get,
      SenateGroup(election, BallotGroup.Code("Q").right.get, Some(Party("Rise Up Australia Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("AB").right.get, Some(Party("Family First Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("S").right.get, Some(Party("Australian Sex Party/Marijuana (HEMP) Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("V").right.get, party = None).right.get,
      SenateGroup(election, BallotGroup.Code("L").right.get, Some(Party("Mature Australia"))).right.get,
      SenateGroup(election, BallotGroup.Code("X").right.get, Some(Party("Liberal Party of Australia"))).right.get,
      SenateGroup(election, BallotGroup.Code("M").right.get, Some(Party("The Arts Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("Z").right.get, Some(Party("Liberal Democratic Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("O").right.get, Some(Party("Renewable Energy Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("T").right.get, Some(Party("Democratic Labour Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("U").right.get, Some(Party("Health Australia Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("H").right.get, Some(Party("Derryn Hinch's Justice Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("R").right.get, Some(Party("Pauline Hanson's One Nation"))).right.get,
      SenateGroup(election, BallotGroup.Code("E").right.get, Some(Party("Citizens Electoral Council of Australia"))).right.get,
      SenateGroup(election, BallotGroup.Code("P").right.get, Some(Party("Australian Liberty Alliance"))).right.get,
      SenateGroup(election, BallotGroup.Code("B").right.get, Some(Party("Shooters, Fishers and Farmers"))).right.get,
      SenateGroup(election, BallotGroup.Code("K").right.get, Some(Party("Animal Justice Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("F").right.get, Some(Party("The Nationals"))).right.get,
      SenateGroup(election, BallotGroup.Code("G").right.get, Some(Party("Socialist Alliance"))).right.get,
      SenateGroup(election, BallotGroup.Code("W").right.get, Some(Party("Australian Christians"))).right.get,
      SenateGroup(election, BallotGroup.Code("Y").right.get, Some(Party("Australia First Party"))).right.get,
      SenateGroup(election, BallotGroup.Code("I").right.get, Some(Party("Palmer United Party"))).right.get,
    )
  }
}

package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Party.{Independent, RegisteredParty}
import au.id.tmm.ausvotes.core.model.parsing.{BallotGroup, Group, Ungrouped}
import au.id.tmm.utilities.geo.australia.State

object GroupFixture {

  trait GroupFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def groups: Set[Group]

    lazy val ungrouped = Ungrouped(election, state)

    lazy val groupLookup: Map[String, BallotGroup] = (groups ++ Set(ungrouped)).groupBy(_.code).mapValues(_.head)
  }

  object NT extends GroupFixture {
    override val state: State = State.NT

    override val groups = Set(
      Group(election, state, "C", RegisteredParty("Citizens Electoral Council of Australia")),
      Group(election, state, "B", RegisteredParty("Marijuana (HEMP) Party/Australian Sex Party")),
      Group(election, state, "A", RegisteredParty("Rise Up Australia Party")),
      Group(election, state, "D", RegisteredParty("The Greens")),
      Group(election, state, "F", RegisteredParty("Australian Labor Party (Northern Territory) Branch")),
      Group(election, state, "E", RegisteredParty("Country Liberals (NT)")),
      Group(election, state, "G", RegisteredParty("Christian Democratic Party (Fred Nile Group)"))
    )
  }

  object ACT extends GroupFixture {
    override val state: State = State.ACT

    override val groups = Set(
      Group(election, state, "A", RegisteredParty("Liberal Democratic Party")),
      Group(election, state, "B", RegisteredParty("Secular Party of Australia")),
      Group(election, state, "C", RegisteredParty("Australian Labor Party")),
      Group(election, state, "E", RegisteredParty("Sustainable Australia")),
      Group(election, state, "D", RegisteredParty("Rise Up Australia Party")),
      Group(election, state, "F", RegisteredParty("Liberal")),
      Group(election, state, "G", RegisteredParty("Animal Justice Party")),
      Group(election, state, "H", RegisteredParty("The Greens")),
      Group(election, state, "I", RegisteredParty("Christian Democratic Party (Fred Nile Group)")),
      Group(election, state, "J", RegisteredParty("Australian Sex Party"))
    )

    val ALP_GROUP: Group = groups.find(_.party == RegisteredParty("Australian Labor Party")).get
  }

  object TAS extends GroupFixture {
    override val state: State = State.TAS

    override lazy val groups: Nothing = ???
  }

  object WA extends GroupFixture {
    override val state: State = State.WA

    override lazy val groups: Set[Group] = Set(
      Group(election, state, "J", RegisteredParty("The Greens (WA)")),
      Group(election, state, "A", RegisteredParty("Christian Democratic Party (Fred Nile Group)")),
      Group(election, state, "D", RegisteredParty("Australian Labor Party")),
      Group(election, state, "N", RegisteredParty("Australian Cyclists Party")),
      Group(election, state, "C", RegisteredParty("Nick Xenophon Team")),
      Group(election, state, "AA", RegisteredParty("VOTEFLUX.ORG | Upgrade Democracy!")),
      Group(election, state, "Q", RegisteredParty("Rise Up Australia Party")),
      Group(election, state, "AB", RegisteredParty("Family First Party")),
      Group(election, state, "S", RegisteredParty("Australian Sex Party/Marijuana (HEMP) Party")),
      Group(election, state, "V", Independent),
      Group(election, state, "L", RegisteredParty("Mature Australia")),
      Group(election, state, "X", RegisteredParty("Liberal Party of Australia")),
      Group(election, state, "M", RegisteredParty("The Arts Party")),
      Group(election, state, "Z", RegisteredParty("Liberal Democratic Party")),
      Group(election, state, "O", RegisteredParty("Renewable Energy Party")),
      Group(election, state, "T", RegisteredParty("Democratic Labour Party")),
      Group(election, state, "U", RegisteredParty("Health Australia Party")),
      Group(election, state, "H", RegisteredParty("Derryn Hinch's Justice Party")),
      Group(election, state, "R", RegisteredParty("Pauline Hanson's One Nation")),
      Group(election, state, "E", RegisteredParty("Citizens Electoral Council of Australia")),
      Group(election, state, "P", RegisteredParty("Australian Liberty Alliance")),
      Group(election, state, "B", RegisteredParty("Shooters, Fishers and Farmers")),
      Group(election, state, "K", RegisteredParty("Animal Justice Party")),
      Group(election, state, "F", RegisteredParty("The Nationals")),
      Group(election, state, "G", RegisteredParty("Socialist Alliance")),
      Group(election, state, "W", RegisteredParty("Australian Christians")),
      Group(election, state, "Y", RegisteredParty("Australia First Party")),
      Group(election, state, "I", RegisteredParty("Palmer United Party")),
    )
  }
}

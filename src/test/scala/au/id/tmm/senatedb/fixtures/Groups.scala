package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.{BallotGroup, Group, Party}
import au.id.tmm.utilities.geo.australia.State

object Groups {

  trait GroupFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def groups: Set[Group]

    lazy val groupLookup: Map[String, BallotGroup] = BallotGroup.lookupFrom(groups)

    def party(name: String) = Party(election, name)
  }

  object NT extends GroupFixture {
    override val state = State.NT

    override val groups = Set(
      Group(election, state, "C", Some(party("Citizens Electoral Council of Australia"))),
      Group(election, state, "B", Some(party("Marijuana (HEMP) Party/Australian Sex Party"))),
      Group(election, state, "A", Some(party("Rise Up Australia Party"))),
      Group(election, state, "D", Some(party("The Greens"))),
      Group(election, state, "F", Some(party("Australian Labor Party (Northern Territory) Branch"))),
      Group(election, state, "E", Some(party("Country Liberals (NT)"))),
      Group(election, state, "G", Some(party("Christian Democratic Party (Fred Nile Group)")))
    )
  }

  object ACT extends GroupFixture {
    override val state = State.ACT

    override val groups = Set(
      Group(election, state, "A", Some(party("Liberal Democratic Party"))),
      Group(election, state, "B", Some(party("Secular Party of Australia"))),
      Group(election, state, "C", Some(party("Australian Labor Party"))),
      Group(election, state, "E", Some(party("Sustainable Australia"))),
      Group(election, state, "D", Some(party("Rise Up Australia Party"))),
      Group(election, state, "F", Some(party("Liberal"))),
      Group(election, state, "G", Some(party("Animal Justice Party"))),
      Group(election, state, "H", Some(party("The Greens"))),
      Group(election, state, "I", Some(party("Christian Democratic Party (Fred Nile Group)"))),
      Group(election, state, "J", Some(party("Australian Sex Party")))
    )
  }

  object TAS extends GroupFixture {
    override val state = State.TAS

    override lazy val groups = ???
  }
}

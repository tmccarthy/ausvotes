package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.{BallotGroup, Group, RegisteredParty}
import au.id.tmm.utilities.geo.australia.State

object Groups {

  trait GroupFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def groups: Set[Group]

    lazy val groupLookup: Map[String, BallotGroup] = BallotGroup.lookupFrom(groups)
  }

  object NT extends GroupFixture {
    override val state = State.NT

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
    override val state = State.ACT

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
  }

  object TAS extends GroupFixture {
    override val state = State.TAS

    override lazy val groups = ???
  }
}

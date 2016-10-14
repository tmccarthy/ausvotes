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
  }

  object NT extends GroupFixture {
    override val state = State.NT

    override val groups = Set(
      Group(election, state, "C", Some(Party("Citizens Electoral Council of Australia"))),
      Group(election, state, "B", Some(Party("Marijuana (HEMP) Party/Australian Sex Party"))),
      Group(election, state, "A", Some(Party("Rise Up Australia Party"))),
      Group(election, state, "D", Some(Party("The Greens"))),
      Group(election, state, "F", Some(Party("Australian Labor Party (Northern Territory) Branch"))),
      Group(election, state, "E", Some(Party("Country Liberals (NT)"))),
      Group(election, state, "G", Some(Party("Christian Democratic Party (Fred Nile Group)")))
    )
  }

  object ACT extends GroupFixture {
    override val state = State.ACT

    override val groups = Set(
      Group(election, state, "A", Some(Party("Liberal Democratic Party"))),
      Group(election, state, "B", Some(Party("Secular Party of Australia"))),
      Group(election, state, "C", Some(Party("Australian Labor Party"))),
      Group(election, state, "E", Some(Party("Sustainable Australia"))),
      Group(election, state, "D", Some(Party("Rise Up Australia Party"))),
      Group(election, state, "F", Some(Party("Liberal"))),
      Group(election, state, "G", Some(Party("Animal Justice Party"))),
      Group(election, state, "H", Some(Party("The Greens"))),
      Group(election, state, "I", Some(Party("Christian Democratic Party (Fred Nile Group)"))),
      Group(election, state, "J", Some(Party("Australian Sex Party")))
    )
  }

  object TAS extends GroupFixture {
    override val state = State.TAS

    override lazy val groups = ???
  }
}

package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Division
import au.id.tmm.utilities.geo.australia.State

object Divisions {

  trait DivisionFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def divisions: Set[Division]

    lazy val divisionLookup = divisions
      .groupBy(_.name)
      .mapValues(_.head)
  }

  object ACT extends DivisionFixture {
    override val state = State.ACT

    override val divisions = Set(
      Division(election, state, "Canberra", 101),
      Division(election, state, "Fenner", 102)
    )

    val CANBERRA: Division = divisionLookup("Canberra")
  }
}

package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Division
import au.id.tmm.utilities.geo.australia.State

object DivisionFixture {

  trait DivisionFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def divisions: Set[Division]

    lazy val divisionLookup: Map[String, Division] = divisions
      .groupBy(_.name)
      .mapValues(_.head)
  }

  object ACT extends DivisionFixture {
    override val state: State = State.ACT

    override val divisions = Set(
      Division(election, state, "Canberra", 101),
      Division(election, state, "Fenner", 102)
    )

    val CANBERRA: Division = divisionLookup("Canberra")
  }

  object NT extends DivisionFixture {
    override val state: State = State.NT

    val LINGIARI = Division(election, state, "Lingiari", 306)
    val SOLOMON = Division(election, state, "Solomon", 307)

    override val divisions = Set(
      LINGIARI,
      SOLOMON
    )
  }

  object WA extends DivisionFixture {
    override val state: State = State.WA

    val PERTH = Division(election, state, "Perth", 245)

    override def divisions: Set[Division] = Set(
      PERTH,
    )
  }
}

package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.model.federal.{Division, FederalElection}
import au.id.tmm.ausgeo.State

object DivisionFixture {

  trait DivisionFixture {
    val election: FederalElection = FederalElection.`2016`
    def state: State
    def divisions: Set[Division]

    lazy val divisionLookup: Map[String, Division] = divisions
      .groupBy(_.name)
      .view
      .mapValues(_.head)
      .toMap
  }

  object ACT extends DivisionFixture {
    override val state: State = State.ACT

    override val divisions: Set[Division] = Set(
      Division(election, state, "Canberra"),
      Division(election, state, "Fenner"),
    )

    val CANBERRA: Division = divisionLookup("Canberra")
  }

  object NT extends DivisionFixture {
    override val state: State = State.NT

    val LINGIARI: Division = Division(election, state, "Lingiari")
    val SOLOMON: Division = Division(election, state, "Solomon")

    override val divisions: Set[Division] = Set(
      LINGIARI,
      SOLOMON
    )
  }

  object WA extends DivisionFixture {
    override val state: State = State.WA

    val PERTH: Division = Division(election, state, "Perth")

    override def divisions: Set[Division] = Set(
      PERTH,
    )
  }
}

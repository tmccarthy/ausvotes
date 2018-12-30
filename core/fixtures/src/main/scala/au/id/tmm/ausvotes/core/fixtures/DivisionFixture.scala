package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.model.Electorate
import au.id.tmm.ausvotes.model.federal.{Division, FederalElection}
import au.id.tmm.utilities.geo.australia.State

object DivisionFixture {

  trait DivisionFixture {
    val election: FederalElection = FederalElection.`2016`
    def state: State
    def divisions: Set[Division]

    lazy val divisionLookup: Map[String, Division] = divisions
      .groupBy(_.name)
      .mapValues(_.head)
  }

  object ACT extends DivisionFixture {
    override val state: State = State.ACT

    override val divisions: Set[Division] = Set(
      Division(election, state, "Canberra", Electorate.Id(101)),
      Division(election, state, "Fenner", Electorate.Id(102)),
    )

    val CANBERRA: Division = divisionLookup("Canberra")
  }

  object NT extends DivisionFixture {
    override val state: State = State.NT

    val LINGIARI: Division = Division(election, state, "Lingiari", Electorate.Id(306))
    val SOLOMON: Division = Division(election, state, "Solomon", Electorate.Id(307))

    override val divisions: Set[Division] = Set(
      LINGIARI,
      SOLOMON
    )
  }

  object WA extends DivisionFixture {
    override val state: State = State.WA

    val PERTH: Division = Division(election, state, "Perth", Electorate.Id(245))

    override def divisions: Set[Division] = Set(
      PERTH,
    )
  }
}

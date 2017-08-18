package au.id.tmm.senatedb.api.persistence.daos.rowentities

import au.id.tmm.senatedb.api.persistence.entities.stats.{Stat, StatClass}
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Division, JurisdictionLevel}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class StatRowSpec extends ImprovedFlatSpec {

  "A stat row" can "be converted to a Stat instance" in {
    val statRow = StatRow(
      id = 1,
      statClass = StatClass.FormalBallots,
      election = SenateElection.`2016`,
      state = Some(State.SA),
      division = Some(DivisionRow(
        id = 1,
        election = SenateElection.`2016`,
        aecId = 1,
        state = State.SA,
        name = "Mayo",
      )),
      vcp = None,
      amount = 42d,
      perCapita = Some(42d),
    )

    val actualStat: Stat[JurisdictionLevel[Division]] = statRow.asStat

    val expectedStat = Stat(
      statClass = StatClass.FormalBallots,
      jurisdictionLevel = JurisdictionLevel.Division,
    )(
      jurisdiction = Division(
        election = SenateElection.`2016`,
        state = State.SA,
        name = "Mayo",
        aecId = 1
      ),
      amount = 42d,
      rankPerJurisdictionLevel = Map(),
      perCapita = Some(42d),
      rankPerCapitaPerJurisdictionLevel = Map(),
    )

    assert(expectedStat === actualStat)
  }

}

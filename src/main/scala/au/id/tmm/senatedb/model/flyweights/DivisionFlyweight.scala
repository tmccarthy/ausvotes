package au.id.tmm.senatedb.model.flyweights

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Division
import au.id.tmm.utilities.collection.Flyweight
import au.id.tmm.utilities.geo.australia.State

final class DivisionFlyweight private () {
  private val flyweight: Flyweight[(SenateElection, State, String), Division] = Flyweight {
    case (election, state, name) => Division(election, state, name)
  }

  def apply(election: SenateElection, state: State, name: String): Division = flyweight(election, state, name)
}

object DivisionFlyweight {
  def apply(): DivisionFlyweight = new DivisionFlyweight()
}
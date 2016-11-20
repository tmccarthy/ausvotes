package au.id.tmm.senatedb.core.model.flyweights

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.utilities.collection.Flyweight
import au.id.tmm.utilities.geo.australia.State

final class DivisionFlyweight private () {
  private val flyweight: Flyweight[(SenateElection, State, String, Int), Division] = Flyweight {
    case (election, state, name, aecId) => Division(election, state, name, aecId)
  }

  def apply(election: SenateElection, state: State, name: String, aecId: Int): Division =
    flyweight(election, state, name, aecId)
}

object DivisionFlyweight {
  def apply(): DivisionFlyweight = new DivisionFlyweight()
}
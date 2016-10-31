package au.id.tmm.senatedb.model.flyweights

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.{Group, Party}
import au.id.tmm.utilities.collection.Flyweight
import au.id.tmm.utilities.geo.australia.State

final class GroupFlyweight private () {
  private val flyweight: Flyweight[(SenateElection, State, String, Party), Group] = Flyweight {
    case (election, state, code, party) => Group(election, state, code, party)
  }

  def apply(election: SenateElection, state: State, code: String, party: Party) =
    flyweight(election, state, code, party)
}

object GroupFlyweight {
  def apply(): GroupFlyweight = new GroupFlyweight()
}
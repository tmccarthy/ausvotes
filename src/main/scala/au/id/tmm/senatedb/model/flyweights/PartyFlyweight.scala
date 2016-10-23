package au.id.tmm.senatedb.model.flyweights

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Party
import au.id.tmm.utilities.collection.Flyweight

final class PartyFlyweight private () {
  private val flyweight: Flyweight[(SenateElection, String), Party] = Flyweight {
    case (election, name) => Party(election, name).canonicalise
  }

  def apply(election: SenateElection, partyName: String): Party = flyweight((election, partyName))
}

object PartyFlyweight {
  def apply(): PartyFlyweight = new PartyFlyweight()
}
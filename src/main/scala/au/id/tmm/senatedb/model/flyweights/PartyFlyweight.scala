package au.id.tmm.senatedb.model.flyweights

import au.id.tmm.senatedb.model.parsing.Party
import au.id.tmm.utilities.collection.Flyweight

final class PartyFlyweight private () {
  private val flyweight: Flyweight[String, Party] = Flyweight(Party)

  def apply(partyName: String): Party = flyweight(partyName)
}

object PartyFlyweight {
  def apply(): PartyFlyweight = new PartyFlyweight()
}
package au.id.tmm.senatedb.core.model.flyweights

import au.id.tmm.senatedb.core.model.parsing.Party.RegisteredParty
import au.id.tmm.utilities.collection.Flyweight

final class RegisteredPartyFlyweight private() {
  private val flyweight: Flyweight[String, RegisteredParty] = Flyweight(name => RegisteredParty(name).canonicalise)

  def apply(partyName: String): RegisteredParty = flyweight(partyName)
}

object RegisteredPartyFlyweight {
  def apply(): RegisteredPartyFlyweight = new RegisteredPartyFlyweight()
}
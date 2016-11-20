package au.id.tmm.senatedb.core.model.flyweights

import au.id.tmm.utilities.collection.Flyweight
import au.id.tmm.utilities.geo.australia.Postcode

final class PostcodeFlyweight private () {
  private val flyweight: Flyweight[String, Postcode] = Flyweight(Postcode)
  
  def apply(rawPostcode: String): Postcode = flyweight(rawPostcode)
}

object PostcodeFlyweight {
  def apply(): PostcodeFlyweight = new PostcodeFlyweight()
}
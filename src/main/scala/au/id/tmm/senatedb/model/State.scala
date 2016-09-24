package au.id.tmm.senatedb.model

final case class State private(fullName: String, shortName: String,
                               isTerritory: Boolean = false, numSenators: Int = 12) extends Ordered[State] {
  override def toString: String = shortName

  override def compare(that: State): Int = State.ordering.compare(this, that)
}

object State {
  val NSW = State("New South Wales", "NSW")
  val QLD = State("Queensland", "QLD")
  val SA = State("South Australia", "SA")
  val TAS = State("Tasmania", "TAS")
  val VIC = State("Victoria", "VIC")
  val WA = State("Western Australia", "WA")

  val NT = State("Northern Territory", "NT", isTerritory = true, numSenators = 2)
  val ACT = State("Australian Capital Territory", "ACT", isTerritory = true, numSenators = 2)

  val ALL_STATES = Set(NSW, QLD, SA, TAS, VIC, WA, NT, ACT)

  def fromShortName(shortName: String): Option[State] = ALL_STATES.find(_.shortName == shortName.trim.toUpperCase)

  private val ordering: Ordering[State] = Ordering.by(_.fullName)
}
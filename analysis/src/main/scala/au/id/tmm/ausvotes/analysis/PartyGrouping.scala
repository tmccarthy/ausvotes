package au.id.tmm.ausvotes.analysis

import au.id.tmm.ausvotes.model.Party

sealed trait PartyGrouping {
  def name: String
}

object PartyGrouping {

  final case class SignificantParty(party: Party) extends PartyGrouping {
    override def name: String = party.name
  }

  case object OtherParty extends PartyGrouping {
    override val name: String = "Other"
  }

  def of(significant: Party => Boolean)(party: Party): PartyGrouping =
    if (significant(party)) {
      PartyGrouping.SignificantParty(party)
    } else {
      PartyGrouping.OtherParty
    }

}

package au.id.tmm.senatedb.model

import au.id.tmm.senatedb.model.parsing.Party
import au.id.tmm.senatedb.model.parsing.Party.RegisteredParty

sealed trait PartySignificance {

}

object PartySignificance {
  case object MajorParty extends PartySignificance

  case object MinorParty extends PartySignificance

  case object Independent extends PartySignificance

  def of(party: Party): PartySignificance = party match {
    case p: RegisteredParty => {
      val nationalEquivalent = p.nationalEquivalent

      val isMajorParty = nationalEquivalent == RegisteredParty.ALP ||
        nationalEquivalent == RegisteredParty.THE_GREENS ||
        p.inTheCoalition

      if (isMajorParty) {
        MajorParty
      } else {
        MinorParty
      }
    }
    case Party.Independent => Independent
  }
}
package au.id.tmm.ausvotes.model

sealed trait PartySignificance

object PartySignificance {
  case object Major extends PartySignificance
  case object Minor extends PartySignificance
  case object Independent extends PartySignificance
}

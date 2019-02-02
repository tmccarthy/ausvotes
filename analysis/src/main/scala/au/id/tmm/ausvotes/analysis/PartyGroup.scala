package au.id.tmm.ausvotes.analysis

import au.id.tmm.ausvotes.model.Party

sealed abstract class PartyGroup(val name: String, val colourName: String)

object PartyGroup {

  case object Coalition extends PartyGroup("Coalition", "blue")
  case object Labor extends PartyGroup("Labor", "red")
  case object Greens extends PartyGroup("Greens", "green")
  case object OneNation extends PartyGroup("One Nation", "orange")
  case object Other extends PartyGroup("Other", "gray")

  def from(party: Option[Party]): PartyGroup = party.map {
    case Party.ALP     | Party.ALPNTBranch => Labor
    case Party.Greens  | Party.GreensWA    => Greens
    case Party.Liberal | Party.CountryLiberalsNT | Party.LNP | Party.LiberalWithNationals | Party.Nationals => Coalition
    case Party.OneNation => OneNation
    case _ => Other
  }.getOrElse(Other)

  implicit val ordering: Ordering[PartyGroup] = Ordering.by {
    case Coalition => 0
    case Labor => 1
    case Greens => 2
    case OneNation => 3
    case Other => 4
  }

  val all: List[PartyGroup] = List[PartyGroup](Coalition, Labor, Greens, OneNation, Other).sorted

}


package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.Codecs.Codec

sealed abstract class Party {
  def name: String
}

object Party {

  def apply(name: String): Party = name match {
    case ALP.name | "Labor" => ALP
    case ALPNTBranch.name => ALPNTBranch
    case Antipaedophile.name | "Antipaedophile Party" => Antipaedophile
    case CitizensElectoralCouncil.name | "Citizens Electoral Council" => CitizensElectoralCouncil
    case DLP.name | "DLP Democratic Labour" | "Democratic Labour Party (DLP)" => DLP
    case FamilyFirst.name | "Family First" => FamilyFirst
    case Greens.name => Greens
    case GreensWA.name => GreensWA
    case Liberal.name | "Liberal" => Liberal
    case CountryLiberalsNT.name => CountryLiberalsNT
    case LNP.name => LNP
    case LiberalWithNationals.name | "Liberal/The Nationals" => LiberalWithNationals
    case Nationals.name => Nationals
    case LibDems.name | "Liberal Democrats" => LibDems
    case Pirate.name | "Pirate Party" => Pirate
    case ScienceWithCyclists.name | "Science Party/Cyclists Party" => ScienceWithCyclists
    case SexPartyWithHempParty.name | "Marijuana (HEMP) Party/Australian Sex Party" => SexPartyWithHempParty
    case otherName => Party.Other(otherName)
  }

  implicit val codec: Codec[Party] = Codecs.simpleCodec[Party, String](encode = _.name, decode = Party(_))

  case object ALP                      extends Party { override val name: String = "Australian Labor Party" }
  case object ALPNTBranch              extends Party { override val name: String = "Australian Labor Party (Northern Territory) Branch" }
  case object Antipaedophile           extends Party { override val name: String = "Australian Antipaedophile Party" }
  case object CitizensElectoralCouncil extends Party { override val name: String = "Citizens Electoral Council of Australia" }
  case object DLP                      extends Party { override val name: String = "Democratic Labour Party" }
  case object FamilyFirst              extends Party { override val name: String = "Family First Party" }
  case object Greens                   extends Party { override val name: String = "The Greens" }
  case object GreensWA                 extends Party { override val name: String = "The Greens (WA)" }
  case object Liberal                  extends Party { override val name: String = "Liberal Party of Australia" }
  case object CountryLiberalsNT        extends Party { override val name: String = "Country Liberals (NT)" }
  case object LNP                      extends Party { override val name: String = "Liberal National Party of Queensland" }
  case object LiberalWithNationals     extends Party { override val name: String = "Liberal & Nationals" }
  case object Nationals                extends Party { override val name: String = "The Nationals" }
  case object LibDems                  extends Party { override val name: String = "Liberal Democratic Party" }
  case object Pirate                   extends Party { override val name: String = "Pirate Party Australia" }
  case object ScienceWithCyclists      extends Party { override val name: String = "Science Party / Cyclists Party" }
  case object SexPartyWithHempParty    extends Party { override val name: String = "Australian Sex Party/Marijuana (HEMP) Party" }
  final case class Other(override val name: String) extends Party

}

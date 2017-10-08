package au.id.tmm.ausvotes.core.model.parsing

sealed trait Party {
  def nationalEquivalent: Party
}

object Party {

  case object Independent extends Party {
    val nationalEquivalent = this
  }

  final case class RegisteredParty(name: String) extends Party {

    import RegisteredParty._

    val canonicalise: RegisteredParty = {
      name match {
        case "Labor" => ALP
        case "Antipaedophile Party" => RegisteredParty("Australian Antipaedophile Party")
        case "Citizens Electoral Council" => RegisteredParty("Citizens Electoral Council of Australia")
        case "DLP Democratic Labour" |
             "Democratic Labour Party (DLP)" => RegisteredParty("Democratic Labour Party")
        case "Family First" => RegisteredParty("Family First Party")
        case "Liberal" => LIBERAL_PARTY_OF_AUSTRALIA
        case "Liberal/The Nationals" => RegisteredParty("Liberal & Nationals")
        case "Liberal Democrats" => RegisteredParty("Liberal Democratic Party")
        case "Pirate Party" => RegisteredParty("Pirate Party Australia")
        case "Science Party/Cyclists Party" => RegisteredParty("Science Party / Cyclists Party")
        case "Marijuana (HEMP) Party/Australian Sex Party" => RegisteredParty("Australian Sex Party/Marijuana (HEMP) Party")
        case _ => this
      }
    }

    //noinspection NoTailRecursionAnnotation
    override val nationalEquivalent: RegisteredParty = {
      val canonicalParty = this.canonicalise

      if (canonicalParty != this) {
        canonicalParty.nationalEquivalent
      } else {
        name match {
          case "Australian Labor Party (Northern Territory) Branch" => ALP
          case "Liberal National Party of Queensland" |
               "Country Liberals (NT)" |
               "Liberal & Nationals" => LIBERAL_PARTY_OF_AUSTRALIA
          case "The Greens (WA)" => THE_GREENS
          case _ => this
        }
      }
    }

    def inTheCoalition: Boolean = coalitionPartiesNationally contains nationalEquivalent
  }

  object RegisteredParty {
    val LIBERAL_PARTY_OF_AUSTRALIA = RegisteredParty("Liberal Party of Australia")
    val THE_GREENS = RegisteredParty("The Greens")
    val ALP = RegisteredParty("Australian Labor Party")
    val THE_NATIONALS = RegisteredParty("The Nationals")

    private val coalitionPartiesNationally = Set(
      RegisteredParty("Liberal Party of Australia"),
      THE_NATIONALS
    )
  }

}
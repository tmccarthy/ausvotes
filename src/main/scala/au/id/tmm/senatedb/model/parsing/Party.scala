package au.id.tmm.senatedb.model.parsing

sealed trait Party {
  def nationalEquivalent: Party
}

case object Independent extends Party {
  val nationalEquivalent = this
}

final case class RegisteredParty(name: String) extends Party {
  val canonicalise: RegisteredParty = {
        name match {
          case "Labor"                                       => RegisteredParty("Australian Labor Party")
          case "Antipaedophile Party"                        => RegisteredParty("Australian Antipaedophile Party")
          case "Citizens Electoral Council"                  => RegisteredParty("Citizens Electoral Council of Australia")
          case "DLP Democratic Labour" |
               "Democratic Labour Party (DLP)"               => RegisteredParty("Democratic Labour Party")
          case "Family First"                                => RegisteredParty("Family First Party")
          case "Liberal"                                     => RegisteredParty("Liberal Party of Australia")
          case "Liberal/The Nationals"                       => RegisteredParty("Liberal & Nationals")
          case "Liberal Democrats"                           => RegisteredParty("Liberal Democratic Party")
          case "Pirate Party"                                => RegisteredParty("Pirate Party Australia")
          case "Science Party/Cyclists Party"                => RegisteredParty("Science Party / Cyclists Party")
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
        case "Australian Labor Party (Northern Territory) Branch" => RegisteredParty("Australian Labor Party")
        case "Liberal National Party of Queensland" |
             "Country Liberals (NT)" |
             "Liberal & Nationals" => RegisteredParty("Liberal Party of Australia")
        case "The Greens (WA)" => RegisteredParty("The Greens")
        case _ => this
      }
    }
  }
}

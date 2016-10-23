package au.id.tmm.senatedb.model.parsing

import au.id.tmm.senatedb.model.SenateElection

// TODO represent independents in a union type
final case class Party private (election: SenateElection, name: String) {
  def canonicalise: Party = Party.canonicalPartyFor(election, name)
}

object Party {
  private val canonicalNameLookup: Map[String, String] = Map(
    "Australian Labor Party (Northern Territory) Branch" -> "Australian Labor Party",
    "Labor" -> "Australian Labor Party",
    "Antipaedophile Party" -> "Australian Antipaedophile Party",
    "Citizens Electoral Council" -> "Citizens Electoral Council of Australia",
    "DLP Democratic Labour" -> "Democratic Labour Party",
    "Democratic Labour Party (DLP)" -> "Democratic Labour Party",
    "Family First" -> "Family First Party",
    "Liberal" -> "Liberal Party of Australia",
    "Liberal & Nationals" -> "Liberal Party of Australia",
    "Liberal National Party of Queensland" -> "Liberal Party of Australia",
    "Liberal/The Nationals" -> "Liberal Party of Australia",
    "Liberal Democrats" -> "Liberal Democratic Party",
    "Pirate Party" -> "Pirate Party Australia",
    "Science Party/Cyclists Party" -> "Science Party / Cyclists Party",
    "Marijuana (HEMP) Party/Australian Sex Party" -> "Australian Sex Party/Marijuana (HEMP) Party",
    "The Greens (WA)" -> "The Greens"
  )

  def canonicalPartyFor(election: SenateElection, name: String): Party = {
    val canonicalName = canonicalNameLookup.getOrElse(name, name)

    Party(election, canonicalName)
  }
}

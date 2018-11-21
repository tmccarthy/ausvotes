package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.model.parsing.Group
import au.id.tmm.ausvotes.core.model.{HowToVoteCard, SenateElection}
import au.id.tmm.utilities.geo.australia.State

object HowToVoteCardGeneration {

  private val htvsFor2016: Map[(State, String), Vector[String]] = {

    // Taken from http://www.abc.net.au/news/federal-election-2016/guide/svic/htv/ etc
    val rawHtvs = Stream(
      (State.WA, "Shooters, Fishers and Farmers") -> "B,P,Q,R,A,X",
      (State.WA, "Australian Labor Party") -> "D,J,S,H,B,F",
      (State.WA, "Socialist Alliance") -> "G,J,M,O,S,N,K,D",
      (State.WA, "The Greens") -> "J,O,N,K,S,D",
      (State.WA, "Animal Justice Party") -> "K,O,N,U,J,D",
      (State.WA, "Australian Cyclists Party") -> "N,M,K,J,S,C",
      (State.WA, "Renewable Energy Party") -> "O,S,K,U,J,D,AA,N,L",
      (State.WA, "Australian Liberty Alliance") -> "P,B,W,Q,AB,Z",
      (State.WA, "Rise Up Australia Party") -> "Q,B,R,W,AB,T,A",
      (State.WA, "Pauline Hanson's One Nation") -> "R,Q,B,AB,L,A",
      (State.WA, "Australian Sex Party/Marijuana (HEMP) Party") -> "S,K,O,M,J,D",
      (State.WA, "Australian Christians") -> "W,AB,Q,A,T,P,X",
      (State.WA, "Liberal Party of Australia") -> "X,F,W,B,Z,AB",
      (State.WA, "Liberal Democratic Party") -> "Z,I,F,S,AB,B",
      (State.WA, "Family First Party") -> "AB,B,W,T,P,Q",

      (State.ACT, "Australian Labor Party") -> "C,H,J,G,B,A",
      (State.ACT, "Rise Up Australia Party") -> "D,I,F,A,G,E",
      (State.ACT, "Liberal Party of Australia") -> "F,I,A,G,E,C",
      (State.ACT, "The Greens") -> "H,B,J,G,C,E",

      (State.VIC, "Animal Justice Party") -> "C,I,M,X,AK,D",
      (State.VIC, "Australian Labor Party") -> "D,AL,A,M,AK,E",
      (State.VIC, "Science Party / Cyclists Party") -> "E,X,AK,C,Q,D",
      (State.VIC, "Australian Christians") -> "H,R,O,AI,P,AF",
      (State.VIC, "Pirate Party Australia") -> "J,AK,AL,E,D,M",
      (State.VIC, "Renewable Energy Party") -> "M,AL,C,I,AK,D,W,AC,N",
      (State.VIC, "Family First Party") -> "O,H,R,P,AB,A",
      (State.VIC, "Democratic Labour Party") -> "R,H,O,L,AA,AI,AF",
      (State.VIC, "Australian Liberty Alliance") -> "U,AG,A,O,AE,AA",
      (State.VIC, "Marriage Equality") -> "X,AK,AL,E,C,Q",
      (State.VIC, "Pauline Hanson's One Nation") -> "Y,AI,AG,P,AE,A",
      (State.VIC, "Socialist Alliance") -> "Z,AJ,AK,X,AL,M,D",
      (State.VIC, "Liberal Party of Australia") -> "AF,O,H,R,AA,A",
      (State.VIC, "Shooters, Fishers and Farmers") -> "AG,AI,O,Y,P,AH",
      (State.VIC, "Liberal Democratic Party") -> "AH,W,AL,AG,O,AB",
      (State.VIC, "Rise Up Australia Party") -> "AI,Y,H,O,AG,R,P",
      (State.VIC, "The Greens") -> "AK,X,E,C,Q,D",
      (State.VIC, "Australian Sex Party") -> "AL,AD,X,T,Q,M",

      (State.QLD, "Australian Cyclists Party") -> "A,B,H,AK,J,D",
      (State.QLD, "Australian Labor Party") -> "D,AK,V,AD,AC,I",
      (State.QLD, "Liberal Party of Australia") -> "G,T,AA,Q,I,AF",
      (State.QLD, "Animal Justice Party") -> "H,AL,U,A,AK,D",
      (State.QLD, "Katter's Australian Party") -> "I,AC,Q,N,T,X",
      (State.QLD, "Pirate Party Australia") -> "M,AK,V,D,U,C",
      (State.QLD, "Australian Liberty Alliance") -> "N,X,T,I,Q,G",
      (State.QLD, "Shooters, Fishers and Farmers") -> "Q,Y,X,AA,N,G",
      (State.QLD, "Democratic Labour Party") -> "S,Y,AF,I,AH,AA,Q,T,AI,AJ,X,G",
      (State.QLD, "Family First Party") -> "T,Y,N,I,AC,X,G",
      (State.QLD, "Renewable Energy Party") -> "U,AL,H,AH,AK,D,V,AG,W",
      (State.QLD, "Australian Sex Party/Marijuana (HEMP) Party") -> "V,J,H,C,B,A",
      (State.QLD, "Pauline Hanson's One Nation") -> "X,N,Y,Q,T,I",
      (State.QLD, "Rise Up Australia Party") -> "Y,T,X,AF,Q,S,AA",
      (State.QLD, "Glenn Lazarus Team") -> "AC,I,T,AK,D,G",
      (State.QLD, "Australian Christians") -> "AF,T,Y,AA,S,N,G",
      (State.QLD, "Veterans Party") -> "AJ,O,K,AI,AH,AD",
      (State.QLD, "The Greens") -> "AK,U,J,B,D,A",

      (State.TAS, "Family First Party") -> "A,P,T,N,S,F",
      (State.TAS, "Australian Labor Party") -> "B,C,M,S,H,L",
      (State.TAS, "The Greens") -> "C,L,S,B,R,U",
      (State.TAS, "Liberal Party of Australia") -> "F,D,P,T,A,B",
      (State.TAS, "Australian Sex Party/Marijuana (HEMP) Party") -> "H,U,S,R,Q,L",
      (State.TAS, "Pauline Hanson's One Nation") -> "I,D,P,A,S,N", // The groups listed in this how to vote were
//                                                                    I,D,AG,A,S,N, but there is no AG group in Tas. The
//                                                                    Shooters and Fishers are group P
      (State.TAS, "Renewable Energy Party") -> "L,S,C,H,Q,B,J,R,O",
      (State.TAS, "Australian Liberty Alliance") -> "N,D,T,P,A,M",
      (State.TAS, "VOTEFLUX.ORG | Upgrade Democracy!") -> "P,D,I,A,N,T,S",
      (State.TAS, "Science Party") -> "R,Q,C,H,U,B",

      (State.SA, "Australian Labor Party") -> "B,D,U,P,F,J",
      (State.SA, "The Greens") -> "D,U,E,P,C,B",
      (State.SA, "Australian Cyclists Party") -> "E,D,U,R,C,B",
      (State.SA, "Liberal Party of Australia") -> "H,N,K,M,J,Q",
      (State.SA, "Liberal Democratic Party") -> "K,N,Q,R,V,S",
      (State.SA, "Family First Party") -> "N,M,S,Q,O,K",
      (State.SA, "Pauline Hanson's One Nation") -> "O,N,Q,A,S,J",
      (State.SA, "Shooters, Fishers and Farmers") -> "Q,N,M,O,S,K",
      (State.SA, "Australian Sex Party/Marijuana (HEMP) Party") -> "R,P,V,U,C,E",
      (State.SA, "Australian Liberty Alliance") -> "S,Q,K,N,H,M",
      (State.SA, "Animal Justice Party") -> "U,D,P,E,B,V",

      (State.NSW, "Family First Party") -> "C,H,D,X,AM,AF",
      (State.NSW, "Liberal Democratic Party") -> "D,J,AG,C,AM,X",
      (State.NSW, "Liberal Party of Australia") -> "F,AF,J,C,D,AA",
      (State.NSW, "Democratic Labour Party") -> "H,C,AF,X,J,A",
      (State.NSW, "Science Party / Cyclists Party") -> "I,AC,AL,AB,AG,N",
      (State.NSW, "Shooters, Fishers and Farmers") -> "J,AF,AM,M,D,F",
      (State.NSW, "Socialist Alliance") -> "L,AL,AH,K,AJ,AO,AB,N",
      (State.NSW, "Rise Up Australia Party") -> "M,S,AF,H,J,C",
      (State.NSW, "Australian Labor Party") -> "N,AL,AN,AB,AG,D",
      (State.NSW, "Pirate Party Australia") -> "R,AL,AG,I,N,AN",
      (State.NSW, "Pauline Hanson's One Nation") -> "S,M,AB,H,AM,AD",
//      (State.NSW, "Veterans Party") -> "", The Veterans Party lists the Cycling and Science parties in positions 4
//                                           and 5 respectively, but these parties are in the same group. The HTV is
//                                           invalid.
      (State.NSW, "Animal Justice Party") -> "AB,AK,AN,A,AL,N",
      (State.NSW, "Australian Sex Party") -> "AG,AO,K,AN,U,AB",
      (State.NSW, "The Greens") -> "AL,R,I,L,AB,N",
      (State.NSW, "Australian Liberty Alliance") -> "AM,C,J,X,S,D",
      (State.NSW, "Renewable Energy Party") -> "AN,AB,AK,AG,AL,N,AO,E,I",

      (State.NT, "Rise Up Australia Party") -> "A,G,C,E,F,D",
      (State.NT, "The Greens") -> "D,F,B,E,C,G",
      (State.NT, "Country Liberals (NT)") -> "E,G,F,B,D,A",
      (State.NT, "Australian Labor Party") -> "F,D,B,G,E,C"
    )

    rawHtvs.map {
      case ((state, partyName), groupsString) => {
        val groups = groupsString.split(',').toVector
        val mainGroup = groups.head

        (state, mainGroup) -> groups
      }
    }.toMap
  }

  def from(election: SenateElection, groups: Set[Group]): Set[HowToVoteCard] = {
    require(election == SenateElection.`2016`, s"$election is unsupported")

    val groupCodeLookup = groups
      .filter(_.election == SenateElection.`2016`)
      .groupBy(group => (group.state, group.code))
      .mapValues(_.head)

    groups.flatMap(htvIn2016For(_, groupCodeLookup))
  }

  private def htvIn2016For(group: Group, groupCodeLookup: Map[(State, String), Group]): Option[HowToVoteCard] = {
    htvsFor2016.get(group.state, group.code)
      .map(htvGroupCodes => {
        val htvGroupOrder = htvGroupCodes.map(groupCode => groupCodeLookup(group.state, groupCode))

        assert(htvGroupOrder.head == group)

        HowToVoteCard(SenateElection.`2016`, group.state, group, htvGroupOrder)
      })
  }
}

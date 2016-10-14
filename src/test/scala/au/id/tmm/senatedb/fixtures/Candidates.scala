package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.fixtures.Groups.GroupFixture
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.{Candidate, CandidatePosition, Party}
import au.id.tmm.utilities.geo.australia.State

object Candidates {

  trait CandidateFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def candidates: Set[Candidate]

    def groupFixture: GroupFixture

    lazy val groupLookup = groupFixture.groupLookup
  }

  object NT extends CandidateFixture {
    override val state = State.NT

    override val groupFixture = Groups.NT

    override val candidates = Set(
      Candidate(election, state, "28559", "GIMINI", "Jimmy", Some(Party("Rise Up Australia Party")), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, "28546", "BANNISTER", "Kathy", Some(Party("The Greens")), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, "29602", "JONES", "Timothy", Some(Party("Australian Sex Party")), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, "29597", "RYAN", "Maurie Japarta", None, CandidatePosition(groupLookup("UG"), 2)),
      Candidate(election, state, "29140", "MacDONALD", "Marney", Some(Party("Antipaedophile Party")), CandidatePosition(groupLookup("UG"), 3)),
      Candidate(election, state, "28574", "ORDISH", "John", Some(Party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, "28575", "MCCARTHY", "Malarndirri", Some(Party("Australian Labor Party (Northern Territory) Branch")), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, "28558", "PILE", "Jan", Some(Party("Rise Up Australia Party")), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, "28573", "ORDISH", "Carol", Some(Party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, "29596", "STRETTLES", "Greg", None, CandidatePosition(groupLookup("UG"), 4)),
      Candidate(election, state, "28544", "CONNARD", "Michael", Some(Party("The Greens")), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, "28820", "SCULLION", "Nigel", Some(Party("Country Liberals (NT)")), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, "28004", "BARRY", "Ian", Some(Party("Citizens Electoral Council")), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, "28822", "LILLIS", "Jenni", Some(Party("Country Liberals (NT)")), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, "29145", "MARSHALL", "Tristan", Some(Party("Online Direct Democracy - (Empowering the People!)")), CandidatePosition(groupLookup("UG"), 1)),
      Candidate(election, state, "29601", "KAVASILAS", "Andrew", Some(Party("Marijuana (HEMP) Party")), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, "28576", "HONAN", "Pat", Some(Party("Australian Labor Party (Northern Territory) Branch")), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, "28538", "LEE", "TS", None, CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, "28003", "CAMPBELL", "Trudy", Some(Party("Citizens Electoral Council")), CandidatePosition(groupLookup("C"), 0))
    )
  }

  object ACT extends CandidateFixture {
    override val state = State.ACT

    override val groupFixture = Groups.ACT

    override val candidates = Set(
      Candidate(election, state, "29611",    "DONNELLY", "Matt",       Some(Party("Liberal Democrats")), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, "29612",    "HENNINGS", "Cawley",     Some(Party("Liberal Democrats")), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, "28933",    "EDWARDS", "David",       Some(Party("Secular Party of Australia")), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, "28937",    "MIHALJEVIC", "Denis",    Some(Party("Secular Party of Australia")), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, "28147",    "GALLAGHER", "Katy",      Some(Party("Australian Labor Party")), CandidatePosition(groupLookup("C"), 0)),
      Candidate(election, state, "28149",    "SMITH", "David",         Some(Party("Australian Labor Party")), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, "29514",    "O'CONNOR", "Sandie",     Some(Party("Rise Up Australia Party")), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, "29518",    "WYATT", "Jess",          Some(Party("Rise Up Australia Party")), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, "28468",    "HAYDON", "John",         Some(Party("Sustainable Australia")), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, "28469",    "TYE", "Martin",          Some(Party("Sustainable Australia")), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, "28773",    "SESELJA", "Zed",         Some(Party("Liberal")), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, "28782",    "HIATT", "Jane",          Some(Party("Liberal")), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, "28254",    "FIELD", "Deborah",       Some(Party("Animal Justice Party")), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, "28256",    "MONTAGNE", "Jessica",    Some(Party("Animal Justice Party")), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, "28306",    "HOBBS", "Christina",     Some(Party("The Greens")), CandidatePosition(groupLookup("H"), 0)),
      Candidate(election, state, "28308",    "WAREHAM", "Sue",         Some(Party("The Greens")), CandidatePosition(groupLookup("H"), 1)),
      Candidate(election, state, "28760",    "KIM", "David William",   Some(Party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("I"), 0)),
      Candidate(election, state, "28763",    "TADROS", "Elizabeth",    Some(Party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("I"), 1)),
      Candidate(election, state, "29390",    "BAILEY", "Steven",       Some(Party("Australian Sex Party")), CandidatePosition(groupLookup("J"), 0)),
      Candidate(election, state, "29391",    "SWAN", "Robbie",         Some(Party("Australian Sex Party")), CandidatePosition(groupLookup("J"), 1)),
      Candidate(election, state, "29520",   "HAY", "Michael Gerard",  Some(Party("VOTEFLUX.ORG | Upgrade Democracy!")), CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, "28150",   "HANSON", "Anthony",      Some(Party("Mature Australia")), CandidatePosition(groupLookup("UG"), 1))
    )
  }

  object TAS extends CandidateFixture {
    override val state = State.TAS

    override val groupFixture = Groups.TAS

    override lazy val candidates = Set(
      Candidate(election, state, "28580", "ABETZ", "Eric", Some(Party("Liberal")), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, "28600", "CASS", "Suzanne", Some(Party("Derryn Hinch's Justice Party")), CandidatePosition(groupLookup("J"), 0)),
      Candidate(election, state, "29141", "VON STIEGLITZ", "Quentin", Some(Party("Palmer United Party")), CandidatePosition(groupLookup("G"), 2)),
      Candidate(election, state, "29124", "KAYE", "Max", Some(Party("VOTEFLUX.ORG | Upgrade Democracy!")), CandidatePosition(groupLookup("O"), 1)),
      Candidate(election, state, "28595", "SINGH", "Lisa", Some(Party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 5)),
      Candidate(election, state, "28368", "McKIM", "Nick", Some(Party("The Greens")), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, "28276", "HARKINS", "Kevin", Some(Party("Australian Recreational Fishers Party")), CandidatePosition(groupLookup("S"), 0)),
      Candidate(election, state, "28187", "VOLTA", "JoAnne", Some(Party("The Arts Party")), CandidatePosition(groupLookup("U"), 1)),
      Candidate(election, state, "28597", "GORA", "Mishka", Some(Party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, "28596", "NERO-NILE", "Silvana", Some(Party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, "28589", "ROBERTS", "Andrew", Some(Party("Family First")), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, "28048", "KUCINA", "Steve", Some(Party("Citizens Electoral Council")), CandidatePosition(groupLookup("K"), 1)),
      Candidate(election, state, "28592", "BROWN", "Carol", Some(Party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 2)),
      Candidate(election, state, "28186", "O'HARA", "Scott", Some(Party("The Arts Party")), CandidatePosition(groupLookup("U"), 0)),
      Candidate(election, state, "28588", "MADDEN", "Peter", Some(Party("Family First")), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, "28587", "JOYCE", "Sharon", Some(Party("Renewable Energy Party")), CandidatePosition(groupLookup("L"), 1)),
      Candidate(election, state, "29560", "BEVIS", "Karen", Some(Party("Animal Justice Party")), CandidatePosition(groupLookup("Q"), 0)),
      Candidate(election, state, "28581", "PARRY", "Stephen", Some(Party("Liberal")), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, "28594", "SHORT", "John", Some(Party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 4)),
      Candidate(election, state, "28582", "DUNIAM", "Jonathon", Some(Party("Liberal")), CandidatePosition(groupLookup("F"), 2)),
      Candidate(election, state, "28579", "TEMBY", "Richard", Some(Party("Mature Australia")), CandidatePosition(groupLookup("UG"), 2)),
      Candidate(election, state, "28385", "ALSTON", "Ian", Some(Party("Liberal Democrats")), CandidatePosition(groupLookup("T"), 1)),
      Candidate(election, state, "28598", "HOULT", "Michelle", Some(Party("Nick Xenophon Team")), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, "28374", "McCULLOCH", "Kate", Some(Party("Pauline Hanson's One Nation")), CandidatePosition(groupLookup("I"), 0)),
      Candidate(election, state, "28361", "LAMBIE", "Jacqui", Some(Party("Jacqui Lambie Network")), CandidatePosition(groupLookup("M"), 0)),
      Candidate(election, state, "28363", "MARTIN", "Steve", Some(Party("Jacqui Lambie Network")), CandidatePosition(groupLookup("M"), 1)),
      Candidate(election, state, "28593", "BILYK", "Catryna", Some(Party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 3)),
      Candidate(election, state, "29139", "STRINGER", "Justin Leigh", Some(Party("Palmer United Party")), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, "28583", "BUSHBY", "David", Some(Party("Liberal")), CandidatePosition(groupLookup("F"), 3)),
      Candidate(election, state, "29283", "CHOI", "Jin-oh", Some(Party("Science Party")), CandidatePosition(groupLookup("R"), 1)),
      Candidate(election, state, "29288", "ALLEN", "Matthew", Some(Party("Shooters, Fishers and Farmers")), CandidatePosition(groupLookup("P"), 0)),
      Candidate(election, state, "28601", "BAKER", "Daniel", Some(Party("Derryn Hinch's Justice Party")), CandidatePosition(groupLookup("J"), 1)),
      Candidate(election, state, "28599", "COHEN", "Nicky", Some(Party("Nick Xenophon Team")), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, "28381", "MEAD", "Clinton", Some(Party("Liberal Democrats")), CandidatePosition(groupLookup("T"), 0)),
      Candidate(election, state, "28054", "LANE", "George", Some(Party("Independent")), CandidatePosition(groupLookup("UG"), 4)),
      Candidate(election, state, "28584", "COLBECK", "Richard", Some(Party("Liberal")), CandidatePosition(groupLookup("F"), 4)),
      Candidate(election, state, "28586", "MANSON", "Rob", Some(Party("Renewable Energy Party")), CandidatePosition(groupLookup("L"), 0)),
      Candidate(election, state, "28591", "POLLEY", "Helen", Some(Party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, "29281", "WILLINK", "Hans", Some(Party("Science Party")), CandidatePosition(groupLookup("R"), 0)),
      Candidate(election, state, "28375", "MANZI", "Natasia", Some(Party("Pauline Hanson's One Nation")), CandidatePosition(groupLookup("I"), 1)),
      Candidate(election, state, "28585", "TUCKER", "John", Some(Party("Liberal")), CandidatePosition(groupLookup("F"), 5)),
      Candidate(election, state, "28590", "URQUHART", "Anne", Some(Party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, "28801", "HORWOOD", "Susan", Some(Party("Australian Liberty Alliance")), CandidatePosition(groupLookup("N"), 1)),
      Candidate(election, state, "29239", "MARSKELL", "Kaye", Some(Party("Independent")), CandidatePosition(groupLookup("UG"), 1)),
      Candidate(election, state, "28370", "REYNOLDS", "Anna", Some(Party("The Greens")), CandidatePosition(groupLookup("C"), 2)),
      Candidate(election, state, "28367", "WHISH-WILSON", "Peter", Some(Party("The Greens")), CandidatePosition(groupLookup("C"), 0)),
      Candidate(election, state, "28365", "WATERMAN", "Rob", Some(Party("Jacqui Lambie Network")), CandidatePosition(groupLookup("M"), 2)),
      Candidate(election, state, "29291", "MIDSON", "Ricky", Some(Party("Shooters, Fishers and Farmers")), CandidatePosition(groupLookup("P"), 1)),
      Candidate(election, state, "29119", "POULTON", "Adam", Some(Party("VOTEFLUX.ORG | Upgrade Democracy!")), CandidatePosition(groupLookup("O"), 0)),
      Candidate(election, state, "28277", "EVANS", "Carmen", Some(Party("Australian Recreational Fishers Party")), CandidatePosition(groupLookup("S"), 1)),
      Candidate(election, state, "29135", "MORGAN", "Kevin", Some(Party("Palmer United Party")), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, "28358", "CRAWFORD", "David", Some(Party("Antipaedophile Party")), CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, "28047", "THORNTON", "Meg", Some(Party("Citizens Electoral Council")), CandidatePosition(groupLookup("K"), 0)),
      Candidate(election, state, "29302", "COLLINS", "Francesca", Some(Party("Australian Sex Party")), CandidatePosition(groupLookup("H"), 0)),
      Candidate(election, state, "29567", "BAKER", "Alison", Some(Party("Animal Justice Party")), CandidatePosition(groupLookup("Q"), 1)),
      Candidate(election, state, "28767", "RUSSELL", "Grant", Some(Party("Independent")), CandidatePosition(groupLookup("UG"), 3)),
      Candidate(election, state, "28800", "ROBINSON", "Tony", Some(Party("Australian Liberty Alliance")), CandidatePosition(groupLookup("N"), 0)),
      Candidate(election, state, "29307", "OWEN", "Matt", Some(Party("Marijuana (HEMP) Party")), CandidatePosition(groupLookup("H"), 1))
    )
  }
}

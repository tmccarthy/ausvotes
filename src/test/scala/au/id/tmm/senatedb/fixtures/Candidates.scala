package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.fixtures.Groups.GroupFixture
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.{Candidate, CandidatePosition, Name, Party}
import au.id.tmm.utilities.geo.australia.State

object Candidates {

  trait CandidateFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def candidates: Set[Candidate]

    def groupFixture: GroupFixture

    lazy val groupLookup = groupFixture.groupLookup

    def candidateWithId(aecId: String) = candidates.find(_.aecId == aecId).get

    def party(name: String) = Party(election, name)
  }

  object NT extends CandidateFixture {
    override val state = State.NT

    override val groupFixture = Groups.NT

    override val candidates = Set(
      Candidate(election, state, "28559", Name("Jimmy", "GIMINI"), Some(party("Rise Up Australia Party")), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, "28546", Name("Kathy", "BANNISTER"), Some(party("The Greens")), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, "29602", Name("Timothy", "JONES"), Some(party("Australian Sex Party")), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, "29597", Name("Maurie Japarta", "RYAN"), None, CandidatePosition(groupLookup("UG"), 2)),
      Candidate(election, state, "29140", Name("Marney", "MacDONALD"), Some(party("Antipaedophile Party")), CandidatePosition(groupLookup("UG"), 3)),
      Candidate(election, state, "28574", Name("John", "ORDISH"), Some(party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, "28575", Name("Malarndirri", "MCCARTHY"), Some(party("Australian Labor Party (Northern Territory) Branch")), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, "28558", Name("Jan", "PILE"), Some(party("Rise Up Australia Party")), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, "28573", Name("Carol", "ORDISH"), Some(party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, "29596", Name("Greg", "STRETTLES"), None, CandidatePosition(groupLookup("UG"), 4)),
      Candidate(election, state, "28544", Name("Michael", "CONNARD"), Some(party("The Greens")), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, "28820", Name("Nigel", "SCULLION"), Some(party("Country Liberals (NT)")), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, "28004", Name("Ian", "BARRY"), Some(party("Citizens Electoral Council")), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, "28822", Name("Jenni", "LILLIS"), Some(party("Country Liberals (NT)")), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, "29145", Name("Tristan", "MARSHALL"), Some(party("Online Direct Democracy - (Empowering the People!)")), CandidatePosition(groupLookup("UG"), 1)),
      Candidate(election, state, "29601", Name("Andrew", "KAVASILAS"), Some(party("Marijuana (HEMP) Party")), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, "28576", Name("Pat", "HONAN"), Some(party("Australian Labor Party (Northern Territory) Branch")), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, "28538", Name("TS", "LEE"), None, CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, "28003", Name("Trudy", "CAMPBELL"), Some(party("Citizens Electoral Council")), CandidatePosition(groupLookup("C"), 0))
    )
  }

  object ACT extends CandidateFixture {
    override val state = State.ACT

    override val groupFixture = Groups.ACT

    override val candidates = Set(
      Candidate(election, state, "29611",    Name("Matt", "DONNELLY"),       Some(party("Liberal Democrats")), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, "29612",    Name("Cawley", "HENNINGS"),     Some(party("Liberal Democrats")), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, "28933",    Name("David", "EDWARDS"),       Some(party("Secular Party of Australia")), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, "28937",    Name("Denis", "MIHALJEVIC"),    Some(party("Secular Party of Australia")), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, "28147",    Name("Katy", "GALLAGHER"),      Some(party("Australian Labor Party")), CandidatePosition(groupLookup("C"), 0)),
      Candidate(election, state, "28149",    Name("David", "SMITH"),         Some(party("Australian Labor Party")), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, "29514",    Name("Sandie", "O'CONNOR"),     Some(party("Rise Up Australia Party")), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, "29518",    Name("Jess", "WYATT"),          Some(party("Rise Up Australia Party")), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, "28468",    Name("John", "HAYDON"),         Some(party("Sustainable Australia")), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, "28469",    Name("Martin", "TYE"),          Some(party("Sustainable Australia")), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, "28773",    Name("Zed", "SESELJA"),         Some(party("Liberal")), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, "28782",    Name("Jane", "HIATT"),          Some(party("Liberal")), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, "28254",    Name("Deborah", "FIELD"),       Some(party("Animal Justice Party")), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, "28256",    Name("Jessica", "MONTAGNE"),    Some(party("Animal Justice Party")), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, "28306",    Name("Christina", "HOBBS"),     Some(party("The Greens")), CandidatePosition(groupLookup("H"), 0)),
      Candidate(election, state, "28308",    Name("Sue", "WAREHAM"),         Some(party("The Greens")), CandidatePosition(groupLookup("H"), 1)),
      Candidate(election, state, "28760",    Name("David William", "KIM"),   Some(party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("I"), 0)),
      Candidate(election, state, "28763",    Name("Elizabeth", "TADROS"),    Some(party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("I"), 1)),
      Candidate(election, state, "29390",    Name("Steven", "BAILEY"),       Some(party("Australian Sex Party")), CandidatePosition(groupLookup("J"), 0)),
      Candidate(election, state, "29391",    Name("Robbie", "SWAN"),         Some(party("Australian Sex Party")), CandidatePosition(groupLookup("J"), 1)),
      Candidate(election, state, "29520",   Name("Michael Gerard", "HAY"),  Some(party("VOTEFLUX.ORG | Upgrade Democracy!")), CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, "28150",   Name("Anthony", "HANSON"),      Some(party("Mature Australia")), CandidatePosition(groupLookup("UG"), 1))
    )
  }

  object TAS extends CandidateFixture {
    override val state = State.TAS

    override val groupFixture = Groups.TAS

    override lazy val candidates = Set(
      Candidate(election, state, "28580", Name("Eric", "ABETZ"), Some(party("Liberal")), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, "28600", Name("Suzanne", "CASS"), Some(party("Derryn Hinch's Justice Party")), CandidatePosition(groupLookup("J"), 0)),
      Candidate(election, state, "29141", Name("Quentin", "VON STIEGLITZ"), Some(party("Palmer United Party")), CandidatePosition(groupLookup("G"), 2)),
      Candidate(election, state, "29124", Name("Max", "KAYE"), Some(party("VOTEFLUX.ORG | Upgrade Democracy!")), CandidatePosition(groupLookup("O"), 1)),
      Candidate(election, state, "28595", Name("Lisa", "SINGH"), Some(party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 5)),
      Candidate(election, state, "28368", Name("Nick", "McKIM"), Some(party("The Greens")), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, "28276", Name("Kevin", "HARKINS"), Some(party("Australian Recreational Fishers Party")), CandidatePosition(groupLookup("S"), 0)),
      Candidate(election, state, "28187", Name("JoAnne", "VOLTA"), Some(party("The Arts Party")), CandidatePosition(groupLookup("U"), 1)),
      Candidate(election, state, "28597", Name("Mishka", "GORA"), Some(party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, "28596", Name("Silvana", "NERO-NILE"), Some(party("Christian Democratic Party (Fred Nile Group)")), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, "28589", Name("Andrew", "ROBERTS"), Some(party("Family First")), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, "28048", Name("Steve", "KUCINA"), Some(party("Citizens Electoral Council")), CandidatePosition(groupLookup("K"), 1)),
      Candidate(election, state, "28592", Name("Carol", "BROWN"), Some(party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 2)),
      Candidate(election, state, "28186", Name("Scott", "O'HARA"), Some(party("The Arts Party")), CandidatePosition(groupLookup("U"), 0)),
      Candidate(election, state, "28588", Name("Peter", "MADDEN"), Some(party("Family First")), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, "28587", Name("Sharon", "JOYCE"), Some(party("Renewable Energy Party")), CandidatePosition(groupLookup("L"), 1)),
      Candidate(election, state, "29560", Name("Karen", "BEVIS"), Some(party("Animal Justice Party")), CandidatePosition(groupLookup("Q"), 0)),
      Candidate(election, state, "28581", Name("Stephen", "PARRY"), Some(party("Liberal")), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, "28594", Name("John", "SHORT"), Some(party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 4)),
      Candidate(election, state, "28582", Name("Jonathon", "DUNIAM"), Some(party("Liberal")), CandidatePosition(groupLookup("F"), 2)),
      Candidate(election, state, "28579", Name("Richard", "TEMBY"), Some(party("Mature Australia")), CandidatePosition(groupLookup("UG"), 2)),
      Candidate(election, state, "28385", Name("Ian", "ALSTON"), Some(party("Liberal Democrats")), CandidatePosition(groupLookup("T"), 1)),
      Candidate(election, state, "28598", Name("Michelle", "HOULT"), Some(party("Nick Xenophon Team")), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, "28374", Name("Kate", "McCULLOCH"), Some(party("Pauline Hanson's One Nation")), CandidatePosition(groupLookup("I"), 0)),
      Candidate(election, state, "28361", Name("Jacqui", "LAMBIE"), Some(party("Jacqui Lambie Network")), CandidatePosition(groupLookup("M"), 0)),
      Candidate(election, state, "28363", Name("Steve", "MARTIN"), Some(party("Jacqui Lambie Network")), CandidatePosition(groupLookup("M"), 1)),
      Candidate(election, state, "28593", Name("Catryna", "BILYK"), Some(party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 3)),
      Candidate(election, state, "29139", Name("Justin Leigh", "STRINGER"), Some(party("Palmer United Party")), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, "28583", Name("David", "BUSHBY"), Some(party("Liberal")), CandidatePosition(groupLookup("F"), 3)),
      Candidate(election, state, "29283", Name("Jin-oh", "CHOI"), Some(party("Science Party")), CandidatePosition(groupLookup("R"), 1)),
      Candidate(election, state, "29288", Name("Matthew", "ALLEN"), Some(party("Shooters, Fishers and Farmers")), CandidatePosition(groupLookup("P"), 0)),
      Candidate(election, state, "28601", Name("Daniel", "BAKER"), Some(party("Derryn Hinch's Justice Party")), CandidatePosition(groupLookup("J"), 1)),
      Candidate(election, state, "28599", Name("Nicky", "COHEN"), Some(party("Nick Xenophon Team")), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, "28381", Name("Clinton", "MEAD"), Some(party("Liberal Democrats")), CandidatePosition(groupLookup("T"), 0)),
      Candidate(election, state, "28054", Name("George", "LANE"), Some(party("Independent")), CandidatePosition(groupLookup("UG"), 4)),
      Candidate(election, state, "28584", Name("Richard", "COLBECK"), Some(party("Liberal")), CandidatePosition(groupLookup("F"), 4)),
      Candidate(election, state, "28586", Name("Rob", "MANSON"), Some(party("Renewable Energy Party")), CandidatePosition(groupLookup("L"), 0)),
      Candidate(election, state, "28591", Name("Helen", "POLLEY"), Some(party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, "29281", Name("Hans", "WILLINK"), Some(party("Science Party")), CandidatePosition(groupLookup("R"), 0)),
      Candidate(election, state, "28375", Name("Natasia", "MANZI"), Some(party("Pauline Hanson's One Nation")), CandidatePosition(groupLookup("I"), 1)),
      Candidate(election, state, "28585", Name("John", "TUCKER"), Some(party("Liberal")), CandidatePosition(groupLookup("F"), 5)),
      Candidate(election, state, "28590", Name("Anne", "URQUHART"), Some(party("Australian Labor Party")), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, "28801", Name("Susan", "HORWOOD"), Some(party("Australian Liberty Alliance")), CandidatePosition(groupLookup("N"), 1)),
      Candidate(election, state, "29239", Name("Kaye", "MARSKELL"), Some(party("Independent")), CandidatePosition(groupLookup("UG"), 1)),
      Candidate(election, state, "28370", Name("Anna", "REYNOLDS"), Some(party("The Greens")), CandidatePosition(groupLookup("C"), 2)),
      Candidate(election, state, "28367", Name("Peter", "WHISH-WILSON"), Some(party("The Greens")), CandidatePosition(groupLookup("C"), 0)),
      Candidate(election, state, "28365", Name("Rob", "WATERMAN"), Some(party("Jacqui Lambie Network")), CandidatePosition(groupLookup("M"), 2)),
      Candidate(election, state, "29291", Name("Ricky", "MIDSON"), Some(party("Shooters, Fishers and Farmers")), CandidatePosition(groupLookup("P"), 1)),
      Candidate(election, state, "29119", Name("Adam", "POULTON"), Some(party("VOTEFLUX.ORG | Upgrade Democracy!")), CandidatePosition(groupLookup("O"), 0)),
      Candidate(election, state, "28277", Name("Carmen", "EVANS"), Some(party("Australian Recreational Fishers Party")), CandidatePosition(groupLookup("S"), 1)),
      Candidate(election, state, "29135", Name("Kevin", "MORGAN"), Some(party("Palmer United Party")), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, "28358", Name("David", "CRAWFORD"), Some(party("Antipaedophile Party")), CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, "28047", Name("Meg", "THORNTON"), Some(party("Citizens Electoral Council")), CandidatePosition(groupLookup("K"), 0)),
      Candidate(election, state, "29302", Name("Francesca", "COLLINS"), Some(party("Australian Sex Party")), CandidatePosition(groupLookup("H"), 0)),
      Candidate(election, state, "29567", Name("Alison", "BAKER"), Some(party("Animal Justice Party")), CandidatePosition(groupLookup("Q"), 1)),
      Candidate(election, state, "28767", Name("Grant", "RUSSELL"), Some(party("Independent")), CandidatePosition(groupLookup("UG"), 3)),
      Candidate(election, state, "28800", Name("Tony", "ROBINSON"), Some(party("Australian Liberty Alliance")), CandidatePosition(groupLookup("N"), 0)),
      Candidate(election, state, "29307", Name("Matt", "OWEN"), Some(party("Marijuana (HEMP) Party")), CandidatePosition(groupLookup("H"), 1))
    )
  }
}

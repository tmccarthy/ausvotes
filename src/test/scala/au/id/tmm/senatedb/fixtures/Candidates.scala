package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.fixtures.Groups.GroupFixture
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing._
import au.id.tmm.utilities.geo.australia.State

object Candidates {

  trait CandidateFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def candidates: Set[Candidate]

    def groupFixture: GroupFixture

    lazy val groupLookup = groupFixture.groupLookup

    def candidateWithId(aecId: String) = candidates.find(_.aecId == aecId).get

    def candidateWithName(name: Name) = candidates.find(_.name equalsIgnoreCase name).get
  }

  object NT extends CandidateFixture {
    override val state = State.NT

    override val groupFixture = Groups.NT

    override val candidates = Set(
      Candidate(election, state, "28559", Name("Jimmy", "GIMINI"), RegisteredParty("Rise Up Australia Party"), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, "28546", Name("Kathy", "BANNISTER"), RegisteredParty("The Greens"), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, "29602", Name("Timothy", "JONES"), RegisteredParty("Australian Sex Party"), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, "29597", Name("Maurie Japarta", "RYAN"), Independent, CandidatePosition(groupLookup("UG"), 2)),
      Candidate(election, state, "29140", Name("Marney", "MacDONALD"), RegisteredParty("Antipaedophile Party"), CandidatePosition(groupLookup("UG"), 3)),
      Candidate(election, state, "28574", Name("John", "ORDISH"), RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, "28575", Name("Malarndirri", "MCCARTHY"), RegisteredParty("Australian Labor Party (Northern Territory) Branch"), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, "28558", Name("Jan", "PILE"), RegisteredParty("Rise Up Australia Party"), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, "28573", Name("Carol", "ORDISH"), RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, "29596", Name("Greg", "STRETTLES"), Independent, CandidatePosition(groupLookup("UG"), 4)),
      Candidate(election, state, "28544", Name("Michael", "CONNARD"), RegisteredParty("The Greens"), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, "28820", Name("Nigel", "SCULLION"), RegisteredParty("Country Liberals (NT)"), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, "28004", Name("Ian", "BARRY"), RegisteredParty("Citizens Electoral Council"), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, "28822", Name("Jenni", "LILLIS"), RegisteredParty("Country Liberals (NT)"), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, "29145", Name("Tristan", "MARSHALL"), RegisteredParty("Online Direct Democracy - (Empowering the People!)"), CandidatePosition(groupLookup("UG"), 1)),
      Candidate(election, state, "29601", Name("Andrew", "KAVASILAS"), RegisteredParty("Marijuana (HEMP) Party"), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, "28576", Name("Pat", "HONAN"), RegisteredParty("Australian Labor Party (Northern Territory) Branch"), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, "28538", Name("TS", "LEE"), Independent, CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, "28003", Name("Trudy", "CAMPBELL"), RegisteredParty("Citizens Electoral Council"), CandidatePosition(groupLookup("C"), 0))
    )
  }

  object ACT extends CandidateFixture {
    override val state = State.ACT

    override val groupFixture = Groups.ACT

    override val candidates = Set(
      Candidate(election, state, "29611",    Name("Matt", "DONNELLY"),       RegisteredParty("Liberal Democrats"), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, "29612",    Name("Cawley", "HENNINGS"),     RegisteredParty("Liberal Democrats"), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, "28933",    Name("David", "EDWARDS"),       RegisteredParty("Secular Party of Australia"), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, "28937",    Name("Denis", "MIHALJEVIC"),    RegisteredParty("Secular Party of Australia"), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, "28147",    Name("Katy", "GALLAGHER"),      RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("C"), 0)),
      Candidate(election, state, "28149",    Name("David", "SMITH"),         RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, "29514",    Name("Sandie", "O'CONNOR"),     RegisteredParty("Rise Up Australia Party"), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, "29518",    Name("Jess", "WYATT"),          RegisteredParty("Rise Up Australia Party"), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, "28468",    Name("John", "HAYDON"),         RegisteredParty("Sustainable Australia"), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, "28469",    Name("Martin", "TYE"),          RegisteredParty("Sustainable Australia"), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, "28773",    Name("Zed", "SESELJA"),         RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, "28782",    Name("Jane", "HIATT"),          RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, "28254",    Name("Deborah", "FIELD"),       RegisteredParty("Animal Justice Party"), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, "28256",    Name("Jessica", "MONTAGNE"),    RegisteredParty("Animal Justice Party"), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, "28306",    Name("Christina", "HOBBS"),     RegisteredParty("The Greens"), CandidatePosition(groupLookup("H"), 0)),
      Candidate(election, state, "28308",    Name("Sue", "WAREHAM"),         RegisteredParty("The Greens"), CandidatePosition(groupLookup("H"), 1)),
      Candidate(election, state, "28760",    Name("David William", "KIM"),   RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("I"), 0)),
      Candidate(election, state, "28763",    Name("Elizabeth", "TADROS"),    RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("I"), 1)),
      Candidate(election, state, "29390",    Name("Steven", "BAILEY"),       RegisteredParty("Australian Sex Party"), CandidatePosition(groupLookup("J"), 0)),
      Candidate(election, state, "29391",    Name("Robbie", "SWAN"),         RegisteredParty("Australian Sex Party"), CandidatePosition(groupLookup("J"), 1)),
      Candidate(election, state, "29520",   Name("Michael Gerard", "HAY"),  RegisteredParty("VOTEFLUX.ORG | Upgrade Democracy!"), CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, "28150",   Name("Anthony", "HANSON"),      RegisteredParty("Mature Australia"), CandidatePosition(groupLookup("UG"), 1))
    )
  }

  object TAS extends CandidateFixture {
    override val state = State.TAS

    override val groupFixture = Groups.TAS

    override lazy val candidates = Set(
      Candidate(election, state, "28580", Name("Eric", "ABETZ"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, "28600", Name("Suzanne", "CASS"), RegisteredParty("Derryn Hinch's Justice Party"), CandidatePosition(groupLookup("J"), 0)),
      Candidate(election, state, "29141", Name("Quentin", "VON STIEGLITZ"), RegisteredParty("Palmer United Party"), CandidatePosition(groupLookup("G"), 2)),
      Candidate(election, state, "29124", Name("Max", "KAYE"), RegisteredParty("VOTEFLUX.ORG | Upgrade Democracy!"), CandidatePosition(groupLookup("O"), 1)),
      Candidate(election, state, "28595", Name("Lisa", "SINGH"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 5)),
      Candidate(election, state, "28368", Name("Nick", "McKIM"), RegisteredParty("The Greens"), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, "28276", Name("Kevin", "HARKINS"), RegisteredParty("Australian Recreational Fishers Party"), CandidatePosition(groupLookup("S"), 0)),
      Candidate(election, state, "28187", Name("JoAnne", "VOLTA"), RegisteredParty("The Arts Party"), CandidatePosition(groupLookup("U"), 1)),
      Candidate(election, state, "28597", Name("Mishka", "GORA"), RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, "28596", Name("Silvana", "NERO-NILE"), RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, "28589", Name("Andrew", "ROBERTS"), RegisteredParty("Family First"), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, "28048", Name("Steve", "KUCINA"), RegisteredParty("Citizens Electoral Council"), CandidatePosition(groupLookup("K"), 1)),
      Candidate(election, state, "28592", Name("Carol", "BROWN"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 2)),
      Candidate(election, state, "28186", Name("Scott", "O'HARA"), RegisteredParty("The Arts Party"), CandidatePosition(groupLookup("U"), 0)),
      Candidate(election, state, "28588", Name("Peter", "MADDEN"), RegisteredParty("Family First"), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, "28587", Name("Sharon", "JOYCE"), RegisteredParty("Renewable Energy Party"), CandidatePosition(groupLookup("L"), 1)),
      Candidate(election, state, "29560", Name("Karen", "BEVIS"), RegisteredParty("Animal Justice Party"), CandidatePosition(groupLookup("Q"), 0)),
      Candidate(election, state, "28581", Name("Stephen", "PARRY"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, "28594", Name("John", "SHORT"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 4)),
      Candidate(election, state, "28582", Name("Jonathon", "DUNIAM"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 2)),
      Candidate(election, state, "28579", Name("Richard", "TEMBY"), RegisteredParty("Mature Australia"), CandidatePosition(groupLookup("UG"), 2)),
      Candidate(election, state, "28385", Name("Ian", "ALSTON"), RegisteredParty("Liberal Democrats"), CandidatePosition(groupLookup("T"), 1)),
      Candidate(election, state, "28598", Name("Michelle", "HOULT"), RegisteredParty("Nick Xenophon Team"), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, "28374", Name("Kate", "McCULLOCH"), RegisteredParty("Pauline Hanson's One Nation"), CandidatePosition(groupLookup("I"), 0)),
      Candidate(election, state, "28361", Name("Jacqui", "LAMBIE"), RegisteredParty("Jacqui Lambie Network"), CandidatePosition(groupLookup("M"), 0)),
      Candidate(election, state, "28363", Name("Steve", "MARTIN"), RegisteredParty("Jacqui Lambie Network"), CandidatePosition(groupLookup("M"), 1)),
      Candidate(election, state, "28593", Name("Catryna", "BILYK"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 3)),
      Candidate(election, state, "29139", Name("Justin Leigh", "STRINGER"), RegisteredParty("Palmer United Party"), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, "28583", Name("David", "BUSHBY"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 3)),
      Candidate(election, state, "29283", Name("Jin-oh", "CHOI"), RegisteredParty("Science Party"), CandidatePosition(groupLookup("R"), 1)),
      Candidate(election, state, "29288", Name("Matthew", "ALLEN"), RegisteredParty("Shooters, Fishers and Farmers"), CandidatePosition(groupLookup("P"), 0)),
      Candidate(election, state, "28601", Name("Daniel", "BAKER"), RegisteredParty("Derryn Hinch's Justice Party"), CandidatePosition(groupLookup("J"), 1)),
      Candidate(election, state, "28599", Name("Nicky", "COHEN"), RegisteredParty("Nick Xenophon Team"), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, "28381", Name("Clinton", "MEAD"), RegisteredParty("Liberal Democrats"), CandidatePosition(groupLookup("T"), 0)),
      Candidate(election, state, "28054", Name("George", "LANE"), RegisteredParty("Independent"), CandidatePosition(groupLookup("UG"), 4)),
      Candidate(election, state, "28584", Name("Richard", "COLBECK"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 4)),
      Candidate(election, state, "28586", Name("Rob", "MANSON"), RegisteredParty("Renewable Energy Party"), CandidatePosition(groupLookup("L"), 0)),
      Candidate(election, state, "28591", Name("Helen", "POLLEY"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, "29281", Name("Hans", "WILLINK"), RegisteredParty("Science Party"), CandidatePosition(groupLookup("R"), 0)),
      Candidate(election, state, "28375", Name("Natasia", "MANZI"), RegisteredParty("Pauline Hanson's One Nation"), CandidatePosition(groupLookup("I"), 1)),
      Candidate(election, state, "28585", Name("John", "TUCKER"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 5)),
      Candidate(election, state, "28590", Name("Anne", "URQUHART"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, "28801", Name("Susan", "HORWOOD"), RegisteredParty("Australian Liberty Alliance"), CandidatePosition(groupLookup("N"), 1)),
      Candidate(election, state, "29239", Name("Kaye", "MARSKELL"), RegisteredParty("Independent"), CandidatePosition(groupLookup("UG"), 1)),
      Candidate(election, state, "28370", Name("Anna", "REYNOLDS"), RegisteredParty("The Greens"), CandidatePosition(groupLookup("C"), 2)),
      Candidate(election, state, "28367", Name("Peter", "WHISH-WILSON"), RegisteredParty("The Greens"), CandidatePosition(groupLookup("C"), 0)),
      Candidate(election, state, "28365", Name("Rob", "WATERMAN"), RegisteredParty("Jacqui Lambie Network"), CandidatePosition(groupLookup("M"), 2)),
      Candidate(election, state, "29291", Name("Ricky", "MIDSON"), RegisteredParty("Shooters, Fishers and Farmers"), CandidatePosition(groupLookup("P"), 1)),
      Candidate(election, state, "29119", Name("Adam", "POULTON"), RegisteredParty("VOTEFLUX.ORG | Upgrade Democracy!"), CandidatePosition(groupLookup("O"), 0)),
      Candidate(election, state, "28277", Name("Carmen", "EVANS"), RegisteredParty("Australian Recreational Fishers Party"), CandidatePosition(groupLookup("S"), 1)),
      Candidate(election, state, "29135", Name("Kevin", "MORGAN"), RegisteredParty("Palmer United Party"), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, "28358", Name("David", "CRAWFORD"), RegisteredParty("Antipaedophile Party"), CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, "28047", Name("Meg", "THORNTON"), RegisteredParty("Citizens Electoral Council"), CandidatePosition(groupLookup("K"), 0)),
      Candidate(election, state, "29302", Name("Francesca", "COLLINS"), RegisteredParty("Australian Sex Party"), CandidatePosition(groupLookup("H"), 0)),
      Candidate(election, state, "29567", Name("Alison", "BAKER"), RegisteredParty("Animal Justice Party"), CandidatePosition(groupLookup("Q"), 1)),
      Candidate(election, state, "28767", Name("Grant", "RUSSELL"), RegisteredParty("Independent"), CandidatePosition(groupLookup("UG"), 3)),
      Candidate(election, state, "28800", Name("Tony", "ROBINSON"), RegisteredParty("Australian Liberty Alliance"), CandidatePosition(groupLookup("N"), 0)),
      Candidate(election, state, "29307", Name("Matt", "OWEN"), RegisteredParty("Marijuana (HEMP) Party"), CandidatePosition(groupLookup("H"), 1))
    )
  }
}

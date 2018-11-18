package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.fixtures.GroupFixture.GroupFixture
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.ausvotes.core.model.parsing.Party.{Independent, RegisteredParty}
import au.id.tmm.ausvotes.core.model.parsing._
import au.id.tmm.utilities.geo.australia.State

object CandidateFixture {

  trait CandidateFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def candidates: Set[Candidate]

    def groupFixture: GroupFixture

    lazy val groupLookup: Map[String, BallotGroup] = groupFixture.groupLookup

    def candidateWithId(aecId: String): Candidate = candidates.find(_.aecId.asString == aecId).get

    def candidateWithName(name: Name): Candidate = candidates.find(_.name equalsIgnoreCase name).get
  }

  object NT extends CandidateFixture {
    override val state: State = State.NT

    override val groupFixture: GroupFixture.NT.type = GroupFixture.NT

    override val candidates = Set(
      Candidate(election, state, AecCandidateId("28558"), Name("Jan", "PILE"), RegisteredParty("Rise Up Australia Party"), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, AecCandidateId("28559"), Name("Jimmy", "GIMINI"), RegisteredParty("Rise Up Australia Party"), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, AecCandidateId("29601"), Name("Andrew", "KAVASILAS"), RegisteredParty("Marijuana (HEMP) Party"), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, AecCandidateId("29602"), Name("Timothy", "JONES"), RegisteredParty("Australian Sex Party"), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, AecCandidateId("28003"), Name("Trudy", "CAMPBELL"), RegisteredParty("Citizens Electoral Council"), CandidatePosition(groupLookup("C"), 0)),
      Candidate(election, state, AecCandidateId("28004"), Name("Ian", "BARRY"), RegisteredParty("Citizens Electoral Council"), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, AecCandidateId("28544"), Name("Michael", "CONNARD"), RegisteredParty("The Greens"), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, AecCandidateId("28546"), Name("Kathy", "BANNISTER"), RegisteredParty("The Greens"), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, AecCandidateId("28820"), Name("Nigel", "SCULLION"), RegisteredParty("Country Liberals (NT)"), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, AecCandidateId("28822"), Name("Jenni", "LILLIS"), RegisteredParty("Country Liberals (NT)"), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, AecCandidateId("28575"), Name("Malarndirri", "MCCARTHY"), RegisteredParty("Australian Labor Party (Northern Territory) Branch"), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, AecCandidateId("28576"), Name("Pat", "HONAN"), RegisteredParty("Australian Labor Party (Northern Territory) Branch"), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, AecCandidateId("28573"), Name("Carol", "ORDISH"), RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, AecCandidateId("28574"), Name("John", "ORDISH"), RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, AecCandidateId("28538"), Name("TS", "LEE"), Independent, CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, AecCandidateId("29145"), Name("Tristan", "MARSHALL"), RegisteredParty("Online Direct Democracy - (Empowering the People!)"), CandidatePosition(groupLookup("UG"), 1)),
      Candidate(election, state, AecCandidateId("29597"), Name("Maurie Japarta", "RYAN"), Independent, CandidatePosition(groupLookup("UG"), 2)),
      Candidate(election, state, AecCandidateId("29140"), Name("Marney", "MacDONALD"), RegisteredParty("Antipaedophile Party"), CandidatePosition(groupLookup("UG"), 3)),
      Candidate(election, state, AecCandidateId("29596"), Name("Greg", "STRETTLES"), Independent, CandidatePosition(groupLookup("UG"), 4)),
    )
  }

  object ACT extends CandidateFixture {
    override val state: State = State.ACT

    override val groupFixture: GroupFixture.ACT.type = GroupFixture.ACT

    override val candidates = Set(
      Candidate(election, state, AecCandidateId("29611"),    Name("Matt", "DONNELLY"),       RegisteredParty("Liberal Democrats"), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, AecCandidateId("29612"),    Name("Cawley", "HENNINGS"),     RegisteredParty("Liberal Democrats"), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, AecCandidateId("28933"),    Name("David", "EDWARDS"),       RegisteredParty("Secular Party of Australia"), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, AecCandidateId("28937"),    Name("Denis", "MIHALJEVIC"),    RegisteredParty("Secular Party of Australia"), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, AecCandidateId("28147"),    Name("Katy", "GALLAGHER"),      RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("C"), 0)),
      Candidate(election, state, AecCandidateId("28149"),    Name("David", "SMITH"),         RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, AecCandidateId("29514"),    Name("Sandie", "O'CONNOR"),     RegisteredParty("Rise Up Australia Party"), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, AecCandidateId("29518"),    Name("Jess", "WYATT"),          RegisteredParty("Rise Up Australia Party"), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, AecCandidateId("28468"),    Name("John", "HAYDON"),         RegisteredParty("Sustainable Australia"), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, AecCandidateId("28469"),    Name("Martin", "TYE"),          RegisteredParty("Sustainable Australia"), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, AecCandidateId("28773"),    Name("Zed", "SESELJA"),         RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, AecCandidateId("28782"),    Name("Jane", "HIATT"),          RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, AecCandidateId("28254"),    Name("Deborah", "FIELD"),       RegisteredParty("Animal Justice Party"), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, AecCandidateId("28256"),    Name("Jessica", "MONTAGNE"),    RegisteredParty("Animal Justice Party"), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, AecCandidateId("28306"),    Name("Christina", "HOBBS"),     RegisteredParty("The Greens"), CandidatePosition(groupLookup("H"), 0)),
      Candidate(election, state, AecCandidateId("28308"),    Name("Sue", "WAREHAM"),         RegisteredParty("The Greens"), CandidatePosition(groupLookup("H"), 1)),
      Candidate(election, state, AecCandidateId("28760"),    Name("David William", "KIM"),   RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("I"), 0)),
      Candidate(election, state, AecCandidateId("28763"),    Name("Elizabeth", "TADROS"),    RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("I"), 1)),
      Candidate(election, state, AecCandidateId("29390"),    Name("Steven", "BAILEY"),       RegisteredParty("Australian Sex Party"), CandidatePosition(groupLookup("J"), 0)),
      Candidate(election, state, AecCandidateId("29391"),    Name("Robbie", "SWAN"),         RegisteredParty("Australian Sex Party"), CandidatePosition(groupLookup("J"), 1)),
      Candidate(election, state, AecCandidateId("29520"),   Name("Michael Gerard", "HAY"),  RegisteredParty("VOTEFLUX.ORG | Upgrade Democracy!"), CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, AecCandidateId("28150"),   Name("Anthony", "HANSON"),      RegisteredParty("Mature Australia"), CandidatePosition(groupLookup("UG"), 1))
    )

    val katyGallagher: Candidate = candidateWithName(Name("Katy", "GALLAGHER"))
    val zedSeselja: Candidate = candidateWithName(Name("Zed", "SESELJA"))
    val janeHiatt: Candidate = candidateWithName(Name("Jane", "HIATT"))
    val christinaHobbs: Candidate = candidateWithName(Name("Christina", "HOBBS"))
    val mattDonnelly: Candidate = candidateWithName(Name("Matt", "DONNELLY"))
    val anthonyHanson: Candidate = candidateWithName(Name("Anthony", "HANSON"))
  }

  object TAS extends CandidateFixture {
    override val state: State = State.TAS

    override val groupFixture: GroupFixture.TAS.type = GroupFixture.TAS

    override lazy val candidates = Set(
      Candidate(election, state, AecCandidateId("28580"), Name("Eric", "ABETZ"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, AecCandidateId("28600"), Name("Suzanne", "CASS"), RegisteredParty("Derryn Hinch's Justice Party"), CandidatePosition(groupLookup("J"), 0)),
      Candidate(election, state, AecCandidateId("29141"), Name("Quentin", "VON STIEGLITZ"), RegisteredParty("Palmer United Party"), CandidatePosition(groupLookup("G"), 2)),
      Candidate(election, state, AecCandidateId("29124"), Name("Max", "KAYE"), RegisteredParty("VOTEFLUX.ORG | Upgrade Democracy!"), CandidatePosition(groupLookup("O"), 1)),
      Candidate(election, state, AecCandidateId("28595"), Name("Lisa", "SINGH"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 5)),
      Candidate(election, state, AecCandidateId("28368"), Name("Nick", "McKIM"), RegisteredParty("The Greens"), CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, AecCandidateId("28276"), Name("Kevin", "HARKINS"), RegisteredParty("Australian Recreational Fishers Party"), CandidatePosition(groupLookup("S"), 0)),
      Candidate(election, state, AecCandidateId("28187"), Name("JoAnne", "VOLTA"), RegisteredParty("The Arts Party"), CandidatePosition(groupLookup("U"), 1)),
      Candidate(election, state, AecCandidateId("28597"), Name("Mishka", "GORA"), RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, AecCandidateId("28596"), Name("Silvana", "NERO-NILE"), RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, AecCandidateId("28589"), Name("Andrew", "ROBERTS"), RegisteredParty("Family First"), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, AecCandidateId("28048"), Name("Steve", "KUCINA"), RegisteredParty("Citizens Electoral Council"), CandidatePosition(groupLookup("K"), 1)),
      Candidate(election, state, AecCandidateId("28592"), Name("Carol", "BROWN"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 2)),
      Candidate(election, state, AecCandidateId("28186"), Name("Scott", "O'HARA"), RegisteredParty("The Arts Party"), CandidatePosition(groupLookup("U"), 0)),
      Candidate(election, state, AecCandidateId("28588"), Name("Peter", "MADDEN"), RegisteredParty("Family First"), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, AecCandidateId("28587"), Name("Sharon", "JOYCE"), RegisteredParty("Renewable Energy Party"), CandidatePosition(groupLookup("L"), 1)),
      Candidate(election, state, AecCandidateId("29560"), Name("Karen", "BEVIS"), RegisteredParty("Animal Justice Party"), CandidatePosition(groupLookup("Q"), 0)),
      Candidate(election, state, AecCandidateId("28581"), Name("Stephen", "PARRY"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, AecCandidateId("28594"), Name("John", "SHORT"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 4)),
      Candidate(election, state, AecCandidateId("28582"), Name("Jonathon", "DUNIAM"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 2)),
      Candidate(election, state, AecCandidateId("28579"), Name("Richard", "TEMBY"), RegisteredParty("Mature Australia"), CandidatePosition(groupLookup("UG"), 2)),
      Candidate(election, state, AecCandidateId("28385"), Name("Ian", "ALSTON"), RegisteredParty("Liberal Democrats"), CandidatePosition(groupLookup("T"), 1)),
      Candidate(election, state, AecCandidateId("28598"), Name("Michelle", "HOULT"), RegisteredParty("Nick Xenophon Team"), CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, AecCandidateId("28374"), Name("Kate", "McCULLOCH"), RegisteredParty("Pauline Hanson's One Nation"), CandidatePosition(groupLookup("I"), 0)),
      Candidate(election, state, AecCandidateId("28361"), Name("Jacqui", "LAMBIE"), RegisteredParty("Jacqui Lambie Network"), CandidatePosition(groupLookup("M"), 0)),
      Candidate(election, state, AecCandidateId("28363"), Name("Steve", "MARTIN"), RegisteredParty("Jacqui Lambie Network"), CandidatePosition(groupLookup("M"), 1)),
      Candidate(election, state, AecCandidateId("28593"), Name("Catryna", "BILYK"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 3)),
      Candidate(election, state, AecCandidateId("29139"), Name("Justin Leigh", "STRINGER"), RegisteredParty("Palmer United Party"), CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, AecCandidateId("28583"), Name("David", "BUSHBY"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 3)),
      Candidate(election, state, AecCandidateId("29283"), Name("Jin-oh", "CHOI"), RegisteredParty("Science Party"), CandidatePosition(groupLookup("R"), 1)),
      Candidate(election, state, AecCandidateId("29288"), Name("Matthew", "ALLEN"), RegisteredParty("Shooters, Fishers and Farmers"), CandidatePosition(groupLookup("P"), 0)),
      Candidate(election, state, AecCandidateId("28601"), Name("Daniel", "BAKER"), RegisteredParty("Derryn Hinch's Justice Party"), CandidatePosition(groupLookup("J"), 1)),
      Candidate(election, state, AecCandidateId("28599"), Name("Nicky", "COHEN"), RegisteredParty("Nick Xenophon Team"), CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, AecCandidateId("28381"), Name("Clinton", "MEAD"), RegisteredParty("Liberal Democrats"), CandidatePosition(groupLookup("T"), 0)),
      Candidate(election, state, AecCandidateId("28054"), Name("George", "LANE"), RegisteredParty("Independent"), CandidatePosition(groupLookup("UG"), 4)),
      Candidate(election, state, AecCandidateId("28584"), Name("Richard", "COLBECK"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 4)),
      Candidate(election, state, AecCandidateId("28586"), Name("Rob", "MANSON"), RegisteredParty("Renewable Energy Party"), CandidatePosition(groupLookup("L"), 0)),
      Candidate(election, state, AecCandidateId("28591"), Name("Helen", "POLLEY"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, AecCandidateId("29281"), Name("Hans", "WILLINK"), RegisteredParty("Science Party"), CandidatePosition(groupLookup("R"), 0)),
      Candidate(election, state, AecCandidateId("28375"), Name("Natasia", "MANZI"), RegisteredParty("Pauline Hanson's One Nation"), CandidatePosition(groupLookup("I"), 1)),
      Candidate(election, state, AecCandidateId("28585"), Name("John", "TUCKER"), RegisteredParty("Liberal"), CandidatePosition(groupLookup("F"), 5)),
      Candidate(election, state, AecCandidateId("28590"), Name("Anne", "URQUHART"), RegisteredParty("Australian Labor Party"), CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, AecCandidateId("28801"), Name("Susan", "HORWOOD"), RegisteredParty("Australian Liberty Alliance"), CandidatePosition(groupLookup("N"), 1)),
      Candidate(election, state, AecCandidateId("29239"), Name("Kaye", "MARSKELL"), RegisteredParty("Independent"), CandidatePosition(groupLookup("UG"), 1)),
      Candidate(election, state, AecCandidateId("28370"), Name("Anna", "REYNOLDS"), RegisteredParty("The Greens"), CandidatePosition(groupLookup("C"), 2)),
      Candidate(election, state, AecCandidateId("28367"), Name("Peter", "WHISH-WILSON"), RegisteredParty("The Greens"), CandidatePosition(groupLookup("C"), 0)),
      Candidate(election, state, AecCandidateId("28365"), Name("Rob", "WATERMAN"), RegisteredParty("Jacqui Lambie Network"), CandidatePosition(groupLookup("M"), 2)),
      Candidate(election, state, AecCandidateId("29291"), Name("Ricky", "MIDSON"), RegisteredParty("Shooters, Fishers and Farmers"), CandidatePosition(groupLookup("P"), 1)),
      Candidate(election, state, AecCandidateId("29119"), Name("Adam", "POULTON"), RegisteredParty("VOTEFLUX.ORG | Upgrade Democracy!"), CandidatePosition(groupLookup("O"), 0)),
      Candidate(election, state, AecCandidateId("28277"), Name("Carmen", "EVANS"), RegisteredParty("Australian Recreational Fishers Party"), CandidatePosition(groupLookup("S"), 1)),
      Candidate(election, state, AecCandidateId("29135"), Name("Kevin", "MORGAN"), RegisteredParty("Palmer United Party"), CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, AecCandidateId("28358"), Name("David", "CRAWFORD"), RegisteredParty("Antipaedophile Party"), CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, AecCandidateId("28047"), Name("Meg", "THORNTON"), RegisteredParty("Citizens Electoral Council"), CandidatePosition(groupLookup("K"), 0)),
      Candidate(election, state, AecCandidateId("29302"), Name("Francesca", "COLLINS"), RegisteredParty("Australian Sex Party"), CandidatePosition(groupLookup("H"), 0)),
      Candidate(election, state, AecCandidateId("29567"), Name("Alison", "BAKER"), RegisteredParty("Animal Justice Party"), CandidatePosition(groupLookup("Q"), 1)),
      Candidate(election, state, AecCandidateId("28767"), Name("Grant", "RUSSELL"), RegisteredParty("Independent"), CandidatePosition(groupLookup("UG"), 3)),
      Candidate(election, state, AecCandidateId("28800"), Name("Tony", "ROBINSON"), RegisteredParty("Australian Liberty Alliance"), CandidatePosition(groupLookup("N"), 0)),
      Candidate(election, state, AecCandidateId("29307"), Name("Matt", "OWEN"), RegisteredParty("Marijuana (HEMP) Party"), CandidatePosition(groupLookup("H"), 1))
    )
  }

  object WA extends CandidateFixture {
    override val state: State = State.WA

    override val groupFixture: GroupFixture = GroupFixture.WA

    override lazy val candidates: Set[Candidate] = Set(
      Candidate(election, state, AecCandidateId("29437"), Name("Mark David", "IMISIDES"),          RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("A"), 0)),
      Candidate(election, state, AecCandidateId("29440"), Name("Philip Campbell", "READ"),         RegisteredParty("Christian Democratic Party (Fred Nile Group)"), CandidatePosition(groupLookup("A"), 1)),
      Candidate(election, state, AecCandidateId("28400"), Name("Andrew", "SKERRITT"),              RegisteredParty("Shooters, Fishers and Farmers"),                CandidatePosition(groupLookup("B"), 0)),
      Candidate(election, state, AecCandidateId("28401"), Name("Ross", "WILLIAMSON"),              RegisteredParty("Shooters, Fishers and Farmers"),                CandidatePosition(groupLookup("B"), 1)),
      Candidate(election, state, AecCandidateId("28527"), Name("Luke", "BOLTON"),                  RegisteredParty("Nick Xenophon Team"),                           CandidatePosition(groupLookup("C"), 0)),
      Candidate(election, state, AecCandidateId("28528"), Name("Michael", "BOVELL"),               RegisteredParty("Nick Xenophon Team"),                           CandidatePosition(groupLookup("C"), 1)),
      Candidate(election, state, AecCandidateId("28516"), Name("Sue", "LINES"),                    RegisteredParty("Australian Labor Party"),                       CandidatePosition(groupLookup("D"), 0)),
      Candidate(election, state, AecCandidateId("28517"), Name("Glenn", "STERLE"),                 RegisteredParty("Australian Labor Party"),                       CandidatePosition(groupLookup("D"), 1)),
      Candidate(election, state, AecCandidateId("28518"), Name("Patrick", "DODSON"),               RegisteredParty("Australian Labor Party"),                       CandidatePosition(groupLookup("D"), 2)),
      Candidate(election, state, AecCandidateId("28519"), Name("Louise", "PRATT"),                 RegisteredParty("Australian Labor Party"),                       CandidatePosition(groupLookup("D"), 3)),
      Candidate(election, state, AecCandidateId("28520"), Name("Mark", "REED"),                    RegisteredParty("Australian Labor Party"),                       CandidatePosition(groupLookup("D"), 4)),
      Candidate(election, state, AecCandidateId("28521"), Name("Susan", "BOWERS"),                 RegisteredParty("Australian Labor Party"),                       CandidatePosition(groupLookup("D"), 5)),
      Candidate(election, state, AecCandidateId("28522"), Name("Mia", "ONORATO"),                  RegisteredParty("Australian Labor Party"),                       CandidatePosition(groupLookup("D"), 6)),
      Candidate(election, state, AecCandidateId("28098"), Name("Jean", "ROBINSON"),                RegisteredParty("Citizens Electoral Council of Australia"),      CandidatePosition(groupLookup("E"), 0)),
      Candidate(election, state, AecCandidateId("28099"), Name("Judy", "SUDHOLZ"),                 RegisteredParty("Citizens Electoral Council of Australia"),      CandidatePosition(groupLookup("E"), 1)),
      Candidate(election, state, AecCandidateId("29189"), Name("Kado", "MUIR"),                    RegisteredParty("The Nationals"),                                CandidatePosition(groupLookup("F"), 0)),
      Candidate(election, state, AecCandidateId("29190"), Name("Nick", "FARDELL"),                 RegisteredParty("The Nationals"),                                CandidatePosition(groupLookup("F"), 1)),
      Candidate(election, state, AecCandidateId("29191"), Name("Elizabeth", "RE"),                 RegisteredParty("The Nationals"),                                CandidatePosition(groupLookup("F"), 2)),
      Candidate(election, state, AecCandidateId("28341"), Name("Kamala", "EMANUEL"),               RegisteredParty("Socialist Alliance"),                           CandidatePosition(groupLookup("G"), 0)),
      Candidate(election, state, AecCandidateId("28342"), Name("Seamus", "DOHERTY"),               RegisteredParty("Socialist Alliance"),                           CandidatePosition(groupLookup("G"), 1)),
      Candidate(election, state, AecCandidateId("28343"), Name("Farida", "IQBAL"),                 RegisteredParty("Socialist Alliance"),                           CandidatePosition(groupLookup("G"), 2)),
      Candidate(election, state, AecCandidateId("29604"), Name("Nicki", "HIDE"),                   RegisteredParty("Derryn Hinch's Justice Party"),                 CandidatePosition(groupLookup("H"), 0)),
      Candidate(election, state, AecCandidateId("29605"), Name("Rachael", "HIGGINS"),              RegisteredParty("Derryn Hinch's Justice Party"),                 CandidatePosition(groupLookup("H"), 1)),
      Candidate(election, state, AecCandidateId("29193"), Name("Zhenya Dio", "WANG"),              RegisteredParty("Palmer United Party"),                          CandidatePosition(groupLookup("I"), 0)),
      Candidate(election, state, AecCandidateId("29194"), Name("Jacque", "KRUGER"),                RegisteredParty("Palmer United Party"),                          CandidatePosition(groupLookup("I"), 1)),
      Candidate(election, state, AecCandidateId("28246"), Name("Scott", "LUDLAM"),                 RegisteredParty("The Greens (WA)"),                              CandidatePosition(groupLookup("J"), 0)),
      Candidate(election, state, AecCandidateId("28247"), Name("Rachel", "SIEWERT"),               RegisteredParty("The Greens (WA)"),                              CandidatePosition(groupLookup("J"), 1)),
      Candidate(election, state, AecCandidateId("28248"), Name("Jordon", "STEELE-JOHN"),           RegisteredParty("The Greens (WA)"),                              CandidatePosition(groupLookup("J"), 2)),
      Candidate(election, state, AecCandidateId("28249"), Name("Samantha", "JENKINSON"),           RegisteredParty("The Greens (WA)"),                              CandidatePosition(groupLookup("J"), 3)),
      Candidate(election, state, AecCandidateId("28250"), Name("Michael", "BALDOCK"),              RegisteredParty("The Greens (WA)"),                              CandidatePosition(groupLookup("J"), 4)),
      Candidate(election, state, AecCandidateId("28251"), Name("Rai", "ISMAIL"),                   RegisteredParty("The Greens (WA)"),                              CandidatePosition(groupLookup("J"), 5)),
      Candidate(election, state, AecCandidateId("28831"), Name("Katrina", "LOVE"),                 RegisteredParty("Animal Justice Party"),                         CandidatePosition(groupLookup("K"), 0)),
      Candidate(election, state, AecCandidateId("28834"), Name("Alicia", "SUTTON"),                RegisteredParty("Animal Justice Party"),                         CandidatePosition(groupLookup("K"), 1)),
      Candidate(election, state, AecCandidateId("28270"), Name("Stuart", "DONALD"),                RegisteredParty("Mature Australia"),                             CandidatePosition(groupLookup("L"), 0)),
      Candidate(election, state, AecCandidateId("28271"), Name("Patti", "BRADSHAW"),               RegisteredParty("Mature Australia"),                             CandidatePosition(groupLookup("L"), 1)),
      Candidate(election, state, AecCandidateId("28264"), Name("Robert", "BURATTI"),               RegisteredParty("The Arts Party"),                               CandidatePosition(groupLookup("M"), 0)),
      Candidate(election, state, AecCandidateId("28265"), Name("Robert Kenneth Leslie", "TAYLOR"), RegisteredParty("The Arts Party"),                               CandidatePosition(groupLookup("M"), 1)),
      Candidate(election, state, AecCandidateId("29197"), Name("Peter", "MAH"),                    RegisteredParty("Australian Cyclists Party"),                    CandidatePosition(groupLookup("N"), 0)),
      Candidate(election, state, AecCandidateId("29198"), Name("Christopher John", "HOWARD"),      RegisteredParty("Australian Cyclists Party"),                    CandidatePosition(groupLookup("N"), 1)),
      Candidate(election, state, AecCandidateId("28272"), Name("Pedro", "SCHWINDT"),               RegisteredParty("Renewable Energy Party"),                       CandidatePosition(groupLookup("O"), 0)),
      Candidate(election, state, AecCandidateId("28273"), Name("Camilla", "SUNDBLADH"),            RegisteredParty("Renewable Energy Party"),                       CandidatePosition(groupLookup("O"), 1)),
      Candidate(election, state, AecCandidateId("28402"), Name("Anthony", "HARDWICK"),             RegisteredParty("Rise Up Australia Party"),                      CandidatePosition(groupLookup("Q"), 0)),
      Candidate(election, state, AecCandidateId("28403"), Name("Sheila", "MUNDY"),                 RegisteredParty("Rise Up Australia Party"),                      CandidatePosition(groupLookup("Q"), 1)),
      Candidate(election, state, AecCandidateId("28523"), Name("Debbie", "ROBINSON"),              RegisteredParty("Australian Liberty Alliance"),                  CandidatePosition(groupLookup("P"), 0)),
      Candidate(election, state, AecCandidateId("28524"), Name("Marion", "HERCOCK"),               RegisteredParty("Australian Liberty Alliance"),                  CandidatePosition(groupLookup("P"), 1)),
      Candidate(election, state, AecCandidateId("29186"), Name("Rodney Norman", "CULLETON"),       RegisteredParty("Pauline Hanson's One Nation"),                  CandidatePosition(groupLookup("R"), 0)),
      Candidate(election, state, AecCandidateId("29187"), Name("Peter", "GEORGIOU"),               RegisteredParty("Pauline Hanson's One Nation"),                  CandidatePosition(groupLookup("R"), 1)),
      Candidate(election, state, AecCandidateId("29188"), Name("Ioanna", "CULLETON"),              RegisteredParty("Pauline Hanson's One Nation"),                  CandidatePosition(groupLookup("R"), 2)),
      Candidate(election, state, AecCandidateId("29607"), Name("Michael", "BALDERSTONE"),          RegisteredParty("Marijuana (HEMP) Party"),                       CandidatePosition(groupLookup("S"), 0)),
      Candidate(election, state, AecCandidateId("29608"), Name("James", "HURLEY"),                 RegisteredParty("Australian Sex Party"),                         CandidatePosition(groupLookup("S"), 1)),
      Candidate(election, state, AecCandidateId("28804"), Name("Fernando", "BOVE"),                RegisteredParty("Democratic Labour Party"),                      CandidatePosition(groupLookup("T"), 0)),
      Candidate(election, state, AecCandidateId("28807"), Name("Troy", "KIERNAN"),                 RegisteredParty("Democratic Labour Party"),                      CandidatePosition(groupLookup("T"), 1)),
      Candidate(election, state, AecCandidateId("28494"), Name("Michaelia", "CASH"),               RegisteredParty("Liberal Party of Australia"),                   CandidatePosition(groupLookup("X"), 1)),
      Candidate(election, state, AecCandidateId("28497"), Name("Chris", "BACK"),                   RegisteredParty("Liberal Party of Australia"),                   CandidatePosition(groupLookup("X"), 4)),
      Candidate(election, state, AecCandidateId("29395"), Name("Sara", "FARGHER"),                 RegisteredParty("Health Australia Party"),                       CandidatePosition(groupLookup("U"), 1)),
      Candidate(election, state, AecCandidateId("29360"), Name("Samantha", "TILBURY"),             RegisteredParty("Health Australia Party"),                       CandidatePosition(groupLookup("U"), 0)),
      Candidate(election, state, AecCandidateId("28864"), Name("Stuey", "PAULL"),                  Independent,                                                     CandidatePosition(groupLookup("V"), 0)),
      Candidate(election, state, AecCandidateId("28865"), Name("Gary J", "MORRIS"),                Independent,                                                     CandidatePosition(groupLookup("V"), 1)),
      Candidate(election, state, AecCandidateId("28821"), Name("Lindsay", "CAMERON"),              RegisteredParty("Australian Christians"),                        CandidatePosition(groupLookup("W"), 0)),
      Candidate(election, state, AecCandidateId("28825"), Name("Jacky", "YOUNG"),                  RegisteredParty("Australian Christians"),                        CandidatePosition(groupLookup("W"), 1)),
      Candidate(election, state, AecCandidateId("28493"), Name("Mathias", "CORMANN"),              RegisteredParty("Liberal Party of Australia"),                   CandidatePosition(groupLookup("X"), 0)),
      Candidate(election, state, AecCandidateId("28495"), Name("Dean", "SMITH"),                   RegisteredParty("Liberal Party of Australia"),                   CandidatePosition(groupLookup("X"), 2)),
      Candidate(election, state, AecCandidateId("28496"), Name("Linda", "REYNOLDS"),               RegisteredParty("Liberal Party of Australia"),                   CandidatePosition(groupLookup("X"), 3)),
      Candidate(election, state, AecCandidateId("28502"), Name("Sheridan", "INGRAM"),              RegisteredParty("Liberal Party of Australia"),                   CandidatePosition(groupLookup("X"), 6)),
      Candidate(election, state, AecCandidateId("28500"), Name("David", "JOHNSTON"),               RegisteredParty("Liberal Party of Australia"),                   CandidatePosition(groupLookup("X"), 5)),
      Candidate(election, state, AecCandidateId("28153"), Name("Lyn", "VICKERY"),                  RegisteredParty("Australia First Party"),                        CandidatePosition(groupLookup("Y"), 0)),
      Candidate(election, state, AecCandidateId("28155"), Name("Brian", "McREA"),                  RegisteredParty("Australia First Party"),                        CandidatePosition(groupLookup("Y"), 1)),
      Candidate(election, state, AecCandidateId("28514"), Name("Graeme Michael", "KLASS"),         RegisteredParty("Liberal Democratic Party"),                     CandidatePosition(groupLookup("Z"), 0)),
      Candidate(election, state, AecCandidateId("28515"), Name("Connor", "WHITTLE"),               RegisteredParty("Liberal Democratic Party"),                     CandidatePosition(groupLookup("Z"), 1)),
      Candidate(election, state, AecCandidateId("29200"), Name("Richard", "THOMAS"),               RegisteredParty("VOTEFLUX.ORG | Upgrade Democracy!"),            CandidatePosition(groupLookup("AA"), 0)),
      Candidate(election, state, AecCandidateId("29201"), Name("Mark", "CONNOLLY"),                RegisteredParty("VOTEFLUX.ORG | Upgrade Democracy!"),            CandidatePosition(groupLookup("AA"), 1)),
      Candidate(election, state, AecCandidateId("28525"), Name("Linda", "ROSE"),                   RegisteredParty("Family First Party"),                           CandidatePosition(groupLookup("AB"), 0)),
      Candidate(election, state, AecCandidateId("28526"), Name("Henry", "HENG"),                   RegisteredParty("Family First Party"),                           CandidatePosition(groupLookup("AB"), 1)),
      Candidate(election, state, AecCandidateId("28097"), Name("Kai", "JONES"),                    Independent,                                                     CandidatePosition(groupLookup("UG"), 0)),
      Candidate(election, state, AecCandidateId("28279"), Name("Tammara", "MOODY"),                RegisteredParty("Australian Antipaedophile Party"),              CandidatePosition(groupLookup("UG"), 1)),
      Candidate(election, state, AecCandidateId("28188"), Name("Julie", "MATHESON"),               Independent,                                                     CandidatePosition(groupLookup("UG"), 2)),
      Candidate(election, state, AecCandidateId("29356"), Name("Peter", "CASTIEAU"),               Independent,                                                     CandidatePosition(groupLookup("UG"), 3)),
      Candidate(election, state, AecCandidateId("29401"), Name("Susan", "HODDINOTT"),              RegisteredParty("Katter's Australian Party"),                    CandidatePosition(groupLookup("UG"), 4)),
      Candidate(election, state, AecCandidateId("29428"), Name("Norm", "RAMSAY"),                  Independent,                                                     CandidatePosition(groupLookup("UG"), 5)),
    )
  }
}

package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.fixtures.GroupFixture.GroupFixture
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.{Candidate, Name, Party}
import au.id.tmm.utilities.geo.australia.State

object CandidateFixture {

  trait CandidateFixture {
    val senateElection: SenateElection = SenateElection.`2016`
    def state: State
    def election: SenateElectionForState = SenateElectionForState(senateElection, state).right.get
    def candidates: Set[SenateCandidate]

    def groupFixture: GroupFixture

    lazy val groupLookup: Map[String, SenateBallotGroup] = groupFixture.groupLookup

    def candidateWithId(aecId: Int): SenateCandidate = candidates.find(_.candidateDetails.id.asInt == aecId).get

    def candidateWithName(name: Name): SenateCandidate = candidates.find(_.candidateDetails.name equalsIgnoreCase name).get

    def candidateWithPosition(candidatePosition: SenateCandidatePosition): SenateCandidate = candidates.find(_.position == candidatePosition).get
  }

  object NT extends CandidateFixture {
    override val state: State = State.NT

    override val groupFixture: GroupFixture.NT.type = GroupFixture.NT

    override val candidates: Set[SenateCandidate] = Set(
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28558), name = Name("Jan", "PILE"), party = Some(Party("Rise Up Australia Party"))), SenateCandidatePosition(groupLookup("A"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28559), name = Name("Jimmy", "GIMINI"), party = Some(Party("Rise Up Australia Party"))), SenateCandidatePosition(groupLookup("A"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29601), name = Name("Andrew", "KAVASILAS"), party = Some(Party("Marijuana (HEMP) Party"))), SenateCandidatePosition(groupLookup("B"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29602), name = Name("Timothy", "JONES"), party = Some(Party("Australian Sex Party"))), SenateCandidatePosition(groupLookup("B"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28003), name = Name("Trudy", "CAMPBELL"), party = Some(Party("Citizens Electoral Council"))), SenateCandidatePosition(groupLookup("C"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28004), name = Name("Ian", "BARRY"), party = Some(Party("Citizens Electoral Council"))), SenateCandidatePosition(groupLookup("C"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28544), name = Name("Michael", "CONNARD"), party = Some(Party("The Greens"))), SenateCandidatePosition(groupLookup("D"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28546), name = Name("Kathy", "BANNISTER"), party = Some(Party("The Greens"))), SenateCandidatePosition(groupLookup("D"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28820), name = Name("Nigel", "SCULLION"), party = Some(Party("Country Liberals (NT)"))), SenateCandidatePosition(groupLookup("E"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28822), name = Name("Jenni", "LILLIS"), party = Some(Party("Country Liberals (NT)"))), SenateCandidatePosition(groupLookup("E"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28575), name = Name("Malarndirri", "MCCARTHY"), party = Some(Party("Australian Labor Party (Northern Territory) Branch"))), SenateCandidatePosition(groupLookup("F"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28576), name = Name("Pat", "HONAN"), party = Some(Party("Australian Labor Party (Northern Territory) Branch"))), SenateCandidatePosition(groupLookup("F"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28573), name = Name("Carol", "ORDISH"), party = Some(Party("Christian Democratic Party (Fred Nile Group)"))), SenateCandidatePosition(groupLookup("G"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28574), name = Name("John", "ORDISH"), party = Some(Party("Christian Democratic Party (Fred Nile Group)"))), SenateCandidatePosition(groupLookup("G"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28538), name = Name("TS", "LEE"), party = None), SenateCandidatePosition(groupLookup("UG"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29145), name = Name("Tristan", "MARSHALL"), party = Some(Party("Online Direct Democracy - (Empowering the People!)"))), SenateCandidatePosition(groupLookup("UG"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29597), name = Name("Maurie Japarta", "RYAN"), party = None), SenateCandidatePosition(groupLookup("UG"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29140), name = Name("Marney", "MacDONALD"), party = Some(Party("Antipaedophile Party"))), SenateCandidatePosition(groupLookup("UG"), 3)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29596), name = Name("Greg", "STRETTLES"), party = None), SenateCandidatePosition(groupLookup("UG"), 4)),
    )
  }

  object ACT extends CandidateFixture {
    override val state: State = State.ACT

    override val groupFixture: GroupFixture.ACT.type = GroupFixture.ACT

    override val candidates: Set[SenateCandidate] = Set(
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29611),    name = Name("Matt", "DONNELLY"),       party = Some(Party("Liberal Democrats"))), SenateCandidatePosition(groupLookup("A"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29612),    name = Name("Cawley", "HENNINGS"),     party = Some(Party("Liberal Democrats"))), SenateCandidatePosition(groupLookup("A"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28933),    name = Name("David", "EDWARDS"),       party = Some(Party("Secular Party of Australia"))), SenateCandidatePosition(groupLookup("B"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28937),    name = Name("Denis", "MIHALJEVIC"),    party = Some(Party("Secular Party of Australia"))), SenateCandidatePosition(groupLookup("B"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28147),    name = Name("Katy", "GALLAGHER"),      party = Some(Party("Australian Labor Party"))), SenateCandidatePosition(groupLookup("C"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28149),    name = Name("David", "SMITH"),         party = Some(Party("Australian Labor Party"))), SenateCandidatePosition(groupLookup("C"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29514),    name = Name("Sandie", "O'CONNOR"),     party = Some(Party("Rise Up Australia Party"))), SenateCandidatePosition(groupLookup("D"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29518),    name = Name("Jess", "WYATT"),          party = Some(Party("Rise Up Australia Party"))), SenateCandidatePosition(groupLookup("D"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28468),    name = Name("John", "HAYDON"),         party = Some(Party("Sustainable Australia"))), SenateCandidatePosition(groupLookup("E"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28469),    name = Name("Martin", "TYE"),          party = Some(Party("Sustainable Australia"))), SenateCandidatePosition(groupLookup("E"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28773),    name = Name("Zed", "SESELJA"),         party = Some(Party("Liberal"))), SenateCandidatePosition(groupLookup("F"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28782),    name = Name("Jane", "HIATT"),          party = Some(Party("Liberal"))), SenateCandidatePosition(groupLookup("F"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28254),    name = Name("Deborah", "FIELD"),       party = Some(Party("Animal Justice Party"))), SenateCandidatePosition(groupLookup("G"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28256),    name = Name("Jessica", "MONTAGNE"),    party = Some(Party("Animal Justice Party"))), SenateCandidatePosition(groupLookup("G"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28306),    name = Name("Christina", "HOBBS"),     party = Some(Party("The Greens"))), SenateCandidatePosition(groupLookup("H"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28308),    name = Name("Sue", "WAREHAM"),         party = Some(Party("The Greens"))), SenateCandidatePosition(groupLookup("H"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28760),    name = Name("David William", "KIM"),   party = Some(Party("Christian Democratic Party (Fred Nile Group)"))), SenateCandidatePosition(groupLookup("I"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28763),    name = Name("Elizabeth", "TADROS"),    party = Some(Party("Christian Democratic Party (Fred Nile Group)"))), SenateCandidatePosition(groupLookup("I"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29390),    name = Name("Steven", "BAILEY"),       party = Some(Party("Australian Sex Party"))), SenateCandidatePosition(groupLookup("J"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29391),    name = Name("Robbie", "SWAN"),         party = Some(Party("Australian Sex Party"))), SenateCandidatePosition(groupLookup("J"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29520),   name = Name("Michael Gerard", "HAY"),  party = Some(Party("VOTEFLUX.ORG | Upgrade Democracy!"))), SenateCandidatePosition(groupLookup("UG"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28150),   name = Name("Anthony", "HANSON"),      party = Some(Party("Mature Australia"))), SenateCandidatePosition(groupLookup("UG"), 1))
    )

    val katyGallagher: SenateCandidate = candidateWithName(Name("Katy", "GALLAGHER"))
    val zedSeselja: SenateCandidate = candidateWithName(Name("Zed", "SESELJA"))
    val janeHiatt: SenateCandidate = candidateWithName(Name("Jane", "HIATT"))
    val christinaHobbs: SenateCandidate = candidateWithName(Name("Christina", "HOBBS"))
    val mattDonnelly: SenateCandidate = candidateWithName(Name("Matt", "DONNELLY"))
    val anthonyHanson: SenateCandidate = candidateWithName(Name("Anthony", "HANSON"))
  }

  object TAS extends CandidateFixture {
    override val state: State = State.TAS

    override val groupFixture: GroupFixture.TAS.type = GroupFixture.TAS

    override lazy val candidates: Set[SenateCandidate] = Set(
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28580), name = Name("Eric", "ABETZ"), party = Some(Party("Liberal"))), SenateCandidatePosition(groupLookup("F"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28600), name = Name("Suzanne", "CASS"), party = Some(Party("Derryn Hinch's Justice Party"))), SenateCandidatePosition(groupLookup("J"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29141), name = Name("Quentin", "VON STIEGLITZ"), party = Some(Party("Palmer United Party"))), SenateCandidatePosition(groupLookup("G"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29124), name = Name("Max", "KAYE"), party = Some(Party("VOTEFLUX.ORG | Upgrade Democracy!"))), SenateCandidatePosition(groupLookup("O"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28595), name = Name("Lisa", "SINGH"), party = Some(Party("Australian Labor Party"))), SenateCandidatePosition(groupLookup("B"), 5)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28368), name = Name("Nick", "McKIM"), party = Some(Party("The Greens"))), SenateCandidatePosition(groupLookup("C"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28276), name = Name("Kevin", "HARKINS"), party = Some(Party("Australian Recreational Fishers Party"))), SenateCandidatePosition(groupLookup("S"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28187), name = Name("JoAnne", "VOLTA"), party = Some(Party("The Arts Party"))), SenateCandidatePosition(groupLookup("U"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28597), name = Name("Mishka", "GORA"), party = Some(Party("Christian Democratic Party (Fred Nile Group)"))), SenateCandidatePosition(groupLookup("D"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28596), name = Name("Silvana", "NERO-NILE"), party = Some(Party("Christian Democratic Party (Fred Nile Group)"))), SenateCandidatePosition(groupLookup("D"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28589), name = Name("Andrew", "ROBERTS"), party = Some(Party("Family First"))), SenateCandidatePosition(groupLookup("A"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28048), name = Name("Steve", "KUCINA"), party = Some(Party("Citizens Electoral Council"))), SenateCandidatePosition(groupLookup("K"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28592), name = Name("Carol", "BROWN"), party = Some(Party("Australian Labor Party"))), SenateCandidatePosition(groupLookup("B"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28186), name = Name("Scott", "O'HARA"), party = Some(Party("The Arts Party"))), SenateCandidatePosition(groupLookup("U"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28588), name = Name("Peter", "MADDEN"), party = Some(Party("Family First"))), SenateCandidatePosition(groupLookup("A"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28587), name = Name("Sharon", "JOYCE"), party = Some(Party("Renewable Energy Party"))), SenateCandidatePosition(groupLookup("L"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29560), name = Name("Karen", "BEVIS"), party = Some(Party("Animal Justice Party"))), SenateCandidatePosition(groupLookup("Q"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28581), name = Name("Stephen", "PARRY"), party = Some(Party("Liberal"))), SenateCandidatePosition(groupLookup("F"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28594), name = Name("John", "SHORT"), party = Some(Party("Australian Labor Party"))), SenateCandidatePosition(groupLookup("B"), 4)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28582), name = Name("Jonathon", "DUNIAM"), party = Some(Party("Liberal"))), SenateCandidatePosition(groupLookup("F"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28579), name = Name("Richard", "TEMBY"), party = Some(Party("Mature Australia"))), SenateCandidatePosition(groupLookup("UG"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28385), name = Name("Ian", "ALSTON"), party = Some(Party("Liberal Democrats"))), SenateCandidatePosition(groupLookup("T"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28598), name = Name("Michelle", "HOULT"), party = Some(Party("Nick Xenophon Team"))), SenateCandidatePosition(groupLookup("E"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28374), name = Name("Kate", "McCULLOCH"), party = Some(Party("Pauline Hanson's One Nation"))), SenateCandidatePosition(groupLookup("I"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28361), name = Name("Jacqui", "LAMBIE"), party = Some(Party("Jacqui Lambie Network"))), SenateCandidatePosition(groupLookup("M"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28363), name = Name("Steve", "MARTIN"), party = Some(Party("Jacqui Lambie Network"))), SenateCandidatePosition(groupLookup("M"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28593), name = Name("Catryna", "BILYK"), party = Some(Party("Australian Labor Party"))), SenateCandidatePosition(groupLookup("B"), 3)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29139), name = Name("Justin Leigh", "STRINGER"), party = Some(Party("Palmer United Party"))), SenateCandidatePosition(groupLookup("G"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28583), name = Name("David", "BUSHBY"), party = Some(Party("Liberal"))), SenateCandidatePosition(groupLookup("F"), 3)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29283), name = Name("Jin-oh", "CHOI"), party = Some(Party("Science Party"))), SenateCandidatePosition(groupLookup("R"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29288), name = Name("Matthew", "ALLEN"), party = Some(Party("Shooters, Fishers and Farmers"))), SenateCandidatePosition(groupLookup("P"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28601), name = Name("Daniel", "BAKER"), party = Some(Party("Derryn Hinch's Justice Party"))), SenateCandidatePosition(groupLookup("J"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28599), name = Name("Nicky", "COHEN"), party = Some(Party("Nick Xenophon Team"))), SenateCandidatePosition(groupLookup("E"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28381), name = Name("Clinton", "MEAD"), party = Some(Party("Liberal Democrats"))), SenateCandidatePosition(groupLookup("T"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28054), name = Name("George", "LANE"), party = Some(Party("party = None"))), SenateCandidatePosition(groupLookup("UG"), 4)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28584), name = Name("Richard", "COLBECK"), party = Some(Party("Liberal"))), SenateCandidatePosition(groupLookup("F"), 4)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28586), name = Name("Rob", "MANSON"), party = Some(Party("Renewable Energy Party"))), SenateCandidatePosition(groupLookup("L"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28591), name = Name("Helen", "POLLEY"), party = Some(Party("Australian Labor Party"))), SenateCandidatePosition(groupLookup("B"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29281), name = Name("Hans", "WILLINK"), party = Some(Party("Science Party"))), SenateCandidatePosition(groupLookup("R"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28375), name = Name("Natasia", "MANZI"), party = Some(Party("Pauline Hanson's One Nation"))), SenateCandidatePosition(groupLookup("I"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28585), name = Name("John", "TUCKER"), party = Some(Party("Liberal"))), SenateCandidatePosition(groupLookup("F"), 5)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28590), name = Name("Anne", "URQUHART"), party = Some(Party("Australian Labor Party"))), SenateCandidatePosition(groupLookup("B"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28801), name = Name("Susan", "HORWOOD"), party = Some(Party("Australian Liberty Alliance"))), SenateCandidatePosition(groupLookup("N"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29239), name = Name("Kaye", "MARSKELL"), party = Some(Party("party = None"))), SenateCandidatePosition(groupLookup("UG"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28370), name = Name("Anna", "REYNOLDS"), party = Some(Party("The Greens"))), SenateCandidatePosition(groupLookup("C"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28367), name = Name("Peter", "WHISH-WILSON"), party = Some(Party("The Greens"))), SenateCandidatePosition(groupLookup("C"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28365), name = Name("Rob", "WATERMAN"), party = Some(Party("Jacqui Lambie Network"))), SenateCandidatePosition(groupLookup("M"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29291), name = Name("Ricky", "MIDSON"), party = Some(Party("Shooters, Fishers and Farmers"))), SenateCandidatePosition(groupLookup("P"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29119), name = Name("Adam", "POULTON"), party = Some(Party("VOTEFLUX.ORG | Upgrade Democracy!"))), SenateCandidatePosition(groupLookup("O"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28277), name = Name("Carmen", "EVANS"), party = Some(Party("Australian Recreational Fishers Party"))), SenateCandidatePosition(groupLookup("S"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29135), name = Name("Kevin", "MORGAN"), party = Some(Party("Palmer United Party"))), SenateCandidatePosition(groupLookup("G"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28358), name = Name("David", "CRAWFORD"), party = Some(Party("Antipaedophile Party"))), SenateCandidatePosition(groupLookup("UG"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28047), name = Name("Meg", "THORNTON"), party = Some(Party("Citizens Electoral Council"))), SenateCandidatePosition(groupLookup("K"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29302), name = Name("Francesca", "COLLINS"), party = Some(Party("Australian Sex Party"))), SenateCandidatePosition(groupLookup("H"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29567), name = Name("Alison", "BAKER"), party = Some(Party("Animal Justice Party"))), SenateCandidatePosition(groupLookup("Q"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28767), name = Name("Grant", "RUSSELL"), party = Some(Party("party = None"))), SenateCandidatePosition(groupLookup("UG"), 3)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28800), name = Name("Tony", "ROBINSON"), party = Some(Party("Australian Liberty Alliance"))), SenateCandidatePosition(groupLookup("N"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29307), name = Name("Matt", "OWEN"), party = Some(Party("Marijuana (HEMP) Party"))), SenateCandidatePosition(groupLookup("H"), 1))
    )
  }

  object WA extends CandidateFixture {
    override val state: State = State.WA

    override val groupFixture: GroupFixture = GroupFixture.WA

    override lazy val candidates: Set[SenateCandidate] = Set(
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29437), name = Name("Mark David", "IMISIDES"),          party = Some(Party("Christian Democratic Party (Fred Nile Group)"))), SenateCandidatePosition(groupLookup("A"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29440), name = Name("Philip Campbell", "READ"),         party = Some(Party("Christian Democratic Party (Fred Nile Group)"))), SenateCandidatePosition(groupLookup("A"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28400), name = Name("Andrew", "SKERRITT"),              party = Some(Party("Shooters, Fishers and Farmers"))),                SenateCandidatePosition(groupLookup("B"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28401), name = Name("Ross", "WILLIAMSON"),              party = Some(Party("Shooters, Fishers and Farmers"))),                SenateCandidatePosition(groupLookup("B"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28527), name = Name("Luke", "BOLTON"),                  party = Some(Party("Nick Xenophon Team"))),                           SenateCandidatePosition(groupLookup("C"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28528), name = Name("Michael", "BOVELL"),               party = Some(Party("Nick Xenophon Team"))),                           SenateCandidatePosition(groupLookup("C"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28516), name = Name("Sue", "LINES"),                    party = Some(Party("Australian Labor Party"))),                       SenateCandidatePosition(groupLookup("D"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28517), name = Name("Glenn", "STERLE"),                 party = Some(Party("Australian Labor Party"))),                       SenateCandidatePosition(groupLookup("D"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28518), name = Name("Patrick", "DODSON"),               party = Some(Party("Australian Labor Party"))),                       SenateCandidatePosition(groupLookup("D"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28519), name = Name("Louise", "PRATT"),                 party = Some(Party("Australian Labor Party"))),                       SenateCandidatePosition(groupLookup("D"), 3)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28520), name = Name("Mark", "REED"),                    party = Some(Party("Australian Labor Party"))),                       SenateCandidatePosition(groupLookup("D"), 4)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28521), name = Name("Susan", "BOWERS"),                 party = Some(Party("Australian Labor Party"))),                       SenateCandidatePosition(groupLookup("D"), 5)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28522), name = Name("Mia", "ONORATO"),                  party = Some(Party("Australian Labor Party"))),                       SenateCandidatePosition(groupLookup("D"), 6)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28098), name = Name("Jean", "ROBINSON"),                party = Some(Party("Citizens Electoral Council of Australia"))),      SenateCandidatePosition(groupLookup("E"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28099), name = Name("Judy", "SUDHOLZ"),                 party = Some(Party("Citizens Electoral Council of Australia"))),      SenateCandidatePosition(groupLookup("E"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29189), name = Name("Kado", "MUIR"),                    party = Some(Party("The Nationals"))),                                SenateCandidatePosition(groupLookup("F"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29190), name = Name("Nick", "FARDELL"),                 party = Some(Party("The Nationals"))),                                SenateCandidatePosition(groupLookup("F"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29191), name = Name("Elizabeth", "RE"),                 party = Some(Party("The Nationals"))),                                SenateCandidatePosition(groupLookup("F"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28341), name = Name("Kamala", "EMANUEL"),               party = Some(Party("Socialist Alliance"))),                           SenateCandidatePosition(groupLookup("G"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28342), name = Name("Seamus", "DOHERTY"),               party = Some(Party("Socialist Alliance"))),                           SenateCandidatePosition(groupLookup("G"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28343), name = Name("Farida", "IQBAL"),                 party = Some(Party("Socialist Alliance"))),                           SenateCandidatePosition(groupLookup("G"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29604), name = Name("Nicki", "HIDE"),                   party = Some(Party("Derryn Hinch's Justice Party"))),                 SenateCandidatePosition(groupLookup("H"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29605), name = Name("Rachael", "HIGGINS"),              party = Some(Party("Derryn Hinch's Justice Party"))),                 SenateCandidatePosition(groupLookup("H"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29193), name = Name("Zhenya Dio", "WANG"),              party = Some(Party("Palmer United Party"))),                          SenateCandidatePosition(groupLookup("I"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29194), name = Name("Jacque", "KRUGER"),                party = Some(Party("Palmer United Party"))),                          SenateCandidatePosition(groupLookup("I"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28246), name = Name("Scott", "LUDLAM"),                 party = Some(Party("The Greens (WA)"))),                              SenateCandidatePosition(groupLookup("J"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28247), name = Name("Rachel", "SIEWERT"),               party = Some(Party("The Greens (WA)"))),                              SenateCandidatePosition(groupLookup("J"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28248), name = Name("Jordon", "STEELE-JOHN"),           party = Some(Party("The Greens (WA)"))),                              SenateCandidatePosition(groupLookup("J"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28249), name = Name("Samantha", "JENKINSON"),           party = Some(Party("The Greens (WA)"))),                              SenateCandidatePosition(groupLookup("J"), 3)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28250), name = Name("Michael", "BALDOCK"),              party = Some(Party("The Greens (WA)"))),                              SenateCandidatePosition(groupLookup("J"), 4)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28251), name = Name("Rai", "ISMAIL"),                   party = Some(Party("The Greens (WA)"))),                              SenateCandidatePosition(groupLookup("J"), 5)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28831), name = Name("Katrina", "LOVE"),                 party = Some(Party("Animal Justice Party"))),                         SenateCandidatePosition(groupLookup("K"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28834), name = Name("Alicia", "SUTTON"),                party = Some(Party("Animal Justice Party"))),                         SenateCandidatePosition(groupLookup("K"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28270), name = Name("Stuart", "DONALD"),                party = Some(Party("Mature Australia"))),                             SenateCandidatePosition(groupLookup("L"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28271), name = Name("Patti", "BRADSHAW"),               party = Some(Party("Mature Australia"))),                             SenateCandidatePosition(groupLookup("L"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28264), name = Name("Robert", "BURATTI"),               party = Some(Party("The Arts Party"))),                               SenateCandidatePosition(groupLookup("M"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28265), name = Name("Robert Kenneth Leslie", "TAYLOR"), party = Some(Party("The Arts Party"))),                               SenateCandidatePosition(groupLookup("M"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29197), name = Name("Peter", "MAH"),                    party = Some(Party("Australian Cyclists Party"))),                    SenateCandidatePosition(groupLookup("N"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29198), name = Name("Christopher John", "HOWARD"),      party = Some(Party("Australian Cyclists Party"))),                    SenateCandidatePosition(groupLookup("N"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28272), name = Name("Pedro", "SCHWINDT"),               party = Some(Party("Renewable Energy Party"))),                       SenateCandidatePosition(groupLookup("O"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28273), name = Name("Camilla", "SUNDBLADH"),            party = Some(Party("Renewable Energy Party"))),                       SenateCandidatePosition(groupLookup("O"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28402), name = Name("Anthony", "HARDWICK"),             party = Some(Party("Rise Up Australia Party"))),                      SenateCandidatePosition(groupLookup("Q"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28403), name = Name("Sheila", "MUNDY"),                 party = Some(Party("Rise Up Australia Party"))),                      SenateCandidatePosition(groupLookup("Q"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28523), name = Name("Debbie", "ROBINSON"),              party = Some(Party("Australian Liberty Alliance"))),                  SenateCandidatePosition(groupLookup("P"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28524), name = Name("Marion", "HERCOCK"),               party = Some(Party("Australian Liberty Alliance"))),                  SenateCandidatePosition(groupLookup("P"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29186), name = Name("Rodney Norman", "CULLETON"),       party = Some(Party("Pauline Hanson's One Nation"))),                  SenateCandidatePosition(groupLookup("R"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29187), name = Name("Peter", "GEORGIOU"),               party = Some(Party("Pauline Hanson's One Nation"))),                  SenateCandidatePosition(groupLookup("R"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29188), name = Name("Ioanna", "CULLETON"),              party = Some(Party("Pauline Hanson's One Nation"))),                  SenateCandidatePosition(groupLookup("R"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29607), name = Name("Michael", "BALDERSTONE"),          party = Some(Party("Marijuana (HEMP) Party"))),                       SenateCandidatePosition(groupLookup("S"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29608), name = Name("James", "HURLEY"),                 party = Some(Party("Australian Sex Party"))),                         SenateCandidatePosition(groupLookup("S"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28804), name = Name("Fernando", "BOVE"),                party = Some(Party("Democratic Labour Party"))),                      SenateCandidatePosition(groupLookup("T"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28807), name = Name("Troy", "KIERNAN"),                 party = Some(Party("Democratic Labour Party"))),                      SenateCandidatePosition(groupLookup("T"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28494), name = Name("Michaelia", "CASH"),               party = Some(Party("Liberal Party of Australia"))),                   SenateCandidatePosition(groupLookup("X"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28497), name = Name("Chris", "BACK"),                   party = Some(Party("Liberal Party of Australia"))),                   SenateCandidatePosition(groupLookup("X"), 4)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29395), name = Name("Sara", "FARGHER"),                 party = Some(Party("Health Australia Party"))),                       SenateCandidatePosition(groupLookup("U"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29360), name = Name("Samantha", "TILBURY"),             party = Some(Party("Health Australia Party"))),                       SenateCandidatePosition(groupLookup("U"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28864), name = Name("Stuey", "PAULL"),                  party = None),                                                     SenateCandidatePosition(groupLookup("V"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28865), name = Name("Gary J", "MORRIS"),                party = None),                                                     SenateCandidatePosition(groupLookup("V"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28821), name = Name("Lindsay", "CAMERON"),              party = Some(Party("Australian Christians"))),                        SenateCandidatePosition(groupLookup("W"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28825), name = Name("Jacky", "YOUNG"),                  party = Some(Party("Australian Christians"))),                        SenateCandidatePosition(groupLookup("W"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28493), name = Name("Mathias", "CORMANN"),              party = Some(Party("Liberal Party of Australia"))),                   SenateCandidatePosition(groupLookup("X"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28495), name = Name("Dean", "SMITH"),                   party = Some(Party("Liberal Party of Australia"))),                   SenateCandidatePosition(groupLookup("X"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28496), name = Name("Linda", "REYNOLDS"),               party = Some(Party("Liberal Party of Australia"))),                   SenateCandidatePosition(groupLookup("X"), 3)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28502), name = Name("Sheridan", "INGRAM"),              party = Some(Party("Liberal Party of Australia"))),                   SenateCandidatePosition(groupLookup("X"), 6)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28500), name = Name("David", "JOHNSTON"),               party = Some(Party("Liberal Party of Australia"))),                   SenateCandidatePosition(groupLookup("X"), 5)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28153), name = Name("Lyn", "VICKERY"),                  party = Some(Party("Australia First Party"))),                        SenateCandidatePosition(groupLookup("Y"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28155), name = Name("Brian", "McREA"),                  party = Some(Party("Australia First Party"))),                        SenateCandidatePosition(groupLookup("Y"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28514), name = Name("Graeme Michael", "KLASS"),         party = Some(Party("Liberal Democratic Party"))),                     SenateCandidatePosition(groupLookup("Z"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28515), name = Name("Connor", "WHITTLE"),               party = Some(Party("Liberal Democratic Party"))),                     SenateCandidatePosition(groupLookup("Z"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29200), name = Name("Richard", "THOMAS"),               party = Some(Party("VOTEFLUX.ORG | Upgrade Democracy!"))),            SenateCandidatePosition(groupLookup("AA"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29201), name = Name("Mark", "CONNOLLY"),                party = Some(Party("VOTEFLUX.ORG | Upgrade Democracy!"))),            SenateCandidatePosition(groupLookup("AA"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28525), name = Name("Linda", "ROSE"),                   party = Some(Party("Family First Party"))),                           SenateCandidatePosition(groupLookup("AB"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28526), name = Name("Henry", "HENG"),                   party = Some(Party("Family First Party"))),                           SenateCandidatePosition(groupLookup("AB"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28097), name = Name("Kai", "JONES"),                    party = None),                                                     SenateCandidatePosition(groupLookup("UG"), 0)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28279), name = Name("Tammara", "MOODY"),                party = Some(Party("Australian Antipaedophile Party"))),              SenateCandidatePosition(groupLookup("UG"), 1)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(28188), name = Name("Julie", "MATHESON"),               party = None),                                                     SenateCandidatePosition(groupLookup("UG"), 2)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29356), name = Name("Peter", "CASTIEAU"),               party = None),                                                     SenateCandidatePosition(groupLookup("UG"), 3)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29401), name = Name("Susan", "HODDINOTT"),              party = Some(Party("Katter's Australian Party"))),                    SenateCandidatePosition(groupLookup("UG"), 4)),
      SenateCandidate(election, SenateCandidateDetails(election, id = Candidate.Id(29428), name = Name("Norm", "RAMSAY"),                  party = None),                                                     SenateCandidatePosition(groupLookup("UG"), 5)),
    )
  }
}

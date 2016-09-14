package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.model._

object TestData {
  val aTestBallot: BallotWithPreferences = {
    val ballotId = "PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0="

    val ballot = BallotRow(ballotId = ballotId,
      electionId = "20499",
      state = "ACT",
      electorate = "Canberra",
      voteCollectionPointId = 4,
      batchNo = 10,
      paperNo = 30
    )

    val atlPreferences = Set(
      AtlPreferencesRow(ballotId, "C", Some(1), None),
      AtlPreferencesRow(ballotId, "J", Some(2), None),
      AtlPreferencesRow(ballotId, "H", Some(3), None),
      AtlPreferencesRow(ballotId, "G", Some(4), None),
      AtlPreferencesRow(ballotId, "D", Some(5), None),
      AtlPreferencesRow(ballotId, "E", Some(6), None)
    )

    val btlPreferences = Set(
      BtlPreferencesRow(ballotId, "I", 1, Some(11), None),
      BtlPreferencesRow(ballotId, "C", 1, Some(3), None),
      BtlPreferencesRow(ballotId, "I", 0, None, Some('/')),
      BtlPreferencesRow(ballotId, "B", 1, Some(1), None),
      BtlPreferencesRow(ballotId, "F", 1, Some(7), None),
      BtlPreferencesRow(ballotId, "G", 1, Some(9), None),
      BtlPreferencesRow(ballotId, "A", 0, None, Some('*')),
      BtlPreferencesRow(ballotId, "H", 0, Some(10), None),
      BtlPreferencesRow(ballotId, "G", 0, Some(8), None),
      BtlPreferencesRow(ballotId, "D", 0, Some(4), None),
      BtlPreferencesRow(ballotId, "D", 1, Some(5), None),
      BtlPreferencesRow(ballotId, "J", 0, Some(12), None),
      BtlPreferencesRow(ballotId, "E", 0, Some(6), None),
      BtlPreferencesRow(ballotId, "C", 0, Some(2), None)
    )

    val ballotFacts = BallotFactsRow(ballotId, 6, 12, false, true, Some(42), Some(42))

    BallotWithPreferences(ballot, ballotFacts, atlPreferences, btlPreferences)
  }

  val allNtGroups: Set[GroupsRow] = Set(
    GroupsRow("C", "20499", "NT", "Citizens Electoral Council of Australia"),
    GroupsRow("B", "20499", "NT", "Marijuana (HEMP) Party/Australian Sex Party"),
    GroupsRow("A", "20499", "NT", "Rise Up Australia Party"),
    GroupsRow("D", "20499", "NT", "The Greens"),
    GroupsRow("F", "20499", "NT", "Australian Labor Party (Northern Territory) Branch"),
    GroupsRow("E", "20499", "NT", "Country Liberals (NT)"),
    GroupsRow("G", "20499", "NT", "Christian Democratic Party (Fred Nile Group)")
  )

  val allNtCandidates: Set[CandidatesRow] = Set(
    CandidatesRow("28559", "20499", "NT", "A", 1, "GIMINI, Jimmy", "Rise Up Australia Party"),
    CandidatesRow("28546", "20499", "NT", "D", 1, "BANNISTER, Kathy", "The Greens"),
    CandidatesRow("29602", "20499", "NT", "B", 1, "JONES, Timothy", "Australian Sex Party"),
    CandidatesRow("29597", "20499", "NT", "UG", 2, "RYAN, Maurie Japarta", "Independent"),
    CandidatesRow("29140", "20499", "NT", "UG", 3, "MacDONALD, Marney", "Antipaedophile Party"),
    CandidatesRow("28574", "20499", "NT", "G", 1, "ORDISH, John", "Christian Democratic Party (Fred Nile Group)"),
    CandidatesRow("28575", "20499", "NT", "F", 0, "McCARTHY, Malarndirri", "Australian Labor Party (Northern Territory) Branch"),
    CandidatesRow("28558", "20499", "NT", "A", 0, "PILE, Jan", "Rise Up Australia Party"),
    CandidatesRow("28573", "20499", "NT", "G", 0, "ORDISH, Carol", "Christian Democratic Party (Fred Nile Group)"),
    CandidatesRow("29596", "20499", "NT", "UG", 4, "STRETTLES, Greg", "Independent"),
    CandidatesRow("28544", "20499", "NT", "D", 0, "CONNARD, Michael", "The Greens"),
    CandidatesRow("28820", "20499", "NT", "E", 0, "SCULLION, Nigel", "Country Liberals (NT)"),
    CandidatesRow("28004", "20499", "NT", "C", 1, "BARRY, Ian", "Citizens Electoral Council"),
    CandidatesRow("28822", "20499", "NT", "E", 1, "LILLIS, Jenni", "Country Liberals (NT)"),
    CandidatesRow("29145", "20499", "NT", "UG", 1, "MARSHALL, Tristan", "Online Direct Democracy - (Empowering the People!)"),
    CandidatesRow("29601", "20499", "NT", "B", 0, "KAVASILAS, Andrew", "Marijuana (HEMP) Party"),
    CandidatesRow("28576", "20499", "NT", "F", 1, "HONAN, Pat", "Australian Labor Party (Northern Territory) Branch"),
    CandidatesRow("28538", "20499", "NT", "UG", 0, "LEE, TS", "Independent"),
    CandidatesRow("28003", "20499", "NT", "C", 0, "CAMPBELL, Trudy", "Citizens Electoral Council")
  )

  val ntGroupsAndCandidates = GroupsAndCandidates(allNtGroups, allNtCandidates)

  val allActGroups = Set(
    GroupsRow("A", "20499", "ACT", "Liberal Democratic Party"),
    GroupsRow("B", "20499", "ACT", "Secular Party of Australia"),
    GroupsRow("C", "20499", "ACT", "Australian Labor Party"),
    GroupsRow("E", "20499", "ACT", "Sustainable Australia"),
    GroupsRow("D", "20499", "ACT", "Rise Up Australia Party"),
    GroupsRow("F", "20499", "ACT", "Liberal"),
    GroupsRow("G", "20499", "ACT", "Animal Justice Party"),
    GroupsRow("H", "20499", "ACT", "The Greens"),
    GroupsRow("I", "20499", "ACT", "Christian Democratic Party (Fred Nile Group)"),
    GroupsRow("J", "20499", "ACT", "Australian Sex Party")
  )

  val allActCandidates = Set(
    CandidatesRow("29611", "20499", "ACT", "A", 0,    "DONNELLY, Matt", "Liberal Democrats"),
    CandidatesRow("29612", "20499", "ACT", "A", 1,    "HENNINGS, Cawley", "Liberal Democrats"),
    CandidatesRow("28933", "20499", "ACT", "B", 0,    "EDWARDS, David", "Secular Party of Australia"),
    CandidatesRow("28937", "20499", "ACT", "B", 1,    "MIHALJEVIC, Denis", "Secular Party of Australia"),
    CandidatesRow("28147", "20499", "ACT", "C", 0,    "GALLAGHER, Katy", "Australian Labor Party"),
    CandidatesRow("28149", "20499", "ACT", "C", 1,    "SMITH, David", "Australian Labor Party"),
    CandidatesRow("29514", "20499", "ACT", "D", 0,    "O'CONNOR, Sandie", "Rise Up Australia Party"),
    CandidatesRow("29518", "20499", "ACT", "D", 1,    "WYATT, Jess", "Rise Up Australia Party"),
    CandidatesRow("28468", "20499", "ACT", "E", 0,    "HAYDON, John", "Sustainable Australia"),
    CandidatesRow("28469", "20499", "ACT", "E", 1,    "TYE, Martin", "Sustainable Australia"),
    CandidatesRow("28773", "20499", "ACT", "F", 0,    "SESELJA, Zed", "Liberal"),
    CandidatesRow("28782", "20499", "ACT", "F", 1,    "HIATT, Jane", "Liberal"),
    CandidatesRow("28254", "20499", "ACT", "G", 0,    "FIELD, Deborah", "Animal Justice Party"),
    CandidatesRow("28256", "20499", "ACT", "G", 1,    "MONTAGNE, Jessica", "Animal Justice Party"),
    CandidatesRow("28306", "20499", "ACT", "H", 0,    "HOBBS, Christina", "The Greens"),
    CandidatesRow("28308", "20499", "ACT", "H", 1,    "WAREHAM, Sue", "The Greens"),
    CandidatesRow("28760", "20499", "ACT", "I", 0,    "KIM, David William", "Christian Democratic Party (Fred Nile Group)"),
    CandidatesRow("28763", "20499", "ACT", "I", 1,    "TADROS, Elizabeth", "Christian Democratic Party (Fred Nile Group)"),
    CandidatesRow("29390", "20499", "ACT", "J", 0,    "BAILEY, Steven", "Australian Sex Party"),
    CandidatesRow("29391", "20499", "ACT", "J", 1,    "SWAN, Robbie", "Australian Sex Party"),
    CandidatesRow("29520", "20499", "ACT", "UG", 0,   "HAY, Michael Gerard", "VOTEFLUX.ORG | Upgrade Democracy!"),
    CandidatesRow("28150", "20499", "ACT", "UG", 1,   "HANSON, Anthony", "Mature Australia")
  )

  val actGroupsAndCandidates = GroupsAndCandidates(allActGroups, allActCandidates)

  val allTasCandidates: Set[CandidatesRow] = Set(
    CandidatesRow("28580", "20499", "TAS", "F", 0, "ABETZ, Eric", "Liberal"), 
    CandidatesRow("28600", "20499", "TAS", "J", 0, "CASS, Suzanne", "Derryn Hinch's Justice Party"), 
    CandidatesRow("29141", "20499", "TAS", "G", 2, "VON STIEGLITZ, Quentin", "Palmer United Party"), 
    CandidatesRow("29124", "20499", "TAS", "O", 1, "KAYE, Max", "VOTEFLUX.ORG | Upgrade Democracy!"), 
    CandidatesRow("28595", "20499", "TAS", "B", 5, "SINGH, Lisa", "Australian Labor Party"),
    CandidatesRow("28368", "20499", "TAS", "C", 1, "McKIM, Nick", "The Greens"),
    CandidatesRow("28276", "20499", "TAS", "S", 0, "HARKINS, Kevin", "Australian Recreational Fishers Party"),
    CandidatesRow("28187", "20499", "TAS", "U", 1, "VOLTA, JoAnne", "The Arts Party"),
    CandidatesRow("28597", "20499", "TAS", "D", 1, "GORA, Mishka", "Christian Democratic Party (Fred Nile Group)"),
    CandidatesRow("28596", "20499", "TAS", "D", 0, "NERO-NILE, Silvana", "Christian Democratic Party (Fred Nile Group)"),
    CandidatesRow("28589", "20499", "TAS", "A", 1, "ROBERTS, Andrew", "Family First"),
    CandidatesRow("28048", "20499", "TAS", "K", 1, "KUCINA, Steve", "Citizens Electoral Council"),
    CandidatesRow("28592", "20499", "TAS", "B", 2, "BROWN, Carol", "Australian Labor Party"),
    CandidatesRow("28186", "20499", "TAS", "U", 0, "O'HARA, Scott", "The Arts Party"),
    CandidatesRow("28588", "20499", "TAS", "A", 0, "MADDEN, Peter", "Family First"),
    CandidatesRow("28587", "20499", "TAS", "L", 1, "JOYCE, Sharon", "Renewable Energy Party"),
    CandidatesRow("29560", "20499", "TAS", "Q", 0, "BEVIS, Karen", "Animal Justice Party"),
    CandidatesRow("28581", "20499", "TAS", "F", 1, "PARRY, Stephen", "Liberal"),
    CandidatesRow("28594", "20499", "TAS", "B", 4, "SHORT, John", "Australian Labor Party"),
    CandidatesRow("28582", "20499", "TAS", "F", 2, "DUNIAM, Jonathon", "Liberal"),
    CandidatesRow("28579", "20499", "TAS", "UG", 2, "TEMBY, Richard", "Mature Australia"),
    CandidatesRow("28385", "20499", "TAS", "T", 1, "ALSTON, Ian", "Liberal Democrats"),
    CandidatesRow("28598", "20499", "TAS", "E", 0, "HOULT, Michelle", "Nick Xenophon Team"),
    CandidatesRow("28374", "20499", "TAS", "I", 0, "McCULLOCH, Kate", "Pauline Hanson's One Nation"),
    CandidatesRow("28361", "20499", "TAS", "M", 0, "LAMBIE, Jacqui", "Jacqui Lambie Network"),
    CandidatesRow("28363", "20499", "TAS", "M", 1, "MARTIN, Steve", "Jacqui Lambie Network"),
    CandidatesRow("28593", "20499", "TAS", "B", 3, "BILYK, Catryna", "Australian Labor Party"),
    CandidatesRow("29139", "20499", "TAS", "G", 1, "STRINGER, Justin Leigh", "Palmer United Party"),
    CandidatesRow("28583", "20499", "TAS", "F", 3, "BUSHBY, David", "Liberal"),
    CandidatesRow("29283", "20499", "TAS", "R", 1, "CHOI, Jin-oh", "Science Party"),
    CandidatesRow("29288", "20499", "TAS", "P", 0, "ALLEN, Matthew", "Shooters, Fishers and Farmers"),
    CandidatesRow("28601", "20499", "TAS", "J", 1, "BAKER, Daniel", "Derryn Hinch's Justice Party"),
    CandidatesRow("28599", "20499", "TAS", "E", 1, "COHEN, Nicky", "Nick Xenophon Team"),
    CandidatesRow("28381", "20499", "TAS", "T", 0, "MEAD, Clinton", "Liberal Democrats"),
    CandidatesRow("28054", "20499", "TAS", "UG", 4, "LANE, George", "Independent"),
    CandidatesRow("28584", "20499", "TAS", "F", 4, "COLBECK, Richard", "Liberal"),
    CandidatesRow("28586", "20499", "TAS", "L", 0, "MANSON, Rob", "Renewable Energy Party"),
    CandidatesRow("28591", "20499", "TAS", "B", 1, "POLLEY, Helen", "Australian Labor Party"),
    CandidatesRow("29281", "20499", "TAS", "R", 0, "WILLINK, Hans", "Science Party"),
    CandidatesRow("28375", "20499", "TAS", "I", 1, "MANZI, Natasia", "Pauline Hanson's One Nation"),
    CandidatesRow("28585", "20499", "TAS", "F", 5, "TUCKER, John", "Liberal"),
    CandidatesRow("28590", "20499", "TAS", "B", 0, "URQUHART, Anne", "Australian Labor Party"),
    CandidatesRow("28801", "20499", "TAS", "N", 1, "HORWOOD, Susan", "Australian Liberty Alliance"),
    CandidatesRow("29239", "20499", "TAS", "UG", 1, "MARSKELL, Kaye", "Independent"),
    CandidatesRow("28370", "20499", "TAS", "C", 2, "REYNOLDS, Anna", "The Greens"),
    CandidatesRow("28367", "20499", "TAS", "C", 0, "WHISH-WILSON, Peter", "The Greens"),
    CandidatesRow("28365", "20499", "TAS", "M", 2, "WATERMAN, Rob", "Jacqui Lambie Network"),
    CandidatesRow("29291", "20499", "TAS", "P", 1, "MIDSON, Ricky", "Shooters, Fishers and Farmers"),
    CandidatesRow("29119", "20499", "TAS", "O", 0, "POULTON, Adam", "VOTEFLUX.ORG | Upgrade Democracy!"),
    CandidatesRow("28277", "20499", "TAS", "S", 1, "EVANS, Carmen", "Australian Recreational Fishers Party"),
    CandidatesRow("29135", "20499", "TAS", "G", 0, "MORGAN, Kevin", "Palmer United Party"),
    CandidatesRow("28358", "20499", "TAS", "UG", 0, "CRAWFORD, David", "Antipaedophile Party"),
    CandidatesRow("28047", "20499", "TAS", "K", 0, "THORNTON, Meg", "Citizens Electoral Council"),
    CandidatesRow("29302", "20499", "TAS", "H", 0, "COLLINS, Francesca", "Australian Sex Party"),
    CandidatesRow("29567", "20499", "TAS", "Q", 1, "BAKER, Alison", "Animal Justice Party"),
    CandidatesRow("28767", "20499", "TAS", "UG", 3, "RUSSELL, Grant", "Independent"),
    CandidatesRow("28800", "20499", "TAS", "N", 0, "ROBINSON, Tony", "Australian Liberty Alliance"),
    CandidatesRow("29307", "20499", "TAS", "H", 1, "OWEN, Matt", "Marijuana (HEMP) Party")
  )
}

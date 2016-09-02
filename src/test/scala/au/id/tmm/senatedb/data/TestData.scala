package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.model._

private[data] object TestData {
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

    val ballotFacts = BallotFactsRow(ballotId, 6, 12, false, true)

    BallotWithPreferences(ballot, ballotFacts, atlPreferences, btlPreferences)
  }

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
    CandidatesRow("28937", "20499", "ACT", "B", 1,    "MIHALJEVIC, Denis", "Secular Party of Australia"),
    CandidatesRow("28147", "20499", "ACT", "C", 0,    "GALLAGHER, Katy", "Australian Labor Party"),
    CandidatesRow("29390", "20499", "ACT", "J", 0,    "BAILEY, Steven", "Australian Sex Party"),
    CandidatesRow("28256", "20499", "ACT", "G", 1,    "MONTAGNE, Jessica", "Animal Justice Party"),
    CandidatesRow("29391", "20499", "ACT", "J", 1,    "SWAN, Robbie", "Australian Sex Party"),
    CandidatesRow("28149", "20499", "ACT", "C", 1,    "SMITH, David", "Australian Labor Party"),
    CandidatesRow("28306", "20499", "ACT", "H", 0,    "HOBBS, Christina", "The Greens"),
    CandidatesRow("28933", "20499", "ACT", "B", 0,    "EDWARDS, David", "Secular Party of Australia"),
    CandidatesRow("28308", "20499", "ACT", "H", 1,    "WAREHAM, Sue", "The Greens"),
    CandidatesRow("28468", "20499", "ACT", "E", 0,    "HAYDON, John", "Sustainable Australia"),
    CandidatesRow("28254", "20499", "ACT", "G", 0,    "FIELD, Deborah", "Animal Justice Party"),
    CandidatesRow("28150", "20499", "ACT", "UG", 1,   "HANSON, Anthony", "Mature Australia"),
    CandidatesRow("29612", "20499", "ACT", "A", 1,    "HENNINGS, Cawley", "Liberal Democrats"),
    CandidatesRow("28782", "20499", "ACT", "F", 1,    "HIATT, Jane", "Liberal"),
    CandidatesRow("28469", "20499", "ACT", "E", 1,    "TYE, Martin", "Sustainable Australia"),
    CandidatesRow("28760", "20499", "ACT", "I", 0,    "KIM, David William", "Christian Democratic Party (Fred Nile Group)"),
    CandidatesRow("29520", "20499", "ACT", "UG", 0,   "HAY, Michael Gerard", "VOTEFLUX.ORG | Upgrade Democracy!"),
    CandidatesRow("28773", "20499", "ACT", "F", 0,    "SESELJA, Zed", "Liberal"),
    CandidatesRow("29611", "20499", "ACT", "A", 0,    "DONNELLY, Matt", "Liberal Democrats"),
    CandidatesRow("29518", "20499", "ACT", "D", 1,    "WYATT, Jess", "Rise Up Australia Party"),
    CandidatesRow("29514", "20499", "ACT", "D", 0,    "O'CONNOR, Sandie", "Rise Up Australia Party"),
    CandidatesRow("28763", "20499", "ACT", "I", 1,    "TADROS, Elizabeth", "Christian Democratic Party (Fred Nile Group)")
  )

}

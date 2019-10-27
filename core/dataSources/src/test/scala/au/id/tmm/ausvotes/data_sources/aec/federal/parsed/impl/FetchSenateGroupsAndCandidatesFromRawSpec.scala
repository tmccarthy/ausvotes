package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl

import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawSenateFirstPreferences
import au.id.tmm.ausvotes.model.federal.senate.SenateElection.`2016`
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.stv.BallotGroup
import au.id.tmm.ausvotes.model.{CandidateDetails, Name, Party}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.fs2interop._
import au.id.tmm.ausgeo.State
import au.id.tmm.utilities.testing.syntax._
import org.scalatest.FlatSpec
import fs2.Stream

class FetchSenateGroupsAndCandidatesFromRawSpec extends FlatSpec {

  private lazy val rawGroupRow = FetchRawSenateFirstPreferences.Row("ACT", " C", "29663", 0, "C Ticket Votes", "Australian Labor Party",
    73455,1120,527,3509,3640,82251)

  private lazy val rawGroupRowWithoutParty = FetchRawSenateFirstPreferences.Row("VIC","B","29710",0,"B Ticket Votes","",
    2110,214,62,140,316,2842)

  private lazy val rawGroupedCandidateRow = FetchRawSenateFirstPreferences.Row("ACT"," C","28147",1,"GALLAGHER, Katy","Australian Labor Party",
    11942,143,64,730,619,13498)

  private lazy val rawUngroupedCandidateRow = FetchRawSenateFirstPreferences.Row("ACT","UG","28150",2,"HANSON, Anthony","Mature Australia",
    264,3,0,18,23,308)

  private lazy val rawIndependentCandidateRow = FetchRawSenateFirstPreferences.Row("NT","UG","28538",1,"LEE, TS","Independent",250,1,1,10,19,281)

  private lazy val rawGroupedCandidateWithoutParty = FetchRawSenateFirstPreferences.Row("VIC","B","29589",1,"COLLYER, David","",
    366,30,12,27,28,463)

  private lazy val rows = Vector(
    rawGroupRow,
    rawGroupedCandidateRow,
    rawGroupRowWithoutParty,
    rawGroupedCandidateWithoutParty,
    rawUngroupedCandidateRow,
    rawIndependentCandidateRow
  )

  private implicit val fetchRawSenateFirstPreferences: FetchRawSenateFirstPreferences[BasicTestIO] =
  {
    case SenateElection.`2016` => BME.pure[BasicTestIO, Stream[BasicTestIO[Throwable, +?], FetchRawSenateFirstPreferences.Row]](Stream(
      rows: _*,
    ))
    case _ => BME.leftPure[BasicTestIO, FetchRawSenateFirstPreferences.Error](FetchRawSenateFirstPreferences.Error(new RuntimeException("No data")))
  }

  private val fetcherUnderTest: FetchSenateGroupsAndCandidatesFromRaw[BasicTestIO] =
    FetchSenateGroupsAndCandidatesFromRaw[BasicTestIO]


  behavior of "the group and candidate generator"

  it should "generate a group" in {
    val expectedGroup = SenateGroup(`2016`.electionForState(State.ACT).get, BallotGroup.Code("C").get, Some(Party("Australian Labor Party"))).get

    val groupsAndCandidates = fetcherUnderTest.senateGroupsAndCandidatesFor(`2016`)
        .runEither(BasicTestData())
        .get

    assert(groupsAndCandidates.groups contains expectedGroup)
  }

  it should "generate a group without a party" in {
    val expectedGroup = SenateGroup(`2016`.electionForState(State.VIC).get, BallotGroup.Code("B").get, None).get

    val groupsAndCandidates = fetcherUnderTest.senateGroupsAndCandidatesFor(`2016`)
        .runEither(BasicTestData())
        .get

    assert(groupsAndCandidates.groups contains expectedGroup)
  }

  it should "generate a grouped candidate" in {
    val expectedCandidate = CandidateFixture.ACT.candidateWithId(28147)

    val groupsAndCandidates = fetcherUnderTest.senateGroupsAndCandidatesFor(`2016`)
        .runEither(BasicTestData())
        .get

    assert(groupsAndCandidates.candidates contains expectedCandidate)
  }

  it should "generate an ungrouped candidate" in {
    val expectedCandidate = CandidateFixture.ACT.candidateWithId(28150)

    val groupsAndCandidates = fetcherUnderTest.senateGroupsAndCandidatesFor(`2016`)
        .runEither(BasicTestData())
        .get

    assert(groupsAndCandidates.candidates contains expectedCandidate)
  }

  it should "generate an independent candidate" in {
    val expectedCandidate = CandidateFixture.NT.candidateWithId(28538)

    val groupsAndCandidates = fetcherUnderTest.senateGroupsAndCandidatesFor(`2016`)
        .runEither(BasicTestData())
        .get

    assert(groupsAndCandidates.candidates contains expectedCandidate)
  }

  it should "generate a grouped, independent candidate" in {
    val election = `2016`.electionForState(State.VIC).get

    val expectedCandidate = SenateCandidate(
      election = election,
      SenateCandidateDetails(
        election,
        name = Name("David", "COLLYER"),
        party = None,
        id = CandidateDetails.Id(29589),
      ),
      position = SenateCandidatePosition(SenateGroup(election, BallotGroup.Code("B").get, None).get, 0),
    )

    val groupsAndCandidates = fetcherUnderTest.senateGroupsAndCandidatesFor(`2016`)
        .runEither(BasicTestData())
        .get

    assert(groupsAndCandidates.candidates contains expectedCandidate)
  }

  it should "flyweight generated groups" in {
    val groupsAndCandidates = fetcherUnderTest.senateGroupsAndCandidatesFor(`2016`)
      .runEither(BasicTestData())
      .get

    val laborGroup = groupsAndCandidates.groups.filter(_.party.contains(Party("Australian Labor Party"))).head
    val laborCandidate = groupsAndCandidates.candidates.filter(_.candidateDetails.party.contains(Party("Australian Labor Party"))).head

    assert(laborGroup eq laborCandidate.position.group)
  }

}

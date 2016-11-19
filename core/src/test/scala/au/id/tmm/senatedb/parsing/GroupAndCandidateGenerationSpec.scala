package au.id.tmm.senatedb.parsing

import au.id.tmm.senatedb.fixtures.Candidates
import au.id.tmm.senatedb.model.SenateElection.`2016`
import au.id.tmm.senatedb.model.parsing.Party.{Independent, RegisteredParty}
import au.id.tmm.senatedb.model.parsing._
import au.id.tmm.senatedb.rawdata.model.FirstPreferencesRow
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class GroupAndCandidateGenerationSpec extends ImprovedFlatSpec {

  private lazy val rawGroupRow = FirstPreferencesRow("ACT", " C", "29663", 0, "C Ticket Votes", "Australian Labor Party",
    73455,1120,527,3509,3640,82251)

  private lazy val rawGroupRowWithoutParty = FirstPreferencesRow("VIC","B","29710",0,"B Ticket Votes","",
    2110,214,62,140,316,2842)

  private lazy val rawGroupedCandidateRow = FirstPreferencesRow("ACT"," C","28147",1,"GALLAGHER, Katy","Australian Labor Party",
    11942,143,64,730,619,13498)

  private lazy val rawUngroupedCandidateRow = FirstPreferencesRow("ACT","UG","28150",2,"HANSON, Anthony","Mature Australia",
    264,3,0,18,23,308)

  private lazy val rawIndependentCandidateRow = FirstPreferencesRow("NT","UG","28538",1,"LEE, TS","Independent",250,1,1,10,19,281)

  private lazy val rawGroupedCandidateWithoutParty = FirstPreferencesRow("VIC","B","29589",1,"COLLYER, David","",
    366,30,12,27,28,463)

  private lazy val rows = Vector(
    rawGroupRow,
    rawGroupedCandidateRow,
    rawGroupRowWithoutParty,
    rawGroupedCandidateWithoutParty,
    rawUngroupedCandidateRow,
    rawIndependentCandidateRow
  )

  behaviour of "the group and candidate generator"

  it should "generate a group" in {
    val expectedGroup = Group(`2016`, State.ACT, "C", RegisteredParty("Australian Labor Party"))

    val groupsAndCandidates = GroupAndCandidateGeneration.fromFirstPreferencesRows(`2016`, rows)

    assert(groupsAndCandidates contains expectedGroup)
  }

  it should "generate a group without a party" in {
    val expectedGroup = Group(`2016`, State.VIC, "B", Independent)

    val groupsAndCandidates = GroupAndCandidateGeneration.fromFirstPreferencesRows(`2016`, rows)

    assert(groupsAndCandidates contains expectedGroup)
  }

  it should "generate a grouped candidate" in {
    val expectedCandidate = Candidates.ACT.candidateWithId("28147")

    val groupsAndCandidates = GroupAndCandidateGeneration.fromFirstPreferencesRows(`2016`, rows)

    assert(groupsAndCandidates contains expectedCandidate)
  }

  it should "generate an ungrouped candidate" in {
    val expectedCandidate = Candidates.ACT.candidateWithId("28150")

    val groupsAndCandidates = GroupAndCandidateGeneration.fromFirstPreferencesRows(`2016`, rows)

    assert(groupsAndCandidates contains expectedCandidate)
  }

  it should "generate an independent candidate" in {
    val expectedCandidate = Candidates.NT.candidateWithId("28538")

    val groupsAndCandidates = GroupAndCandidateGeneration.fromFirstPreferencesRows(`2016`, rows)

    assert(groupsAndCandidates contains expectedCandidate)
  }

  it should "generate a grouped, independent candidate" in {
    val expectedCandidate = Candidate(
      election = `2016`,
      state = State.VIC,
      aecId = "29589",
      name = Name("David", "COLLYER"),
      party = Independent,
      btlPosition = CandidatePosition(Group(`2016`, State.VIC, "B", Independent), 0))

    val groupsAndCandidates = GroupAndCandidateGeneration.fromFirstPreferencesRows(`2016`, rows)

    assert(groupsAndCandidates contains expectedCandidate)
  }

  it should "flyweight generated parties" in {
    val groupsAndCandidates = GroupAndCandidateGeneration.fromFirstPreferencesRows(`2016`, rows)

    val laborGroup = groupsAndCandidates.groups.filter(_.party == RegisteredParty("Australian Labor Party")).head
    val laborCandidate = groupsAndCandidates.candidates.filter(_.party == RegisteredParty("Australian Labor Party")).head

    assert(laborGroup.party eq laborCandidate.party)
  }

  it should "flyweight generated groups" in {
    val groupsAndCandidates = GroupAndCandidateGeneration.fromFirstPreferencesRows(`2016`, rows)

    val laborGroup = groupsAndCandidates.groups.filter(_.party == RegisteredParty("Australian Labor Party")).head
    val laborCandidate = groupsAndCandidates.candidates.filter(_.party == RegisteredParty("Australian Labor Party")).head

    assert(laborGroup eq laborCandidate.btlPosition.group)
  }
}

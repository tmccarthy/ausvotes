package au.id.tmm.senatedb.parsing.countdata

import au.id.tmm.senatedb.fixtures.{Ballots, GroupsAndCandidates, MockAecResourceStore}
import au.id.tmm.senatedb.model.CountStep._
import au.id.tmm.senatedb.model.{CountData, SenateElection}
import au.id.tmm.senatedb.rawdata.RawDataStore
import au.id.tmm.utilities.collection.OrderedSet
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.resources.ManagedResourceUtils.ExtractableManagedResourceOps
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDataGenerationSpec extends ImprovedFlatSpec {

  private val rawDataStore = RawDataStore(MockAecResourceStore)
  private val election = SenateElection.`2016`
  private val groupsAndCandidates = GroupsAndCandidates.ACT.groupsAndCandidates

  private val state = State.ACT

  private val ballotMaker = Ballots.ACT.ballotMaker
  import ballotMaker.candidatePosition

  private lazy val actualCountData = resource.managed(rawDataStore.distributionsOfPreferencesFor(election, state))
    .map(CountDataGeneration.fromDistributionOfPreferencesRows(election, state, groupsAndCandidates, _))
    .toTry
    .get

  "the generated count data" should "have the correct initial allocation" in {
    val expectedCandidateTransfers = Map(
      candidatePosition("A0") -> CountStepTransfer(7371,7371,7371),
      candidatePosition("A1") -> CountStepTransfer(89,89,89),
      candidatePosition("B0") -> CountStepTransfer(1322,1322,1322),
      candidatePosition("B1") -> CountStepTransfer(56,56,56),
      candidatePosition("C0") -> CountStepTransfer(95749,95749,95749),
      candidatePosition("C1") -> CountStepTransfer(918,918,918),
      candidatePosition("D0") -> CountStepTransfer(2455,2455,2455),
      candidatePosition("D1") -> CountStepTransfer(68,68,68),
      candidatePosition("E0") -> CountStepTransfer(2557,2557,2557),
      candidatePosition("E1") -> CountStepTransfer(121,121,121),
      candidatePosition("F0") -> CountStepTransfer(82932,82932,82932),
      candidatePosition("F1") -> CountStepTransfer(1683,1683,1683),
      candidatePosition("G0") -> CountStepTransfer(4150,4150,4150),
      candidatePosition("G1") -> CountStepTransfer(101,101,101),
      candidatePosition("H0") -> CountStepTransfer(40424,40424,40424),
      candidatePosition("H1") -> CountStepTransfer(582,582,582),
      candidatePosition("I0") -> CountStepTransfer(3011,3011,3011),
      candidatePosition("I1") -> CountStepTransfer(76,76,76),
      candidatePosition("J0") -> CountStepTransfer(9744,9744,9744),
      candidatePosition("J1") -> CountStepTransfer(352,352,352),
      candidatePosition("UG0") -> CountStepTransfer(698,698,698),
      candidatePosition("UG1") -> CountStepTransfer(308,308,308)
    )

    val expectedExhaustedTransfer = CountStepTransfer(0, 0, 0)
    val expectedGainLossTransfer = CountStepTransfer(0, 0, 0)

    val expectedInitialAllocation = InitialAllocation(
      expectedCandidateTransfers,
      expectedExhaustedTransfer,
      expectedGainLossTransfer,
      OrderedSet(candidatePosition("C0"))
    )

    assert(actualCountData.initialAllocation === expectedInitialAllocation)
  }

  it should "have the correct number of formal ballots" in {
    assert(actualCountData.totalFormalPapers === 254767)
  }

  it should "have the correct quota" in {
    assert(actualCountData.quota === 84923)
  }

  it should "have the correct 8th distribution step" in {
    val expectedCandidateTransfers = Map(
      candidatePosition("A0") -> CountStepTransfer(0, 0, 7385),
      candidatePosition("A1") -> CountStepTransfer(0, 0, 93),
      candidatePosition("B0") -> CountStepTransfer(0, 0, 1354),
      candidatePosition("B1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("C0") -> CountStepTransfer(0, 0, 84923),
      candidatePosition("C1") -> CountStepTransfer(0, 0, 11262),
      candidatePosition("D0") -> CountStepTransfer(0, 0, 2520),
      candidatePosition("D1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("E0") -> CountStepTransfer(0, 0, 2577),
      candidatePosition("E1") -> CountStepTransfer(0, 0, 130),
      candidatePosition("F0") -> CountStepTransfer(0, 0, 83006),
      candidatePosition("F1") -> CountStepTransfer(0, 0, 1705),
      candidatePosition("G0") -> CountStepTransfer(0, 0, 4162),
      candidatePosition("G1") -> CountStepTransfer(0, 0, 106),
      candidatePosition("H0") -> CountStepTransfer(3, 0, 40721),
      candidatePosition("H1") -> CountStepTransfer(0, 0, 595),
      candidatePosition("I0") -> CountStepTransfer(1, 0, 3074),
      candidatePosition("I1") -> CountStepTransfer(-4, 0, 0),
      candidatePosition("J0") -> CountStepTransfer(0, 0, 9763),
      candidatePosition("J1") -> CountStepTransfer(0, 0, 360),
      candidatePosition("UG0") -> CountStepTransfer(0, 0, 704),
      candidatePosition("UG1") -> CountStepTransfer(0, 0, 314)
    )

    val expectedExhaustedTransfer = CountStepTransfer(0, 0, 0)
    val expectedGainLossTransfer = CountStepTransfer(0, 0, 13)

    val expectedDistributionSource = DistributionSource(candidatePosition("I1"), DistributionReason.EXCLUSION, Set(2), 0.113066455002141d)

    val expectedDistributionStep = DistributionStep(
      count = 8,
      source = expectedDistributionSource,
      candidateTransfers = expectedCandidateTransfers,
      exhaustedTransfer = expectedExhaustedTransfer,
      gainLossTransfer = expectedGainLossTransfer,
      electedThisCount = OrderedSet(),
      excludedThisCount = Some(candidatePosition("A1")),
      elected = OrderedSet(candidatePosition("C0")),
      excluded = OrderedSet(candidatePosition("B1"), candidatePosition("D1"), candidatePosition("I1"), candidatePosition("A1"))
    )

    assert(actualCountData.getDistributionStepForCount(8) === expectedDistributionStep)
  }

  it should "have the correct last distribution step" in {
    val expectedCandidateTransfers = Map(
      candidatePosition("A0") -> CountStepTransfer(324, 324, 8251),
      candidatePosition("A1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("B0") -> CountStepTransfer(0, 0, 0),
      candidatePosition("B1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("C0") -> CountStepTransfer(0, 0, 84923),
      candidatePosition("C1") -> CountStepTransfer(494, 494, 12593),
      candidatePosition("D0") -> CountStepTransfer(0, 0, 0),
      candidatePosition("D1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("E0") -> CountStepTransfer(-3501, -3501, 18),
      candidatePosition("E1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("F0") -> CountStepTransfer(430, 430, 85000),
      candidatePosition("F1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("G0") -> CountStepTransfer(736, 736, 5419),
      candidatePosition("G1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("H0") -> CountStepTransfer(667, 667, 42682),
      candidatePosition("H1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("I0") -> CountStepTransfer(208, 208, 3883),
      candidatePosition("I1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("J0") -> CountStepTransfer(596, 596, 11857),
      candidatePosition("J1") -> CountStepTransfer(0, 0, 0),
      candidatePosition("UG0") -> CountStepTransfer(0, 0, 0),
      candidatePosition("UG1") -> CountStepTransfer(0, 0, 0)
    )

    val expectedExhaustedTransfer = CountStepTransfer(46, 46, 109)
    val expectedGainLossTransfer = CountStepTransfer(0, 0, 32)

    val expectedDistributionSource = DistributionSource(
      candidatePosition("E0"),
      DistributionReason.EXCLUSION,
      Set(1, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27),
      1d
    )

    val expectedDistributionStep = DistributionStep(
      count = 29,
      source = expectedDistributionSource,
      candidateTransfers = expectedCandidateTransfers,
      exhaustedTransfer = expectedExhaustedTransfer,
      gainLossTransfer = expectedGainLossTransfer,
      electedThisCount = OrderedSet(candidatePosition("F0")),
      excludedThisCount = None,
      elected = OrderedSet(candidatePosition("C0"), candidatePosition("F0")),
      excluded = OrderedSet(
        candidatePosition("B1"),
        candidatePosition("D1"),
        candidatePosition("I1"),
        candidatePosition("A1"),
        candidatePosition("G1"),
        candidatePosition("E1"),
        candidatePosition("UG1"),
        candidatePosition("J1"),
        candidatePosition("H1"),
        candidatePosition("UG0"),
        candidatePosition("B0"),
        candidatePosition("F1"),
        candidatePosition("D0"),
        candidatePosition("E0")
      )
    )
    //DistributionStep(29,DistributionSource(CandidatePosition(Group(2016 election,State(ACT),E,RegisteredParty(Sustainable Australia)),0),EXCLUSION,Set(5, 25, 1, 21, 9, 13, 17, 27, 7, 11, 23, 19, 15),1.0),Map(CandidatePosition(Group(2016 election,State(ACT),F,RegisteredParty(Liberal)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Ungrouped(State(ACT)),0) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),H,RegisteredParty(The Greens)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),D,RegisteredParty(Rise Up Australia Party)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),C,RegisteredParty(Australian Labor Party)),0) -> CountStepTransfer(0,0,84923), CandidatePosition(Group(2016 election,State(ACT),B,RegisteredParty(Secular Party of Australia)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),G,RegisteredParty(Animal Justice Party)),0) -> CountStepTransfer(736,736,5419), CandidatePosition(Group(2016 election,State(ACT),I,RegisteredParty(Christian Democratic Party (Fred Nile Group))),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),J,RegisteredParty(Australian Sex Party)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),E,RegisteredParty(Sustainable Australia)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),F,RegisteredParty(Liberal)),0) -> CountStepTransfer(430,430,85000), CandidatePosition(Group(2016 election,State(ACT),B,RegisteredParty(Secular Party of Australia)),0) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),A,RegisteredParty(Liberal Democratic Party)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),J,RegisteredParty(Australian Sex Party)),0) -> CountStepTransfer(596,596,11857), CandidatePosition(Ungrouped(State(ACT)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),C,RegisteredParty(Australian Labor Party)),1) -> CountStepTransfer(494,494,12593), CandidatePosition(Group(2016 election,State(ACT),G,RegisteredParty(Animal Justice Party)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),E,RegisteredParty(Sustainable Australia)),0) -> CountStepTransfer(-3501,-3501,18), CandidatePosition(Group(2016 election,State(ACT),A,RegisteredParty(Liberal Democratic Party)),0) -> CountStepTransfer(324,324,8251), CandidatePosition(Group(2016 election,State(ACT),I,RegisteredParty(Christian Democratic Party (Fred Nile Group))),0) -> CountStepTransfer(208,208,3883), CandidatePosition(Group(2016 election,State(ACT),H,RegisteredParty(The Greens)),0) -> CountStepTransfer(667,667,42682), CandidatePosition(Group(2016 election,State(ACT),D,RegisteredParty(Rise Up Australia Party)),0) -> CountStepTransfer(0,0,0)),CountStepTransfer(46,46,109),CountStepTransfer(0,0,32),Set(CandidatePosition(Group(2016 election,State(ACT),F,RegisteredParty(Liberal)),0)),None,Set(CandidatePosition(Group(2016 election,State(ACT),C,RegisteredParty(Australian Labor Party)),0), CandidatePosition(Group(2016 election,State(ACT),F,RegisteredParty(Liberal)),0)),Set(CandidatePosition(Group(2016 election,State(ACT),B,RegisteredParty(Secular Party of Australia)),1), CandidatePosition(Group(2016 election,State(ACT),D,RegisteredParty(Rise Up Australia Party)),1), CandidatePosition(Group(2016 election,State(ACT),I,RegisteredParty(Christian Democratic Party (Fred Nile Group))),1), CandidatePosition(Group(2016 election,State(ACT),A,RegisteredParty(Liberal Democratic Party)),1), CandidatePosition(Group(2016 election,State(ACT),G,RegisteredParty(Animal Justice Party)),1), CandidatePosition(Group(2016 election,State(ACT),E,RegisteredParty(Sustainable Australia)),1), CandidatePosition(Ungrouped(State(ACT)),1), CandidatePosition(Group(2016 election,State(ACT),J,RegisteredParty(Australian Sex Party)),1), CandidatePosition(Group(2016 election,State(ACT),H,RegisteredParty(The Greens)),1), CandidatePosition(Ungrouped(State(ACT)),0), CandidatePosition(Group(2016 election,State(ACT),B,RegisteredParty(Secular Party of Australia)),0), CandidatePosition(Group(2016 election,State(ACT),F,RegisteredParty(Liberal)),1), CandidatePosition(Group(2016 election,State(ACT),D,RegisteredParty(Rise Up Australia Party)),0), CandidatePosition(Group(2016 election,State(ACT),E,RegisteredParty(Sustainable Australia)),0)))
    //DistributionStep(29,DistributionSource(CandidatePosition(Group(2016 election,State(ACT),E,RegisteredParty(Sustainable Australia)),0),EXCLUSION,Set(5, 25, 1, 21, 9, 13, 17, 27, 7, 11, 23, 19, 15),1.0),Map(CandidatePosition(Group(2016 election,State(ACT),F,RegisteredParty(Liberal)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Ungrouped(State(ACT)),0) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),H,RegisteredParty(The Greens)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),D,RegisteredParty(Rise Up Australia Party)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),C,RegisteredParty(Australian Labor Party)),0) -> CountStepTransfer(0,0,84923), CandidatePosition(Group(2016 election,State(ACT),B,RegisteredParty(Secular Party of Australia)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),G,RegisteredParty(Animal Justice Party)),0) -> CountStepTransfer(736,736,5419), CandidatePosition(Group(2016 election,State(ACT),I,RegisteredParty(Christian Democratic Party (Fred Nile Group))),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),J,RegisteredParty(Australian Sex Party)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),E,RegisteredParty(Sustainable Australia)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),F,RegisteredParty(Liberal)),0) -> CountStepTransfer(430,430,85000), CandidatePosition(Group(2016 election,State(ACT),B,RegisteredParty(Secular Party of Australia)),0) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),A,RegisteredParty(Liberal Democratic Party)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),J,RegisteredParty(Australian Sex Party)),0) -> CountStepTransfer(596,596,11857), CandidatePosition(Ungrouped(State(ACT)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),C,RegisteredParty(Australian Labor Party)),1) -> CountStepTransfer(494,494,12593), CandidatePosition(Group(2016 election,State(ACT),G,RegisteredParty(Animal Justice Party)),1) -> CountStepTransfer(0,0,0), CandidatePosition(Group(2016 election,State(ACT),E,RegisteredParty(Sustainable Australia)),0) -> CountStepTransfer(-3501,3501,18), CandidatePosition(Group(2016 election,State(ACT),A,RegisteredParty(Liberal Democratic Party)),0) -> CountStepTransfer(324,324,8251), CandidatePosition(Group(2016 election,State(ACT),I,RegisteredParty(Christian Democratic Party (Fred Nile Group))),0) -> CountStepTransfer(208,208,3883), CandidatePosition(Group(2016 election,State(ACT),H,RegisteredParty(The Greens)),0) -> CountStepTransfer(667,667,42682), CandidatePosition(Group(2016 election,State(ACT),D,RegisteredParty(Rise Up Australia Party)),0) -> CountStepTransfer(0,0,0)),CountStepTransfer(46,46,109),CountStepTransfer(0,0,32),Set(CandidatePosition(Group(2016 election,State(ACT),F,RegisteredParty(Liberal)),0)),None,Set(CandidatePosition(Group(2016 election,State(ACT),C,RegisteredParty(Australian Labor Party)),0), CandidatePosition(Group(2016 election,State(ACT),F,RegisteredParty(Liberal)),0)),Set(CandidatePosition(Group(2016 election,State(ACT),B,RegisteredParty(Secular Party of Australia)),1), CandidatePosition(Group(2016 election,State(ACT),D,RegisteredParty(Rise Up Australia Party)),1), CandidatePosition(Group(2016 election,State(ACT),I,RegisteredParty(Christian Democratic Party (Fred Nile Group))),1), CandidatePosition(Group(2016 election,State(ACT),A,RegisteredParty(Liberal Democratic Party)),1), CandidatePosition(Group(2016 election,State(ACT),G,RegisteredParty(Animal Justice Party)),1), CandidatePosition(Group(2016 election,State(ACT),E,RegisteredParty(Sustainable Australia)),1), CandidatePosition(Ungrouped(State(ACT)),1), CandidatePosition(Group(2016 election,State(ACT),J,RegisteredParty(Australian Sex Party)),1), CandidatePosition(Group(2016 election,State(ACT),H,RegisteredParty(The Greens)),1), CandidatePosition(Ungrouped(State(ACT)),0), CandidatePosition(Group(2016 election,State(ACT),B,RegisteredParty(Secular Party of Australia)),0), CandidatePosition(Group(2016 election,State(ACT),F,RegisteredParty(Liberal)),1), CandidatePosition(Group(2016 election,State(ACT),D,RegisteredParty(Rise Up Australia Party)),0), CandidatePosition(Group(2016 election,State(ACT),E,RegisteredParty(Sustainable Australia)),0)))
    assert(actualCountData.getDistributionStepForCount(29) === expectedDistributionStep)
  }

  it should "have the correct candidate outcomes" in {
    val expectedOutcomes = Map(
      candidatePosition("A0") -> CountData.CountOutcome.Remainder,
      candidatePosition("A1") -> CountData.CountOutcome.Excluded(4, 8),
      candidatePosition("B0") -> CountData.CountOutcome.Excluded(11, 22),
      candidatePosition("B1") -> CountData.CountOutcome.Excluded(1, 2),
      candidatePosition("C0") -> CountData.CountOutcome.Elected(1, 1),
      candidatePosition("C1") -> CountData.CountOutcome.Remainder,
      candidatePosition("D0") -> CountData.CountOutcome.Excluded(13, 26),
      candidatePosition("D1") -> CountData.CountOutcome.Excluded(2, 4),
      candidatePosition("E0") -> CountData.CountOutcome.Excluded(14, 28),
      candidatePosition("E1") -> CountData.CountOutcome.Excluded(6, 12),
      candidatePosition("F0") -> CountData.CountOutcome.Elected(2, 29),
      candidatePosition("F1") -> CountData.CountOutcome.Excluded(12, 24),
      candidatePosition("G0") -> CountData.CountOutcome.Remainder,
      candidatePosition("G1") -> CountData.CountOutcome.Excluded(5, 10),
      candidatePosition("H0") -> CountData.CountOutcome.Remainder,
      candidatePosition("H1") -> CountData.CountOutcome.Excluded(9, 18),
      candidatePosition("I0") -> CountData.CountOutcome.Remainder,
      candidatePosition("I1") -> CountData.CountOutcome.Excluded(3, 6),
      candidatePosition("J0") -> CountData.CountOutcome.Remainder,
      candidatePosition("J1") -> CountData.CountOutcome.Excluded(8, 16),
      candidatePosition("UG0") -> CountData.CountOutcome.Excluded(10, 20),
      candidatePosition("UG1") -> CountData.CountOutcome.Excluded(7, 14)
    )

    assert(actualCountData.outcomes === expectedOutcomes)
  }
}

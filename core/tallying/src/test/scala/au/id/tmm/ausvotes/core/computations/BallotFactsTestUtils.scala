package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.ausvotes.core.fixtures.DivisionAndPollingPlaceFixture.DivisionsAndPollingPlacesFixture
import au.id.tmm.ausvotes.core.fixtures.GroupAndCandidateFixture.GroupsAndCandidatesFixture
import au.id.tmm.ausvotes.core.fixtures.{DivisionAndPollingPlaceFixture, GroupAndCandidateFixture, MockFetchFederalElectionData}
import au.id.tmm.ausvotes.data_sources.aec.federal.extras.CountRules
import au.id.tmm.ausvotes.data_sources.aec.federal.extras.htv.HowToVoteCardGeneration
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.federal.{DivisionsAndPollingPlaces, FederalBallotJurisdiction}
import au.id.tmm.utilities.geo.australia.State

final class BallotFactsTestUtils private (val state: State,
                                          val groupsAndCandidatesFixture: GroupsAndCandidatesFixture,
                                          val divisionAndPollingPlaceFixture: DivisionsAndPollingPlacesFixture,
                                         ) {

  val groupsAndCandidates: SenateGroupsAndCandidates = groupsAndCandidatesFixture.groupsAndCandidates
  val divisionsAndPollingPlaces: DivisionsAndPollingPlaces = divisionAndPollingPlaceFixture.divisionsAndPollingPlaces

  val senateElection: SenateElection = SenateElection.`2016`
  val election: SenateElectionForState = senateElection.electionForState(state).get
  val countData: SenateCountData = MockFetchFederalElectionData.senateCountDataFor(election, groupsAndCandidates).runUnsafe
  val howToVoteCards: Set[SenateHtv] = HowToVoteCardGeneration.from(SenateElection.`2016`, groupsAndCandidates.groups)

  val normaliser: BallotNormaliser[SenateElectionForState] = BallotNormaliser(CountRules.normalisationRulesFor(election.election), election, groupsAndCandidates.candidates)
  val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)

  def normalise(ballot: SenateBallot): NormalisedSenateBallot = normaliser.normalise(ballot)

  def factsFor(ballot: SenateBallot): StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId] = {
    factsFor(Iterable(ballot)).head
  }

  def factsFor(ballots: Iterable[SenateBallot]): Vector[StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]] = {
    BallotFactsComputation.computeFactsFor(
      election,
      howToVoteCards,
      countData,
      matchingHowToVoteCalculator,
      normaliser,
      ballots,
    )
  }

}

object BallotFactsTestUtils {

  lazy val ACT: BallotFactsTestUtils = new BallotFactsTestUtils(
    state = State.ACT,
    groupsAndCandidatesFixture = GroupAndCandidateFixture.ACT,
    divisionAndPollingPlaceFixture = DivisionAndPollingPlaceFixture.ACT,
  )

  lazy val WA: BallotFactsTestUtils = new BallotFactsTestUtils(
    state = State.WA,
    groupsAndCandidatesFixture = GroupAndCandidateFixture.WA,
    divisionAndPollingPlaceFixture = DivisionAndPollingPlaceFixture.WA,
  )

}

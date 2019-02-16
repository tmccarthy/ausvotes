package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.ausvotes.core.computations.{BallotFactsComputation, BallotWithFacts, ComputationInputData, ComputationTools}
import au.id.tmm.ausvotes.core.fixtures.DivisionAndPollingPlaceFixture.DivisionsAndPollingPlacesFixture
import au.id.tmm.ausvotes.core.fixtures.GroupAndCandidateFixture.GroupsAndCandidatesFixture
import au.id.tmm.ausvotes.core.model.computation.NormalisedBallot
import au.id.tmm.ausvotes.data_sources.aec.federal.impl.htv.HowToVoteCardGeneration
import au.id.tmm.ausvotes.model.federal.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.utilities.geo.australia.State

// TODO move to the core tests
final class BallotFactsTestUtils private(val state: State,
                                         val groupsAndCandidatesFixture: GroupsAndCandidatesFixture,
                                         val divisionAndPollingPlaceFixture: DivisionsAndPollingPlacesFixture,
                                     ) {

  val groupsAndCandidates: SenateGroupsAndCandidates = groupsAndCandidatesFixture.groupsAndCandidates
  val divisionsAndPollingPlaces: DivisionsAndPollingPlaces = divisionAndPollingPlaceFixture.divisionsAndPollingPlaces

  val senateElection: SenateElection = SenateElection.`2016`
  val election: SenateElectionForState = senateElection.electionForState(state).get
  val countData: SenateCountData = MockFetchFederalElectionData.senateCountDataFor(election, groupsAndCandidates).runUnsafe
  val howToVoteCards: Set[SenateHtv] = HowToVoteCardGeneration.from(SenateElection.`2016`, groupsAndCandidates.groups)
  val computationInputData = ComputationInputData(
    ComputationInputData.ElectionLevelData(divisionsAndPollingPlaces, groupsAndCandidates, howToVoteCards),
    ComputationInputData.StateLevelData(countData)
  )

  val normaliser = BallotNormaliser(election, groupsAndCandidates.candidates)
  val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)
  val computationTools = ComputationTools(
    ComputationTools.ElectionLevelTools(matchingHowToVoteCalculator),
    ComputationTools.StateLevelTools(normaliser)
  )

  def normalise(ballot: SenateBallot): NormalisedBallot = normaliser.normalise(ballot)

  def factsFor(ballot: SenateBallot): BallotWithFacts = {
    factsFor(Iterable(ballot)).head
  }

  def factsFor(ballots: Iterable[SenateBallot]): Vector[BallotWithFacts] = {
    BallotFactsComputation.computeFactsFor(
      election,
      computationInputData,
      computationTools,
      ballots
    ).toVector
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

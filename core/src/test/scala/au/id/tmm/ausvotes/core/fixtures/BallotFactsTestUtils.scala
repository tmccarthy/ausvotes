package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.ausvotes.core.computations.{BallotFactsComputation, BallotWithFacts, ComputationInputData, ComputationTools}
import au.id.tmm.ausvotes.core.fixtures.DivisionAndPollingPlaceFixture.DivisionsAndPollingPlacesFixture
import au.id.tmm.ausvotes.core.fixtures.GroupAndCandidateFixture.GroupsAndCandidatesFixture
import au.id.tmm.ausvotes.core.model._
import au.id.tmm.ausvotes.core.model.computation.NormalisedBallot
import au.id.tmm.ausvotes.core.model.parsing.Ballot
import au.id.tmm.ausvotes.core.parsing.HowToVoteCardGeneration
import au.id.tmm.utilities.geo.australia.State

final class BallotFactsTestUtils private(val state: State,
                                         val groupsAndCandidatesFixture: GroupsAndCandidatesFixture,
                                         val divisionAndPollingPlaceFixture: DivisionsAndPollingPlacesFixture,
                                     ) {

  val groupsAndCandidates: GroupsAndCandidates = groupsAndCandidatesFixture.groupsAndCandidates
  val divisionsAndPollingPlaces: DivisionsAndPollingPlaces = divisionAndPollingPlaceFixture.divisionsAndPollingPlaces

  val election: SenateElection.`2016`.type = SenateElection.`2016`
  val countData: CountData = MockParsedDataStore.countDataFor(election, groupsAndCandidates, state)
  val howToVoteCards: Set[HowToVoteCard] = HowToVoteCardGeneration.from(SenateElection.`2016`, groupsAndCandidates.groups)
  val computationInputData = ComputationInputData(
    ComputationInputData.ElectionLevelData(divisionsAndPollingPlaces, groupsAndCandidates, howToVoteCards),
    ComputationInputData.StateLevelData(countData)
  )

  val firstPreferenceCalculator = FirstPreferenceCalculator(election, state, groupsAndCandidates.candidates)
  val normaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)
  val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)
  val computationTools = ComputationTools(
    ComputationTools.ElectionLevelTools(matchingHowToVoteCalculator),
    ComputationTools.StateLevelTools(normaliser, firstPreferenceCalculator)
  )

  def normalise(ballot: Ballot): NormalisedBallot = normaliser.normalise(ballot)

  def factsFor(ballot: Ballot): BallotWithFacts = {
    factsFor(Iterable(ballot)).head
  }

  def factsFor(ballots: Iterable[Ballot]): Vector[BallotWithFacts] = {
    BallotFactsComputation.computeFactsFor(
      election,
      state,
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
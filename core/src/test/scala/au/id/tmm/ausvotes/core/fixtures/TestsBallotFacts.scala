package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.ausvotes.core.computations.{BallotFactsComputation, BallotWithFacts, ComputationInputData, ComputationTools}
import au.id.tmm.ausvotes.core.model._
import au.id.tmm.ausvotes.core.model.computation.NormalisedBallot
import au.id.tmm.ausvotes.core.model.parsing.Ballot
import au.id.tmm.ausvotes.core.parsing.HowToVoteCardGeneration
import au.id.tmm.utilities.geo.australia.State

trait TestsBallotFacts {

  protected val election: SenateElection.`2016`.type = SenateElection.`2016`
  protected val state = State.ACT
  protected val groupsAndCandidates: GroupsAndCandidates = GroupAndCandidateFixture.ACT.groupsAndCandidates
  protected val divisionsAndPollingPlaces: DivisionsAndPollingPlaces = DivisionAndPollingPlaceFixture.ACT.divisionsAndPollingPlaces
  protected val countData: CountData = MockParsedDataStore.countDataFor(election, groupsAndCandidates, state)
  protected val howToVoteCards: Set[HowToVoteCard] = HowToVoteCardGeneration.from(SenateElection.`2016`, groupsAndCandidates.groups)
  protected val computationInputData = ComputationInputData(
    ComputationInputData.ElectionLevelData(divisionsAndPollingPlaces, groupsAndCandidates, howToVoteCards),
    ComputationInputData.StateLevelData(countData)
  )

  protected val firstPreferenceCalculator = FirstPreferenceCalculator(election, state, groupsAndCandidates.candidates)
  protected val normaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)
  protected val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)
  protected val computationTools = ComputationTools(
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

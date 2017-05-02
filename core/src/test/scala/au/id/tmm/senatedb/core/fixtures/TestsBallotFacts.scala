package au.id.tmm.senatedb.core.fixtures

import au.id.tmm.senatedb.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.core.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.senatedb.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.senatedb.core.computations.{BallotFactsComputation, BallotWithFacts, ComputationInputData, ComputationTools}
import au.id.tmm.senatedb.core.model._
import au.id.tmm.senatedb.core.model.parsing.Ballot
import au.id.tmm.senatedb.core.parsing.HowToVoteCardGeneration
import au.id.tmm.utilities.geo.australia.State

trait TestsBallotFacts {

  protected val election: SenateElection.`2016`.type = SenateElection.`2016`
  protected val state = State.ACT
  protected val groupsAndCandidates: GroupsAndCandidates = GroupsAndCandidates.ACT.groupsAndCandidates
  protected val divisionsAndPollingPlaces: DivisionsAndPollingPlaces = DivisionsAndPollingPlaces.ACT.divisionsAndPollingPlaces
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

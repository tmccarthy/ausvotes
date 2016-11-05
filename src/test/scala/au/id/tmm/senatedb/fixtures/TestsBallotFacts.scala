package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.senatedb.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.senatedb.computations.{BallotFactsComputation, BallotWithFacts, ComputationInputData, ComputationTools}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.parsing.HowToVoteCardGeneration
import au.id.tmm.utilities.geo.australia.State

trait TestsBallotFacts {

  protected val election = SenateElection.`2016`
  protected val state = State.ACT
  protected val groupsAndCandidates = GroupsAndCandidates.ACT.groupsAndCandidates
  protected val divisionsAndPollingPlaces = DivisionsAndPollingPlaces.ACT.divisionsAndPollingPlaces
  protected val countData = MockParsedDataStore.countDataFor(election, groupsAndCandidates, state)
  protected val computationInputData = ComputationInputData(groupsAndCandidates, divisionsAndPollingPlaces, countData)

  protected val howToVoteCards = HowToVoteCardGeneration.from(SenateElection.`2016`, groupsAndCandidates.groups)
  protected val firstPreferenceCalculator = FirstPreferenceCalculator(election, state, groupsAndCandidates.candidates)
  protected val normaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)
  protected val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)
  protected val computationTools = ComputationTools(normaliser, firstPreferenceCalculator, matchingHowToVoteCalculator)

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

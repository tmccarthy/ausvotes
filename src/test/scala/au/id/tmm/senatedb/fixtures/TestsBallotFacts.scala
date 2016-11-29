package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.senatedb.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.senatedb.computations.{BallotFactsComputation, BallotWithFacts, ComputationInputData, ComputationTools}
import au.id.tmm.senatedb.model.computation.NormalisedBallot
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.model.{CountData, DivisionsAndPollingPlaces, GroupsAndCandidates, HowToVoteCard, SenateElection}
import au.id.tmm.senatedb.parsing.HowToVoteCardGeneration
import au.id.tmm.utilities.geo.australia.State

trait TestsBallotFacts {

  protected val election: SenateElection = SenateElection.`2016`
  protected val state: State = State.ACT
  protected val groupsAndCandidates: GroupsAndCandidates = GroupsAndCandidates.ACT.groupsAndCandidates
  protected val divisionsAndPollingPlaces: DivisionsAndPollingPlaces = DivisionsAndPollingPlaces.ACT.divisionsAndPollingPlaces
  protected val countData: CountData = MockParsedDataStore.countDataFor(election, groupsAndCandidates, state)
  protected val computationInputData: ComputationInputData = ComputationInputData(groupsAndCandidates, divisionsAndPollingPlaces, countData)

  protected val howToVoteCards: Set[HowToVoteCard] = HowToVoteCardGeneration.from(SenateElection.`2016`, groupsAndCandidates.groups)
  protected val firstPreferenceCalculator: FirstPreferenceCalculator = FirstPreferenceCalculator(election, state, groupsAndCandidates.candidates)
  protected val normaliser: BallotNormaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)
  protected val matchingHowToVoteCalculator: MatchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)
  protected val computationTools: ComputationTools = ComputationTools(normaliser, firstPreferenceCalculator, matchingHowToVoteCalculator)

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

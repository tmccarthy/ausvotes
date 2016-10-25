package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.senatedb.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.senatedb.computations.{BallotFactsComputation, BallotWithFacts, ComputationTools}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.parsing.HowToVoteCardGeneration
import au.id.tmm.utilities.geo.australia.State

trait TestsBallotFacts {

  private val election = SenateElection.`2016`
  private val state = State.ACT
  private val groupsAndCandidates = GroupsAndCandidates.ACT.groupsAndCandidates
  private val divisionsAndPollingPlaces = DivisionsAndPollingPlaces.ACT.divisionsAndPollingPlaces
  private val howToVoteCards = HowToVoteCardGeneration.from(SenateElection.`2016`, groupsAndCandidates.groups)

  private val firstPreferenceCalculator = FirstPreferenceCalculator(election, state, groupsAndCandidates.candidates)
  private val normaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)
  private val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)
  private val computationTools = ComputationTools(normaliser, firstPreferenceCalculator, matchingHowToVoteCalculator)

  def factsFor(ballot: Ballot): BallotWithFacts = {

    BallotFactsComputation.computeFactsFor(
      election,
      state,
      groupsAndCandidates,
      divisionsAndPollingPlaces,
      computationTools,
      Iterable(ballot)
    ).head
  }

}

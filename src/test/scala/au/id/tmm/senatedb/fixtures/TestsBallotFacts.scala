package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.senatedb.computations.{BallotFactsComputation, BallotWithFacts, ComputationTools}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.utilities.geo.australia.State

trait TestsBallotFacts {

  val election = SenateElection.`2016`
  val state = State.ACT
  val groupsAndCandidates = GroupsAndCandidates.ACT.groupsAndCandidates
  val divisionsAndPollingPlaces = DivisionsAndPollingPlaces.ACT.divisionsAndPollingPlaces

  val firstPreferenceCalculator = FirstPreferenceCalculator(election, state, groupsAndCandidates.candidates)
  val normaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)
  val computationTools = ComputationTools(normaliser, firstPreferenceCalculator)

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

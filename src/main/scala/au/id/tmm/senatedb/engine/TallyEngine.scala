package au.id.tmm.senatedb.engine

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.senatedb.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.senatedb.computations.{BallotFactsComputation, BallotWithFacts, ComputationTools}
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.parsing.HowToVoteCardGeneration
import au.id.tmm.senatedb.tallies.Tallies.TraversableOps
import au.id.tmm.senatedb.tallies.{Tallier, Tallies}
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.resources.ManagedResourceUtils.ExtractableManagedResourceOps

import scala.concurrent.{ExecutionContext, Future}

object TallyEngine {

  def runFor(parsedDataStore: ParsedDataStore,
             election: SenateElection,
             states: Set[State],
             talliers: Set[Tallier])
            (implicit ec: ExecutionContext): Future[Tallies] = {
    val divisionsAndPollingPlacesFuture = Future(parsedDataStore.divisionsAndPollingPlacesFor(election))
    val groupsAndCandidatesFuture = Future(parsedDataStore.groupsAndCandidatesFor(election))

    for {
      divisionsAndPollingPlaces <- divisionsAndPollingPlacesFuture
      groupsAndCandidates <- groupsAndCandidatesFuture
      allTallies <- allTalliesFrom(parsedDataStore, election, states, divisionsAndPollingPlaces, groupsAndCandidates, talliers)
    } yield allTallies
  }

  private def allTalliesFrom(parsedDataStore: ParsedDataStore,
                             election: SenateElection,
                             states: Set[State],
                             divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                             groupsAndCandidates: GroupsAndCandidates,
                             talliers: Set[Tallier])
                            (implicit ec: ExecutionContext): Future[Tallies] = {
    val ballotFuturesPerState = states
      .map(state => state -> parsedDataStore.ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces, state))(Set.canBuildFrom)

    val talliesFuturesPerState = ballotFuturesPerState
      .map {
        case (state, ballots) => talliesFor(election, state, divisionsAndPollingPlaces, groupsAndCandidates, ballots, talliers)
      }(Set.canBuildFrom)

    val finalTallies = Future.sequence(talliesFuturesPerState)
      .map(tallies => tallies.reduce(_ + _))

    finalTallies
  }

  private def talliesFor(election: SenateElection,
                         state: State,
                         divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                         groupsAndCandidates: GroupsAndCandidates,
                         ballots: CloseableIterator[Ballot],
                         talliers: Set[Tallier])
                        (implicit ec: ExecutionContext): Future[Tallies] = Future {
    val computationTools = buildComputationToolsFor(election, state, groupsAndCandidates)

    resource.managed(ballots)
      .map(ballots => {
        val groupedIterator = ballots.grouped(5000) // TODO constant

        val tallies = groupedIterator
          .map(ballots => {
            BallotFactsComputation.computeFactsFor(
              election,
              state,
              groupsAndCandidates,
              divisionsAndPollingPlaces,
              computationTools,
              ballots)
          })
          .map(ballotsWithFacts => talliesFrom(ballotsWithFacts, talliers))
          .foldLeft(Tallies())((left, right) => left + right)

        tallies
      })
      .toTry
      .get
  }

  private def buildComputationToolsFor(election: SenateElection,
                               state: State,
                               groupsAndCandidates: GroupsAndCandidates): ComputationTools = {
    // TODO move this somewhere where it isn't being calculated every time per state
    val howToVoteCards = HowToVoteCardGeneration.from(election, groupsAndCandidates.groups)

    val normaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)
    val firstPreferenceCalculator = FirstPreferenceCalculator(election, state, groupsAndCandidates.candidates)
    val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)

    ComputationTools(normaliser, firstPreferenceCalculator, matchingHowToVoteCalculator)
  }

  private def talliesFrom(ballotsWithFactsIterable: Iterable[BallotWithFacts], talliers: Set[Tallier]): Tallies = {
    val ballotsWithFacts = ballotsWithFactsIterable.toVector

    talliers
      .map(tallier => tallier -> tallier.tally(ballotsWithFacts))
      .toTallies
  }
}

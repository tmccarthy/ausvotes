package au.id.tmm.senatedb.core.engine

import au.id.tmm.senatedb.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.core.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.senatedb.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.senatedb.core.computations.{BallotFactsComputation, BallotWithFacts, ComputationInputData, ComputationTools}
import au.id.tmm.senatedb.core.model.parsing.Ballot
import au.id.tmm.senatedb.core.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, HowToVoteCard, SenateElection}
import au.id.tmm.senatedb.core.parsing.HowToVoteCardGeneration
import au.id.tmm.senatedb.core.tallies.Tallies.TraversableOps
import au.id.tmm.senatedb.core.tallies.{Tallier, Tallies}
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.resources.ManagedResourceUtils.ExtractableManagedResourceOps

import scala.concurrent.{ExecutionContext, Future}

trait TallyEngine {
  def runFor(parsedDataStore: ParsedDataStore,
             election: SenateElection,
             states: Set[State],
             talliers: Set[Tallier])
            (implicit ec: ExecutionContext): Future[Tallies]

  def runFor(parsedDataStore: ParsedDataStore,
             election: SenateElection,
             states: Set[State],
             divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
             groupsAndCandidates: GroupsAndCandidates,
             talliers: Set[Tallier])
            (implicit ec: ExecutionContext): Future[Tallies]
}

object TallyEngine extends TallyEngine {
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
      allTallies <- runFor(parsedDataStore, election, states, divisionsAndPollingPlaces, groupsAndCandidates, talliers)
    } yield allTallies
  }

  def runFor(parsedDataStore: ParsedDataStore,
             election: SenateElection,
             states: Set[State],
             divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
             groupsAndCandidates: GroupsAndCandidates,
             talliers: Set[Tallier])
            (implicit ec: ExecutionContext): Future[Tallies] = {
    val howToVoteCards = HowToVoteCardGeneration.from(election, groupsAndCandidates.groups)

    val tallyFuturesPerState = states
      .map(state => talliesForState(parsedDataStore,
        election,
        state,
        divisionsAndPollingPlaces,
        groupsAndCandidates,
        howToVoteCards,
        talliers
      ))

    val finalTallies = Future.sequence(tallyFuturesPerState)
      .map(tallies => tallies.reduce(_ + _))

    finalTallies
  }

  private def talliesForState(parsedDataStore: ParsedDataStore,
                              election: SenateElection,
                              state: State,
                              divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                              groupsAndCandidates: GroupsAndCandidates,
                              howToVoteCards: Set[HowToVoteCard],
                              talliers: Set[Tallier])
                             (implicit ec: ExecutionContext): Future[Tallies] = {

    Future {
      parsedDataStore.countDataFor(election, groupsAndCandidates, state)
    } map { countData =>
      val computationTools = buildComputationToolsFor(election, state, groupsAndCandidates, howToVoteCards)
      val computationInputData = ComputationInputData(
        ComputationInputData.ElectionLevelData(divisionsAndPollingPlaces, groupsAndCandidates, howToVoteCards),
        ComputationInputData.StateLevelData(countData)
      )

      resource.managed(parsedDataStore.ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces, state))
        .map { ballots =>
          talliesFromBallots(election, state, talliers, computationTools, computationInputData, ballots)
        }
        .toTry
        .get
    }
  }

  private def talliesFromBallots(election: SenateElection,
                                 state: State,
                                 talliers: Set[Tallier],
                                 computationTools: ComputationTools,
                                 computationInputData: ComputationInputData,
                                 ballots: CloseableIterator[Ballot]): Tallies = {
    val groupedIterator = ballots.grouped(5000) // TODO constant

    val tallies = groupedIterator
      .map(ballots => {
        BallotFactsComputation.computeFactsFor(
          election,
          state,
          computationInputData,
          computationTools,
          ballots)
      })
      .map(ballotsWithFacts => talliesFrom(ballotsWithFacts, talliers))
      .foldLeft(Tallies())((left, right) => left + right)

    tallies
  }

  private def buildComputationToolsFor(election: SenateElection,
                                       state: State,
                                       groupsAndCandidates: GroupsAndCandidates,
                                       howToVoteCards: Set[HowToVoteCard]): ComputationTools = {

    val normaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)
    val firstPreferenceCalculator = FirstPreferenceCalculator(election, state, groupsAndCandidates.candidates)
    val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)

    ComputationTools(
      ComputationTools.ElectionLevelTools(matchingHowToVoteCalculator),
      ComputationTools.StateLevelTools(normaliser, firstPreferenceCalculator)
    )
  }

  private def talliesFrom(ballotsWithFactsIterable: Iterable[BallotWithFacts], talliers: Set[Tallier]): Tallies = {
    val ballotsWithFacts = ballotsWithFactsIterable.toVector

    talliers
      .map(tallier => tallier -> tallier.tally(ballotsWithFacts))
      .toTallies
  }
}
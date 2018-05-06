package au.id.tmm.ausvotes.core.engine

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.ausvotes.core.computations.{BallotFactsComputation, BallotWithFacts, ComputationInputData, ComputationTools}
import au.id.tmm.ausvotes.core.logging.LoggedEvent.FutureOps
import au.id.tmm.ausvotes.core.logging.Logger
import au.id.tmm.ausvotes.core.model._
import au.id.tmm.ausvotes.core.model.parsing.Ballot
import au.id.tmm.ausvotes.core.parsing.HowToVoteCardGeneration
import au.id.tmm.ausvotes.core.tallies.TallyBundle.TraversableOps
import au.id.tmm.ausvotes.core.tallies.{Tallier, TallyBundle}
import au.id.tmm.utilities.collection.{CloseableIterator, DupelessSeq}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.resources.ManagedResourceUtils.ExtractableManagedResourceOps

import scala.concurrent.{ExecutionContext, Future}

trait TallyEngine {
  def runFor(parsedDataStore: ParsedDataStore,
             election: SenateElection,
             states: Set[State],
             talliers: Set[Tallier])
            (implicit ec: ExecutionContext): Future[TallyBundle]

  def runFor(parsedDataStore: ParsedDataStore,
             election: SenateElection,
             states: Set[State],
             divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
             groupsAndCandidates: GroupsAndCandidates,
             talliers: Set[Tallier])
            (implicit ec: ExecutionContext): Future[TallyBundle]
}

object TallyEngine extends TallyEngine {
  private implicit val logger: Logger = Logger()

  def runFor(parsedDataStore: ParsedDataStore,
             election: SenateElection,
             states: Set[State],
             talliers: Set[Tallier])
            (implicit ec: ExecutionContext): Future[TallyBundle] = {
    val divisionsAndPollingPlacesFuture = Future(parsedDataStore.divisionsAndPollingPlacesFor(election))
    val groupsAndCandidatesFuture = Future(parsedDataStore.groupsAndCandidatesFor(election))

    for {
      divisionsAndPollingPlaces <- divisionsAndPollingPlacesFuture
      groupsAndCandidates <- groupsAndCandidatesFuture
      allTallies <- runFor(parsedDataStore, election, states, divisionsAndPollingPlaces, groupsAndCandidates, talliers)
    } yield allTallies
  }

  private def orderBySize(states: Set[State]): DupelessSeq[State] = {
    DupelessSeq(
      State.NSW,
      State.VIC,
      State.QLD,
      State.WA,
      State.SA,
      State.TAS,
      State.ACT,
      State.NT,
    ).filter(states.contains)
  }

  def runFor(parsedDataStore: ParsedDataStore,
             election: SenateElection,
             states: Set[State],
             divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
             groupsAndCandidates: GroupsAndCandidates,
             talliers: Set[Tallier])
            (implicit ec: ExecutionContext): Future[TallyBundle] = {
    val howToVoteCards = HowToVoteCardGeneration.from(election, groupsAndCandidates.groups)

    val tallyFuturesPerState = orderBySize(states)
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
      .logEvent("TALLY_ENGINE_EXECUTION",
        "election" -> election,
        "states" -> states,
        "talliers" -> talliers.map(_.name),
      )
  }

  private def talliesForState(parsedDataStore: ParsedDataStore,
                              election: SenateElection,
                              state: State,
                              divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                              groupsAndCandidates: GroupsAndCandidates,
                              howToVoteCards: Set[HowToVoteCard],
                              talliers: Set[Tallier])
                             (implicit ec: ExecutionContext): Future[TallyBundle] = {

    Future {
      parsedDataStore.countDataFor(election, groupsAndCandidates, state)
    }.map { countData =>
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
      .logEvent("COMPUTE_TALLIES_FOR_STATE",
        "election" -> election,
        "state" -> state.abbreviation,
        "talliers" -> talliers.map(_.name),
      )
  }

  private def talliesFromBallots(election: SenateElection,
                                 state: State,
                                 talliers: Set[Tallier],
                                 computationTools: ComputationTools,
                                 computationInputData: ComputationInputData,
                                 ballots: CloseableIterator[Ballot]): TallyBundle = {
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
      .foldLeft(TallyBundle())((left, right) => left + right)

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

  private def talliesFrom(ballotsWithFactsIterable: Iterable[BallotWithFacts], talliers: Set[Tallier]): TallyBundle = {
    val ballotsWithFacts = ballotsWithFactsIterable.toVector

    talliers
      .map(tallier => tallier -> tallier.tally(ballotsWithFacts))
      .toTallyBundle
  }
}
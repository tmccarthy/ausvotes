package au.id.tmm.ausvotes.core.engine

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.ausvotes.core.computations.{BallotFactsComputation, BallotWithFacts, ComputationInputData, ComputationTools}
import au.id.tmm.ausvotes.core.parsing.HowToVoteCardGeneration
import au.id.tmm.ausvotes.core.tallies.TallyBundle.TraversableOps
import au.id.tmm.ausvotes.core.tallies.{Tallier, TallyBundle}
import au.id.tmm.ausvotes.model.federal.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.utilities.collection.{CloseableIterator, DupelessSeq}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.logging.LoggedEvent.FutureOps
import au.id.tmm.utilities.logging.Logger
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
             groupsAndCandidates: SenateGroupsAndCandidates,
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
    val divisionsAndPollingPlacesFuture = Future(parsedDataStore.divisionsAndPollingPlacesFor(election.federalElection))
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
             groupsAndCandidates: SenateGroupsAndCandidates,
             talliers: Set[Tallier])
            (implicit ec: ExecutionContext): Future[TallyBundle] = {
    val howToVoteCards = HowToVoteCardGeneration.from(election, groupsAndCandidates.groups)

    val tallyFuturesPerState = orderBySize(states)
      .flatMap(election.electionForState)
      .map(election => talliesForState(
        parsedDataStore,
        election,
        divisionsAndPollingPlaces,
        groupsAndCandidates,
        howToVoteCards,
        talliers,
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
                              election: SenateElectionForState,
                              divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                              groupsAndCandidates: SenateGroupsAndCandidates,
                              howToVoteCards: Set[SenateHtv],
                              talliers: Set[Tallier])
                             (implicit ec: ExecutionContext): Future[TallyBundle] = {

    Future {
      parsedDataStore.countDataFor(election, groupsAndCandidates)
    }.map { countData =>
      val computationTools = buildComputationToolsFor(election, groupsAndCandidates, howToVoteCards)
      val computationInputData = ComputationInputData(
        ComputationInputData.ElectionLevelData(divisionsAndPollingPlaces, groupsAndCandidates, howToVoteCards),
        ComputationInputData.StateLevelData(countData)
      )

      resource.managed(parsedDataStore.ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces))
        .map { ballots =>
          talliesFromBallots(election, talliers, computationTools, computationInputData, ballots)
        }
        .toTry
        .get
    }
      .logEvent("COMPUTE_TALLIES_FOR_STATE",
        "election" -> election.election,
        "state" -> election.state.abbreviation,
        "talliers" -> talliers.map(_.name),
      )
  }

  private def talliesFromBallots(election: SenateElectionForState,
                                 talliers: Set[Tallier],
                                 computationTools: ComputationTools,
                                 computationInputData: ComputationInputData,
                                 ballots: CloseableIterator[SenateBallot]): TallyBundle = {
    val groupedIterator = ballots.grouped(5000) // TODO constant

    val tallies = groupedIterator
      .map(ballots => {
        BallotFactsComputation.computeFactsFor(
          election,
          computationInputData,
          computationTools,
          ballots)
      })
      .map(ballotsWithFacts => talliesFrom(ballotsWithFacts, talliers))
      .foldLeft(TallyBundle())((left, right) => left + right)

    tallies
  }

  private def buildComputationToolsFor(election: SenateElectionForState,
                                       groupsAndCandidates: SenateGroupsAndCandidates,
                                       howToVoteCards: Set[SenateHtv]): ComputationTools = {

    val normaliser = BallotNormaliser(election, groupsAndCandidates.candidates)
    val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)

    ComputationTools(
      ComputationTools.ElectionLevelTools(matchingHowToVoteCalculator),
      ComputationTools.StateLevelTools(normaliser)
    )
  }

  private def talliesFrom(ballotsWithFactsIterable: Iterable[BallotWithFacts], talliers: Set[Tallier]): TallyBundle = {
    val ballotsWithFacts = ballotsWithFactsIterable.toVector

    talliers
      .map(tallier => tallier -> tallier.tally(ballotsWithFacts))
      .toTallyBundle
  }
}

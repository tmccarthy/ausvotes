package au.id.tmm.senatedb.engine

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.senatedb.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.senatedb.computations.{BallotFactsComputation, BallotWithFacts, ComputationTools}
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.parsing.HowToVoteCardGeneration
import au.id.tmm.senatedb.reporting.ReportHolder
import au.id.tmm.senatedb.reporting.reports._
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.resources.ManagedResourceUtils.ExtractableManagedResourceOps

import scala.concurrent.{ExecutionContext, Future}

object ReportEngine {

  def runFor(parsedDataStore: ParsedDataStore,
             election: SenateElection,
             states: Set[State])(implicit ec: ExecutionContext): Future[ReportHolder] = {
    val divisionsAndPollingPlacesFuture = Future(parsedDataStore.divisionsAndPollingPlacesFor(election))
    val groupsAndCandidatesFuture = Future(parsedDataStore.groupsAndCandidatesFor(election))

    for {
      divisionsAndPollingPlaces <- divisionsAndPollingPlacesFuture
      groupsAndCandidates <- groupsAndCandidatesFuture
      allReports <- allReportsFrom(parsedDataStore, election, states, divisionsAndPollingPlaces, groupsAndCandidates)
    } yield allReports
  }

  private def allReportsFrom(parsedDataStore: ParsedDataStore,
                             election: SenateElection,
                             states: Set[State],
                             divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                             groupsAndCandidates: GroupsAndCandidates)
                            (implicit ec: ExecutionContext): Future[ReportHolder] = {
    val ballotFuturesPerState = states
      .map(state => state -> parsedDataStore.ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces, state))(Set.canBuildFrom)

    val reportFuturesPerState = ballotFuturesPerState
      .map {
        case (state, ballots) => reportsFor(election, state, divisionsAndPollingPlaces, groupsAndCandidates, ballots)
      }(Set.canBuildFrom)

    val finalReports = Future.sequence(reportFuturesPerState)
      .map(allReports => allReports.reduce(_ accumulate _))

    finalReports
  }

  private def reportsFor(election: SenateElection,
                         state: State,
                         divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                         groupsAndCandidates: GroupsAndCandidates,
                         ballots: CloseableIterator[Ballot])
                        (implicit ec: ExecutionContext): Future[ReportHolder] = Future {
    val computationTools = buildComputationToolsFor(election, state, groupsAndCandidates)

    resource.managed(ballots)
      .map(ballots => {
        val groupedIterator = ballots.grouped(5000) // TODO constant

        val reports = groupedIterator
          .map(ballots => {
            BallotFactsComputation.computeFactsFor(
              election,
              state,
              groupsAndCandidates,
              divisionsAndPollingPlaces,
              computationTools,
              ballots)
          })
          .map(reportsFor)
          .foldLeft(ReportHolder.empty)((left, right) => left accumulate right)

        reports
      })
      .toTry
      .get
  }

  def buildComputationToolsFor(election: SenateElection,
                               state: State,
                               groupsAndCandidates: GroupsAndCandidates): ComputationTools = {
    // TODO move this somewhere where it isn't being calculated every time per state
    val howToVoteCards = HowToVoteCardGeneration.from(election, groupsAndCandidates.groups)

    val normaliser = BallotNormaliser(election, state, groupsAndCandidates.candidates)
    val firstPreferenceCalculator = FirstPreferenceCalculator(election, state, groupsAndCandidates.candidates)
    val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)

    ComputationTools(normaliser, firstPreferenceCalculator, matchingHowToVoteCalculator)
  }

  private def reportsFor(ballotsFacts: Iterable[BallotWithFacts]): ReportHolder = {
    val ballotsWithFacts = ballotsFacts.toVector

    ReportHolder(
      TotalFormalBallotsReportGenerator.generateFor(ballotsWithFacts),
      OneAtlVoteReportGenerator.generateFor(ballotsWithFacts),
      DonkeyVoteReportGenerator.generateFor(ballotsWithFacts),
      BallotsUsingTicksReportGenerator.generateFor(ballotsWithFacts),
      BallotsUsingCrossesReportGenerator.generateFor(ballotsWithFacts),
      UsedHtvReportGenerator.generateFor(ballotsWithFacts)
    )
  }
}

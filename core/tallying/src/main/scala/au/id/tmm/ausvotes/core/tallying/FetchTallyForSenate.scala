package au.id.tmm.ausvotes.core.tallying

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.ausvotes.core.computations.{BallotFactsComputation, SenateBallotWithFacts}
import au.id.tmm.ausvotes.data_sources.aec.federal.extras.{CountRules, FetchSenateHtv}
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.{FetchDivisionsAndFederalPollingPlaces, FetchSenateBallots, FetchSenateCountData, FetchSenateGroupsAndCandidates}
import au.id.tmm.ausvotes.data_sources.common.JsonCache
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.federal.{DivisionsAndPollingPlaces, FederalBallotJurisdiction}
import au.id.tmm.ausvotes.model.instances.StateInstances
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.{Ops, _}
import au.id.tmm.ausvotes.shared.io.typeclasses.CatsInterop._
import au.id.tmm.ausvotes.shared.io.typeclasses.{Concurrent, SyncEffects}
import cats.instances.list._
import cats.syntax.traverse._

object FetchTallyForSenate {

  def apply[F[+_, +_]](implicit
                       concurrent: Concurrent[F],
                       jsonCache: JsonCache[F],
                       log: Log[F],
                       fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[F],
                       fetchDivisions: FetchDivisionsAndFederalPollingPlaces[F],
                       fetchCountData: FetchSenateCountData[F],
                       fetchSenateBallots: FetchSenateBallots[F],
                       fetchHtv: FetchSenateHtv[F],
                       fetchTally: FetchTally[F],
                      ): FetchTallyForElection[F, SenateElection, SenateBallotWithFacts] =
    FetchTallyForElection(ballotStreamFor(_))

  private def ballotStreamFor[F[+_, +_]](
                                          election: SenateElection,
                                        )(implicit
                                          concurrent: Concurrent[F],
                                          jsonCache: JsonCache[F],
                                          log: Log[F],
                                          fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[F],
                                          fetchDivisions: FetchDivisionsAndFederalPollingPlaces[F],
                                          fetchCountData: FetchSenateCountData[F],
                                          fetchSenateBallots: FetchSenateBallots[F],
                                          fetchHtv: FetchSenateHtv[F],
                                          fetchTally: FetchTally[F],
                                        ): F[Exception, fs2.Stream[F[Throwable, +?], SenateBallotWithFacts]] =
    for {
      divisionsPollingPlacesGroupsAndCandidates <- Concurrent.par2(
        FetchDivisionsAndFederalPollingPlaces.divisionsAndFederalPollingPlacesFor(election.federalElection).leftMap(FetchTally.Error(_)),
        FetchSenateGroupsAndCandidates.senateGroupsAndCandidatesFor(election).leftMap(FetchTally.Error(_)),
      )

      divisionsAndPollingPlaces = divisionsPollingPlacesGroupsAndCandidates._1
      groupsAndCandidates = divisionsPollingPlacesGroupsAndCandidates._2

      htvCards <- FetchSenateHtv.fetchFor(election, groupsAndCandidates.groups)
        .leftMap(FetchTally.Error)

      stateElectionsInSizeOrder = election.allStateElections.toList.sortBy(_.state)(StateInstances.orderStatesByPopulation)

      htvCards <- FetchSenateHtv.fetchFor(election, groupsAndCandidates.groups)

      ballotWithFactsStreamsInSizeOrder <- stateElectionsInSizeOrder
        .traverse { electionForState =>
          ballotsWithFactsFor(electionForState, groupsAndCandidates, divisionsAndPollingPlaces, htvCards.getOrElse(electionForState, Set.empty))
        }

      ballotStream = ballotWithFactsStreamsInSizeOrder
        .reduceOption(_ ++ _)
        .getOrElse[fs2.Stream[F[Throwable, +?], SenateBallotWithFacts]](fs2.Stream.empty)
    } yield ballotStream

  private def ballotsWithFactsFor[F[+_, +_]](
                                              electionForState: SenateElectionForState,
                                              groupsAndCandidates: SenateGroupsAndCandidates,
                                              divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                              htvCards: Set[SenateHtv],
                                            )(implicit
                                              concurrent: Concurrent[F],
                                              log: Log[F],
                                              fetchCountData: FetchSenateCountData[F],
                                              fetchSenateBallots: FetchSenateBallots[F],
                                            ): F[Exception, fs2.Stream[F[Throwable, +?], SenateBallotWithFacts]] =
    for {
      senateBallotsStream <- FetchSenateBallots.senateBallotsFor(electionForState, groupsAndCandidates, divisionsAndPollingPlaces)

      countData <- FetchSenateCountData.senateCountDataFor(electionForState, groupsAndCandidates)

      ballotNormaliser = BallotNormaliser(CountRules.normalisationRulesFor(electionForState.election), electionForState, groupsAndCandidates.candidates)
      matchingHowToVoteCalculator = MatchingHowToVoteCalculator(htvCards)

      ballotsWithFactsStream = senateBallotsStream.chunkN(5000)
        .parEvalMapUnordered(Runtime.getRuntime.availableProcessors() * 2) { chunk =>
          for {
            stream <- SyncEffects.sync(
              fs2.Stream.emits(
                BallotFactsComputation.computeFactsFor[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId](
                  electionForState,
                  htvCards,
                  countData,
                  matchingHowToVoteCalculator,
                  ballotNormaliser,
                  chunk.toVector,
                )
              )
            )

            _ <- Log.logInfo("PROCESS_CHUNK", "election" -> electionForState, "num_ballots" -> chunk.size)

          } yield stream
        }
        .flatten

    } yield ballotsWithFactsStream

}

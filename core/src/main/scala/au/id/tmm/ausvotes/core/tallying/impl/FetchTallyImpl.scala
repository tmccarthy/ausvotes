package au.id.tmm.ausvotes.core.tallying.impl

import java.util.concurrent.TimeUnit

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.ausvotes.core.computations.{BallotFactsComputation, BallotWithFacts, ComputationInputData, ComputationTools}
import au.id.tmm.ausvotes.core.tallies._
import au.id.tmm.ausvotes.core.tallying.FetchTally
import au.id.tmm.ausvotes.core.tallying.impl.FetchTallyImpl.TallyRequest
import au.id.tmm.ausvotes.data_sources.aec.federal.FetchSenateHtv
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed._
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop._
import au.id.tmm.ausvotes.data_sources.common.JsonCache
import au.id.tmm.ausvotes.model.federal.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.instances.StateInstances
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError._
import cats.instances.list._
import cats.syntax.traverse._
import io.circe.{Decoder, Encoder}
import scalaz.zio._
import scalaz.zio.duration.Duration

import scala.collection.mutable

final class FetchTallyImpl private(
                                    mutex: Semaphore,
                                  )(
                                    implicit
                                    fetchDivisionsAndPollingPlaces: FetchDivisionsAndFederalPollingPlaces[IO],
                                    fetchSenateGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO],
                                    fetchSenateCountData: FetchSenateCountData[IO],
                                    fetchSenateBallots: FetchSenateBallots[IO],
                                    fetchSenateHtv: FetchSenateHtv[IO],
                                    jsonCache: JsonCache[IO],
                                  ) extends FetchTally[IO] {

  private val promisesPerRequest: mutable.Map[TallyRequest, Promise[FetchTally.Error, Tally]] = mutable.Map.empty

  override def fetchTally0(election: SenateElection, tallier: Tallier0): IO[FetchTally.Error, Tally0] = {
    val tallyRequest = TallyRequest(election, tallier)

    jsonCache.getOrCompute(tallyRequest) {
      getTallyFor(tallyRequest).map(_.asInstanceOf[Tally0])
    }.leftMap(FetchTally.Error)
  }

  override def fetchTally1[T_GROUP_1 : Encoder : Decoder](election: SenateElection, tallier: Tallier1[T_GROUP_1]): IO[FetchTally.Error, Tally1[T_GROUP_1]] = {
    val tallyRequest = TallyRequest(election, tallier)

    jsonCache.getOrCompute(tallyRequest) {
      getTallyFor(tallyRequest).map(_.asInstanceOf[Tally1[T_GROUP_1]])
    }.leftMap(FetchTally.Error)
  }

  override def fetchTally2[T_GROUP_1 : Encoder : Decoder, T_GROUP_2 : Encoder : Decoder](election: SenateElection, tallier: Tallier2[T_GROUP_1, T_GROUP_2]): IO[FetchTally.Error, Tally2[T_GROUP_1, T_GROUP_2]] = {
    val tallyRequest = TallyRequest(election, tallier)

    jsonCache.getOrCompute(tallyRequest) {
      getTallyFor(tallyRequest).map(_.asInstanceOf[Tally2[T_GROUP_1, T_GROUP_2]])
    }.leftMap(FetchTally.Error)
  }

  override def fetchTally3[T_GROUP_1 : Encoder : Decoder, T_GROUP_2 : Encoder : Decoder, T_GROUP_3 : Encoder : Decoder](election: SenateElection, tallier: Tallier3[T_GROUP_1, T_GROUP_2, T_GROUP_3]): IO[FetchTally.Error, Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]] = {
    val tallyRequest = TallyRequest(election, tallier)

    jsonCache.getOrCompute(tallyRequest) {
      getTallyFor(tallyRequest).map(_.asInstanceOf[Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]])
    }.leftMap(FetchTally.Error)
  }

  private def getTallyFor(tallyRequest: TallyRequest): IO[FetchTally.Error, Tally] =
    mutex.withPermit {
      for {
        promise <- promisesPerRequest.get(tallyRequest) match {
          case Some(existingPromise) => IO.point(existingPromise)

          case None =>
            for {
              promise <- Promise.make[FetchTally.Error, Tally]
              _ = promisesPerRequest.update(tallyRequest, promise)
              _ <- fulfilPromisesFromTallyEngine.fork
            } yield promise
        }
      } yield promise
    }.flatMap { promise =>
      promise.get
    }

  private val fulfilPromisesFromTallyEngine: IO[Nothing, Unit] =
    for {
      _ <- IO.sleep(Duration(5, TimeUnit.SECONDS))

      requestsAndPromises <- mutex.withPermit {
        IO.sync {
          val requestsAndPromises = promisesPerRequest.toMap

          promisesPerRequest.clear()

          requestsAndPromises
        }
      }

      _ <- IO.traverse(requestsAndPromises.groupBy { case (TallyRequest(election, _), _) => election }) {
        case (election, promisesPerTallyRequestsForElection) =>
          val promisesPerTallier: Map[Tallier, Promise[FetchTally.Error, Tally]] = promisesPerTallyRequestsForElection.map {
            case (TallyRequest(_, tallier), promise) => tallier -> promise
          }

          val talliers = promisesPerTallier.keySet

          for {
            tallyBundleOrError <- runForElectionAndTalliers(election, talliers).attempt
              .timedLog(
                eventId = "TALLY_ENGINE_EXECUTION",
                "election" -> election,
                "states" -> election.allStateElections.map(_.state),
                "talliers" -> talliers.map(_.name),
              )

            _ <- tallyBundleOrError match {
              case Right(tallyBundle) => promisesPerTallier.toList.traverse { case (tallier, promise) =>
                promise.complete(tallyBundle.tallyProducedBy(tallier))
              }

              case Left(error) => {
                promisesPerTallier.values.toList
                  .traverse(_.error(error))
              }
            }
          } yield ()
      }

    } yield ()

  private def runForElectionAndTalliers(election: SenateElection, talliers: Set[Tallier]): IO[FetchTally.Error, TallyBundle] = for {
    divisionsPollingPlacesGroupsAndCandidates <- {
      FetchDivisionsAndFederalPollingPlaces.divisionsAndFederalPollingPlacesFor(election.federalElection).leftMap(FetchTally.Error(_)) par
        FetchSenateGroupsAndCandidates.senateGroupsAndCandidatesFor(election).leftMap(FetchTally.Error(_))
    }

    divisionsAndPollingPlaces = divisionsPollingPlacesGroupsAndCandidates._1
    groupsAndCandidates = divisionsPollingPlacesGroupsAndCandidates._2

    htvCards <- FetchSenateHtv.fetchFor(election, groupsAndCandidates.groups)
      .leftMap(FetchTally.Error)

    tallyBundle <- runForAllStatesAtElection(election, divisionsAndPollingPlaces, groupsAndCandidates, htvCards, talliers)

  } yield tallyBundle


  private def runForAllStatesAtElection(
                                         senateElection: SenateElection,
                                         divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                         groupsAndCandidates: SenateGroupsAndCandidates,
                                         htvCards: Map[SenateElectionForState, Set[SenateHtv]],
                                         talliers: Set[Tallier],
                                       ): IO[FetchTally.Error, TallyBundle] = {
    val electionsInSizeOrder = senateElection.allStateElections.toList.sortBy(_.state)(StateInstances.orderStatesByPopulation)

    electionsInSizeOrder.traverse { electionForState =>
      runForState(electionForState, divisionsAndPollingPlaces, groupsAndCandidates, htvCards.getOrElse(electionForState, Set.empty), talliers)
    }.map(_.foldLeft(TallyBundle())(_ + _))
  }

  private def runForState(
                           election: SenateElectionForState,
                           divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                           groupsAndCandidates: SenateGroupsAndCandidates,
                           htvCards: Set[SenateHtv],
                           talliers: Set[Tallier],
                         ): IO[FetchTally.Error, TallyBundle] = {
    val relevantDivisionsAndPollingPlaces = divisionsAndPollingPlaces.findFor(election.election.federalElection, election.state)
    val relevantGroupsAndCandidates = groupsAndCandidates.findFor(election)

    for {
      countData <- FetchSenateCountData.senateCountDataFor(election, groupsAndCandidates)
        .leftMap(FetchTally.Error)

      computationTools = buildComputationToolsFor(election, relevantGroupsAndCandidates, htvCards)
      computationInputData = ComputationInputData(
        ComputationInputData.ElectionLevelData(relevantDivisionsAndPollingPlaces, groupsAndCandidates, htvCards),
        ComputationInputData.StateLevelData(countData)
      )

      tallyBundle <- makeTallyBundleForElection(election, talliers, relevantDivisionsAndPollingPlaces, groupsAndCandidates, computationTools, computationInputData)

    } yield tallyBundle
  }

  private def buildComputationToolsFor(
                                        election: SenateElectionForState,
                                        groupsAndCandidates: SenateGroupsAndCandidates,
                                        howToVoteCards: Set[SenateHtv],
                                      ): ComputationTools = {

    val normaliser = BallotNormaliser(election, groupsAndCandidates.candidates)
    val matchingHowToVoteCalculator = MatchingHowToVoteCalculator(howToVoteCards)

    ComputationTools(
      ComputationTools.ElectionLevelTools(matchingHowToVoteCalculator),
      ComputationTools.StateLevelTools(normaliser)
    )
  }

  private def makeTallyBundleForElection(
                                          election: SenateElectionForState,
                                          talliers: Set[Tallier],
                                          divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                          groupsAndCandidates: SenateGroupsAndCandidates,
                                          computationTools: ComputationTools,
                                          computationInputData: ComputationInputData,
                                        ): IO[FetchTally.Error, TallyBundle] =

    for {
      ballotsStream <- FetchSenateBallots.senateBallotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces)
        .leftMap(FetchTally.Error)

      tallyStream = ballotsStream.chunkN(5000)
        .parEvalMap(maxConcurrent = 4) { chunk =>
          if (chunk.nonEmpty) {
            IO.sync {
              makeTallyBundleForBallots(election, talliers, computationTools, computationInputData, chunk.toVector)
            }.timedLog(
              eventId = "FORK_COMPUTED_TALLIES",
              "election" -> election.election,
              "state" -> election.state.abbreviation,
              "ballots_processed" -> chunk.size,
            ): IO[Nothing, TallyBundle]
          } else {
            IO.point(TallyBundle())
          }
        }(scalaz.zio.interop.catz.taskEffectInstances)
        .foldMonoid

      tallyBundle <- tallyStream.compile.lastOrError
        .swallowThrowablesAndWrapIn(FetchTally.Error)
    } yield tallyBundle

  //    IO.bracket(
  //      acquire = IO.syncException(parsedDataStore.ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces)),
  //    )(
  //      release = resource => IO.sync(resource.close()),
  //    ) { ballots =>
  //
  //      val numComputationForks = 4
  //      val ballotsProcessedPerFork = 5000
  //
  //      val groupedBallots = ballots.grouped(ballotsProcessedPerFork)
  //
  //      for {
  //        finalTallyBundleRef <- Ref(TallyBundle())
  //
  //        readBallotsMutex <- Semaphore(permits = 1)
  //        computationSemaphore <- Semaphore(permits = numComputationForks)
  //
  //        processChunksOp: IO[Nothing, Boolean] = computationSemaphore.withPermit {
  //          readBallotsMutex.withPermit {
  //            IO.sync { if (groupedBallots.hasNext) groupedBallots.next() else Nil }
  //          }.flatMap { ballotChunk =>
  //
  //            if (ballotChunk.nonEmpty) {
  //              IO.sync {
  //                makeTallyBundleForBallots(election, talliers, computationTools, computationInputData, ballotChunk.toVector)
  //              }.flatMap { tallyBundleForChunk =>
  //                finalTallyBundleRef.update(_ + tallyBundleForChunk)
  //              }.map { _ =>
  //                true
  //              }.timedLog(
  //                eventId = "FORK_COMPUTED_TALLIES",
  //                "election" -> election.election,
  //                "state" -> election.state.abbreviation,
  //                "ballots_processed" -> ballotChunk.size,
  //              ): IO[Nothing, Boolean]
  //            } else {
  //              IO.point(false)
  //            }
  //          }
  //        }
  //
  //        _ <- repeatedOp[Nothing, Boolean](processChunksOp, finishesWhen = shouldFinish => shouldFinish, numFibres = numComputationForks * 2)
  //
  //        finalTallyBundle <- finalTallyBundleRef.get
  //
  //        _ <- Log.logInfo(
  //          "COMPUTE_TALLIES_FOR_STATE",
  //          "election" -> election.election,
  //          "state" -> election.state.abbreviation,
  //          "talliers" -> talliers.map(_.name),
  //        )
  //
  //      } yield finalTallyBundle
  //
  //    }.leftMap {
  //      case e: FetchTally.Error => e
  //      case e: Exception => FetchTally.Error(e)
  //    }
  //
  //  private def repeatedOp[E, A](op: IO[E, A], finishesWhen: A => Boolean, numFibres: Int): IO[E, Unit] = {
  //    val workerTasks: List[IO[E, Unit]] = List.fill(numFibres)(op.repeat(Schedule.doWhile(finishesWhen)).map(_ => ()))
  //
  //    IO.parAll(workerTasks).map(_ => ())
  //  }

  private def makeTallyBundleForBallots(
                                         election: SenateElectionForState,
                                         talliers: Set[Tallier],
                                         computationTools: ComputationTools,
                                         computationInputData: ComputationInputData,
                                         ballots: Vector[SenateBallot],
                                       ): TallyBundle = {
    val ballotsWithFacts: Iterable[BallotWithFacts] = BallotFactsComputation.computeFactsFor(
      election,
      computationInputData,
      computationTools,
      ballots,
    )

    TallyBundle {
      talliers.map { tallier =>
        tallier -> tallier.tally(ballotsWithFacts)
      }.toMap
    }
  }


}

object FetchTallyImpl {

  def apply()(
    implicit
    fetchDivisionsAndPollingPlaces: FetchDivisionsAndFederalPollingPlaces[IO],
    fetchSenateGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO],
    fetchSenateCountData: FetchSenateCountData[IO],
    fetchSenateBallots: FetchSenateBallots[IO],
    fetchSenateHtv: FetchSenateHtv[IO],
    jsonCache: JsonCache[IO],
  ): IO[Nothing, FetchTallyImpl] =
    Semaphore(permits = 1).map(mutex => new FetchTallyImpl(mutex))

  private final case class TallyRequest(election: SenateElection, tallier: Tallier)

  private object TallyRequest {
    import TallierCodec._

    implicit val encoder: Encoder[TallyRequest] = Encoder.forProduct2("election", "tallier")(t => (t.election, t.tallier))
  }

}

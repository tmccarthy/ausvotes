package au.id.tmm.ausvotes.core.io_actions.implementations

import java.util.concurrent.TimeUnit

import au.id.tmm.ausvotes.core.engine.{ParsedDataStore, TallyEngine}
import au.id.tmm.ausvotes.core.io_actions.implementations.FetchTallyFromEngine.TallyRequest
import au.id.tmm.ausvotes.core.io_actions.{FetchDivisionsAndPollingPlaces, FetchSenateGroupsAndCandidates, FetchTally, JsonCache}
import au.id.tmm.ausvotes.core.model.{DivisionsAndPollingPlaces, GroupsAndCandidates}
import au.id.tmm.ausvotes.core.tallies._
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError._
import cats.instances.list._
import cats.syntax.traverse._
import io.circe.{Decoder, Encoder}
import scalaz.zio.duration.Duration
import scalaz.zio.interop.future.IOObjOps
import scalaz.zio.{IO, Promise, Semaphore}

import scala.collection.mutable

final class FetchTallyFromEngine private (
                                           parsedDataStore: ParsedDataStore, // TODO we should really abstract this away as well
                                           mutex: Semaphore,
                                         )(
                                           implicit
                                           fetchDivisionsAndPollingPlaces: FetchDivisionsAndPollingPlaces[IO],
                                           fetchSenateGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO],
                                           logging: Log[IO],
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

          val tallyBundleIo =
            for {
              divisionsAndPollingPlaces <- FetchDivisionsAndPollingPlaces.fetchFor(election.federalElection)
                .leftMap(FetchTally.Error(_))
              groupsAndCandidates <- FetchSenateGroupsAndCandidates.fetchFor(election)
                .leftMap(FetchTally.Error(_))

              tallyBundle <- runWithEngine(election, divisionsAndPollingPlaces, groupsAndCandidates, promisesPerTallier.keySet)

            } yield tallyBundle

          for {
            tallyBundleOrError <- tallyBundleIo.attempt
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

  private def runWithEngine(
                             senateElection: SenateElection,
                             divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                             groupsAndCandidates: GroupsAndCandidates,
                             talliers: Set[Tallier],
                           ): IO[FetchTally.Error, TallyBundle] =
    IO.fromFutureAction[TallyBundle] { ec =>
      TallyEngine.runFor(
        parsedDataStore,
        senateElection,
        senateElection.allStateElections.map(_.state),
        divisionsAndPollingPlaces,
        groupsAndCandidates,
        talliers
      )(ec)
    }.leftMap {
      case e: Exception => FetchTally.Error(e)
      case t: Throwable => throw t
    }

}

object FetchTallyFromEngine {

  def apply(
             parsedDataStore: ParsedDataStore,
           )(
             implicit
             fetchDivisionsAndPollingPlaces: FetchDivisionsAndPollingPlaces[IO],
             fetchSenateGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO],
             jsonCache: JsonCache[IO],
           ): IO[Nothing, FetchTallyFromEngine] =
    Semaphore(permits = 1).map(mutex => new FetchTallyFromEngine(parsedDataStore, mutex))

  private final case class TallyRequest(election: SenateElection, tallier: Tallier)

  private object TallyRequest {
    import TallierCodec._

    implicit val encoder: Encoder[TallyRequest] = Encoder.forProduct2("election", "tallier")(t => (t.election, t.tallier))
  }

}

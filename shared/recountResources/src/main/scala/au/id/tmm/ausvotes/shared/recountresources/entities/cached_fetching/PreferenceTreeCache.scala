package au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching

import java.io.ByteArrayInputStream

import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateElection, SenateElectionForState}
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree.{FetchPreferenceTreeException, GroupsCandidatesAndPreferences}
import au.id.tmm.countstv.model.preferences.PreferenceTreeSerialisation
import org.apache.commons.io.IOUtils
import scalaz.zio.{Fiber, IO, Promise, Semaphore}

import scala.collection.mutable

final class PreferenceTreeCache(
                                 private[cached_fetching] val groupsAndCandidatesCache: GroupsAndCandidatesCache,
                                 private val mutex: Semaphore,
                               ) extends FetchPreferenceTree[IO] {

  private[entities] def baseBucket = groupsAndCandidatesCache.baseBucket

  private val preferenceTreeBytes: CacheMap[FetchPreferenceTreeException, Array[Byte]] = mutable.Map()
  private val groupsCandidatesAndPreferences: CacheMap[FetchPreferenceTreeException, GroupsCandidatesAndPreferences] = mutable.Map()

  override def useGroupsCandidatesAndPreferencesWhileCaching[E, A](
                                                                    election: SenateElectionForState,
                                                                  )(
                                                                    handleEntityFetchError: FetchPreferenceTreeException => IO[E, A],
                                                                    handleCachePopulationError: FetchPreferenceTreeException => IO[E, Unit],
                                                                  )(
                                                                    action: GroupsCandidatesAndPreferences => IO[E, A],
                                                                  ): IO[E, A] = {

    val resultForkIO: IO[Nothing, Fiber[E, A]] =
      fetchGroupsCandidatesAndPreferencesFor(election)
        .attempt
        .flatMap {
          case Right(entities) => action(entities)
          case Left(entityFetchError) => handleEntityFetchError(entityFetchError)
        }
        .fork

    val populateCacheForkIO: IO[Nothing, Fiber[E, Unit]] =
      populateFor(election.election)
        .attempt
        .flatMap {
          case Right(_) => IO.unit
          case Left(cachePopulationError) => handleCachePopulationError(cachePopulationError)
        }
        .fork

    for {
      resultFork <- resultForkIO
      populateCacheFork <- populateCacheForkIO

      resultAndUnit <- (resultFork zip populateCacheFork).join
    } yield resultAndUnit._1
  }

  private def populateFor(election: SenateElection): IO[FetchPreferenceTreeException, Unit] = {
    IO.parAll {
      election.allStateElections.map { election =>
        fetchGroupsCandidatesAndPreferencesFor(election)
      }
    }.map(_ => Unit)
  }

  override def fetchGroupsCandidatesAndPreferencesFor(
                                                       election: SenateElectionForState,
                                                     ): IO[FetchPreferenceTreeException, FetchPreferenceTree.GroupsCandidatesAndPreferences] =
    for {
      promise <- groupsCandidatesAndPreferencesPromiseFor(election)
      groupsCandidatesAndPreferences <- promise.get
    } yield groupsCandidatesAndPreferences

  private def groupsCandidatesAndPreferencesPromiseFor(
                                                        election: SenateElectionForState,
                                                      ): IO[Nothing, Promise[FetchPreferenceTreeException, GroupsCandidatesAndPreferences]] =
    mutex.withPermit {
      getPromiseFor(election, groupsCandidatesAndPreferences, mutex) {
        for {
          groupsAndCandidatesPromise <- groupsAndCandidatesCache.groupsAndCandidatesPromiseFor(election)
          preferenceTreeBytesPromise <- getPreferenceTreeBytesPromise(election)

          groupsAndCandidates <- groupsAndCandidatesPromise.get
            .leftMap(FetchPreferenceTreeException.FetchGroupsAndCandidatesException)
          preferenceTreeBytes <- preferenceTreeBytesPromise.get

          preferenceTree <- IO.syncException {
            val inputStream = new ByteArrayInputStream(preferenceTreeBytes)

            PreferenceTreeSerialisation.deserialise[SenateCandidate](groupsAndCandidates.candidates, inputStream)
          }.timedLog(
            "COMPLETE_ENTITY_CACHE_PROMISE",
            "entity_name" -> "preference_tree",
            "election" -> election.election,
            "state" -> election.state,
          ).leftMap(FetchPreferenceTreeException.DeserialisationFetchPreferenceTreeException)
        } yield GroupsCandidatesAndPreferences(groupsAndCandidates, preferenceTree)
      }
    }

  private def getPreferenceTreeBytesPromise(
                                             election: SenateElectionForState,
                                           ): IO[Nothing, Promise[FetchPreferenceTreeException, Array[Byte]]] =
    getPromiseFor(election, preferenceTreeBytes, mutex) {
      val objectKey = EntityLocations.locationOfPreferenceTree(election)

      ReadsS3.useInputStream[IO, Array[Byte]](baseBucket, objectKey) { inputStream =>
        IO.syncException(IOUtils.toByteArray(inputStream))
      }.timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "preference_tree_bytes",
        "election" -> election.election,
        "state" -> election.state,
      ).leftMap(FetchPreferenceTreeException.LoadBytesExceptionFetch)
    }

}

object PreferenceTreeCache {
  def apply(groupsAndCandidatesCache: GroupsAndCandidatesCache): IO[Nothing, PreferenceTreeCache] =
    Semaphore(permits = 1).map(new PreferenceTreeCache(groupsAndCandidatesCache, _))
}

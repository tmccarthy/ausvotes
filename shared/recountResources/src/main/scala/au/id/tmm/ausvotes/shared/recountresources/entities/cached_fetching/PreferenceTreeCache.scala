package au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching

import java.io.ByteArrayInputStream

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree.{FetchPreferenceTreeException, GroupsCandidatesAndPreferences}
import au.id.tmm.countstv.model.preferences.PreferenceTreeSerialisation
import au.id.tmm.utilities.geo.australia.State
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
                                                                    election: SenateElection,
                                                                    state: State,
                                                                  )(
                                                                    handleEntityFetchError: FetchPreferenceTreeException => IO[E, A],
                                                                    handleCachePopulationError: FetchPreferenceTreeException => IO[E, Unit],
                                                                  )(
                                                                    action: GroupsCandidatesAndPreferences => IO[E, A],
                                                                  ): IO[E, A] = {

    val resultForkIO: IO[Nothing, Fiber[E, A]] =
      fetchGroupsCandidatesAndPreferencesFor(election, state)
        .attempt
        .flatMap {
          case Right(entities) => action(entities)
          case Left(entityFetchError) => handleEntityFetchError(entityFetchError)
        }
        .fork

    val populateCacheForkIO: IO[Nothing, Fiber[E, Unit]] =
      populateFor(election)
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
      State.ALL_STATES.map { state =>
        fetchGroupsCandidatesAndPreferencesFor(election, state)
      }
    }.map(_ => Unit)
  }

  override def fetchGroupsCandidatesAndPreferencesFor(
                                                       election: SenateElection,
                                                       state: State,
                                                     ): IO[FetchPreferenceTreeException, FetchPreferenceTree.GroupsCandidatesAndPreferences] =
    for {
      promise <- groupsCandidatesAndPreferencesPromiseFor(election, state)
      groupsCandidatesAndPreferences <- promise.get
    } yield groupsCandidatesAndPreferences

  private def groupsCandidatesAndPreferencesPromiseFor(
                                                        election: SenateElection,
                                                        state: State,
                                                      ): IO[Nothing, Promise[FetchPreferenceTreeException, GroupsCandidatesAndPreferences]] =
    mutex.withPermit {
      getPromiseFor(election, state, groupsCandidatesAndPreferences, mutex) {
        for {
          groupsAndCandidatesPromise <- groupsAndCandidatesCache.groupsAndCandidatesPromiseFor(election, state)
          preferenceTreeBytesPromise <- getPreferenceTreeBytesPromise(election, state)

          groupsAndCandidates <- groupsAndCandidatesPromise.get
            .leftMap(FetchPreferenceTreeException.FetchGroupsAndCandidatesException)
          preferenceTreeBytes <- preferenceTreeBytesPromise.get

          allCandidatePositions = groupsAndCandidates.candidates.map(_.btlPosition)

          preferenceTree <- IO.syncException {
            val inputStream = new ByteArrayInputStream(preferenceTreeBytes)

            PreferenceTreeSerialisation.deserialise(allCandidatePositions, inputStream)
          }.timedLog(
            "COMPLETE_ENTITY_CACHE_PROMISE",
            "entity_name" -> "preference_tree",
            "election" -> election,
            "state" -> state,
          ).leftMap(FetchPreferenceTreeException.DeserialisationFetchPreferenceTreeException)
        } yield GroupsCandidatesAndPreferences(groupsAndCandidates, preferenceTree)
      }
    }

  private def getPreferenceTreeBytesPromise(
                                             election: SenateElection,
                                             state: State,
                                           ): IO[Nothing, Promise[FetchPreferenceTreeException, Array[Byte]]] =
    getPromiseFor(election, state, preferenceTreeBytes, mutex) {
      val objectKey = EntityLocations.locationOfPreferenceTree(election, state)

      ReadsS3.useInputStream[IO, Array[Byte]](baseBucket, objectKey) { inputStream =>
        IO.syncException(IOUtils.toByteArray(inputStream))
      }.timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "preference_tree_bytes",
        "election" -> election,
        "state" -> state,
      ).leftMap(FetchPreferenceTreeException.LoadBytesExceptionFetch)
    }

}

object PreferenceTreeCache {
  def apply(groupsAndCandidatesCache: GroupsAndCandidatesCache): IO[Nothing, PreferenceTreeCache] =
    Semaphore(permits = 1).map(new PreferenceTreeCache(groupsAndCandidatesCache, _))
}

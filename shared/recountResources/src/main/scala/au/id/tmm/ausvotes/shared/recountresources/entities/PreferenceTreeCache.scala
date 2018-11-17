package au.id.tmm.ausvotes.shared.recountresources.entities

import java.io.ByteArrayInputStream

import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition, Group}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.shared.recountresources.entities.PreferenceTreeCache.{GroupsCandidatesAndPreferences, PreferenceTreeCacheException}
import au.id.tmm.ausvotes.shared.recountresources.entities.RecountEntityCacheUtils.getPromiseFor
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree
import au.id.tmm.countstv.model.preferences.PreferenceTreeSerialisation
import au.id.tmm.utilities.geo.australia.State
import org.apache.commons.io.IOUtils
import scalaz.zio.{IO, Promise, Semaphore}

import scala.collection.mutable

final class PreferenceTreeCache(
                                 val groupsAndCandidatesCache: GroupsAndCandidatesCache,
                                 private[entities] val mutex: Semaphore,
                               ) {

  private[entities] def baseBucket = groupsAndCandidatesCache.baseBucket

  private val preferenceTreeBytes: CacheMap[PreferenceTreeCacheException.PreferenceTreeFetchException, Array[Byte]] = mutable.Map()
  private val groupsCandidatesAndPreferences: CacheMap[PreferenceTreeCacheException, GroupsCandidatesAndPreferences] = mutable.Map()

  def withGroupsCandidatesAndPreferencesWhilePopulatingCache[E, A](
                                                                    election: SenateElection,
                                                                    state: State,
                                                                  )(
                                                                    action: GroupsCandidatesAndPreferences => IO[E, A],
                                                                    mapEntityFetchError: PreferenceTreeCacheException => E,
                                                                    mapCachePopulateError: PreferenceTreeCacheException => E,
                                                                  ): IO[E, A] = {
    val resultForkIO = (
      for {
        entitiesPromise <- groupsCandidatesAndPreferencesFor(election, state)
        entities <- entitiesPromise.get
          .leftMap(mapEntityFetchError)
        result <- action(entities)
      } yield result
      ).fork

    val populateCacheForkIO = (
      for {
        _ <- populateFor(election)
          .leftMap(mapCachePopulateError)
      } yield ()
      ).fork

    for {
      resultFork <- resultForkIO
      populateCacheFork <- populateCacheForkIO

      resultAndUnit <- (resultFork zip populateCacheFork).join
    } yield resultAndUnit._1
  }

  def populateFor(election: SenateElection): IO[PreferenceTreeCacheException, Unit] = {
    IO.parAll {
      State.ALL_STATES.map { state =>
        for {
          promise <- groupsCandidatesAndPreferencesFor(election, state)
          entities <- promise.get
        } yield entities
      }
    }.map(_ => ())
  }

  def groupsCandidatesAndPreferencesFor(
                                         election: SenateElection,
                                         state: State,
                                       ): IO[Nothing, Promise[PreferenceTreeCacheException, GroupsCandidatesAndPreferences]] =
    mutex.withPermit {
      getPromiseFor(election, state, groupsCandidatesAndPreferences, mutex) {
        for {
          groupsAndCandidatesPromise <- groupsAndCandidatesCache.groupsAndCandidatesFor(election, state)
          preferenceTreeBytesPromise <- getPreferenceTreeBytesPromise(election, state)

          groupsAndCandidates <- groupsAndCandidatesPromise.get
            .leftMap(PreferenceTreeCacheException.GroupsAndCandidatesFetchException)
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
          ).leftMap(PreferenceTreeCacheException.PreferenceTreeDeserialisationException)
        } yield GroupsCandidatesAndPreferences(groupsAndCandidates, preferenceTree)
      }
    }

  private def getPreferenceTreeBytesPromise(
                                             election: SenateElection,
                                             state: State,
                                           ): IO[Nothing, Promise[PreferenceTreeCacheException.PreferenceTreeFetchException, Array[Byte]]] =
    getPromiseFor(election, state, preferenceTreeBytes, mutex) {
      val objectKey = EntityLocations.locationOfPreferenceTree(election, state)

      ReadsS3.useInputStream[IO, Array[Byte]](baseBucket, objectKey) { inputStream =>
        IO.syncException(IOUtils.toByteArray(inputStream))
      }.timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "preference_tree_bytes",
        "election" -> election,
        "state" -> state,
      ).leftMap(PreferenceTreeCacheException.PreferenceTreeFetchException)
    }

}

object PreferenceTreeCache {

  def apply(groupsAndCandidatesCache: GroupsAndCandidatesCache): IO[Nothing, PreferenceTreeCache] =
    Semaphore(permits = 1).map(new PreferenceTreeCache(groupsAndCandidatesCache, _))

  final case class GroupsCandidatesAndPreferences(
                                                   groupsAndCandidates: GroupsAndCandidates,
                                                   preferenceTree: RootPreferenceTree[CandidatePosition],
                                                 ) {
    def groups: Set[Group] = groupsAndCandidates.groups
    def candidates: Set[Candidate] = groupsAndCandidates.candidates
  }

  sealed abstract class PreferenceTreeCacheException extends ExceptionCaseClass

  object PreferenceTreeCacheException {
    final case class GroupsAndCandidatesFetchException(cause: GroupsAndCandidatesCache.GroupsAndCandidatesCacheException) extends PreferenceTreeCacheException with ExceptionCaseClass.WithCause
    final case class PreferenceTreeFetchException(cause: Exception) extends PreferenceTreeCacheException with ExceptionCaseClass.WithCause
    final case class PreferenceTreeDeserialisationException(cause: Exception) extends PreferenceTreeCacheException with ExceptionCaseClass.WithCause
  }

}

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
import au.id.tmm.ausvotes.shared.recountresources.entities.GroupsAndCandidatesCache.groupsAndCandidatesFor
import au.id.tmm.ausvotes.shared.recountresources.entities.IRecountEntityCache.getPromiseFor
import au.id.tmm.ausvotes.shared.recountresources.entities.PreferenceTreeCache.{GroupsCandidatesAndPreferences, PreferenceTreeCacheException}
import au.id.tmm.countstv.model.preferences.{PreferenceTree, PreferenceTreeSerialisation}
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
}

object PreferenceTreeCache {

  def apply(groupsAndCandidatesCache: GroupsAndCandidatesCache): IO[Nothing, PreferenceTreeCache] =
    Semaphore(permits = 1).map(new PreferenceTreeCache(groupsAndCandidatesCache, _))

  def groupsCandidatesAndPreferencesFor(
                                         election: SenateElection,
                                         state: State,
                                       )(implicit
                                         cache: PreferenceTreeCache,
                                       ): IO[Nothing, Promise[PreferenceTreeCacheException, GroupsCandidatesAndPreferences]] = cache.mutex.withPermit {
    getPromiseFor(election, state, cache.groupsCandidatesAndPreferences, cache.mutex) {
      for {
        groupsAndCandidatesPromise <- groupsAndCandidatesFor(election, state)(cache.groupsAndCandidatesCache)
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
                                           )(implicit
                                             cache: PreferenceTreeCache,
                                           ): IO[Nothing, Promise[PreferenceTreeCacheException.PreferenceTreeFetchException, Array[Byte]]] =
    getPromiseFor(election, state, cache.preferenceTreeBytes, cache.mutex) {
      val objectKey = EntityLocations.locationOfPreferenceTree(election, state)

      ReadsS3.useInputStream[IO, Array[Byte]](cache.baseBucket, objectKey) { inputStream =>
        IO.syncException(IOUtils.toByteArray(inputStream))
      }.timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "preference_tree_bytes",
        "election" -> election,
        "state" -> state,
      ).leftMap(PreferenceTreeCacheException.PreferenceTreeFetchException)
    }

  final case class GroupsCandidatesAndPreferences(
                                                   groupsAndCandidates: GroupsAndCandidates,
                                                   preferenceTree: PreferenceTree[CandidatePosition],
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

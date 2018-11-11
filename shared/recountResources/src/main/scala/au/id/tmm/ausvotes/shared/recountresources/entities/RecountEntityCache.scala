package au.id.tmm.ausvotes.shared.recountresources.entities

import java.io.ByteArrayInputStream

import argonaut.{DecodeJson, DecodeResult, Json, Parse}
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition, Group, Party}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.Logging
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.shared.recountresources.entities.RecountEntityCache.{GroupsCandidatesAndPreferences, RecountEntityCacheException, StateAtElection}
import au.id.tmm.ausvotes.shared.recountresources.exceptions.InvalidJsonException
import au.id.tmm.countstv.model.preferences.{PreferenceTree, PreferenceTreeSerialisation}
import au.id.tmm.utilities.geo.australia.State
import org.apache.commons.io.IOUtils
import scalaz.zio.{IO, Promise, Semaphore}

import scala.collection.mutable

class RecountEntityCache private (
                                   private val baseBucket: S3BucketName,
                                   private val mutex: Semaphore,
                                 ) {

  private object codecs {
    implicit val decodeParty: DecodeJson[Party] = PartyCodec.decodeParty
    implicit val decodeGroup: DecodeJson[Group] = GroupCodec.decodeGroup
  }

  private val candidateJsons: mutable.Map[StateAtElection, Promise[RecountEntityCacheException, Json]] = mutable.Map()
  private val preferenceTreeBytes: mutable.Map[StateAtElection, Promise[RecountEntityCacheException, Array[Byte]]] = mutable.Map()

  private val groups: mutable.Map[StateAtElection, Promise[RecountEntityCacheException, Set[Group]]] = mutable.Map()

  private val groupsAndCandidates: mutable.Map[StateAtElection, Promise[RecountEntityCacheException, GroupsAndCandidates]] = mutable.Map()

  private val groupsCandidatesAndPreferences: mutable.Map[StateAtElection, Promise[RecountEntityCacheException, GroupsCandidatesAndPreferences]] = mutable.Map()

}

object RecountEntityCache {

  type GroupsCandidatesAndPreferences = (Set[Group], Set[Candidate], PreferenceTree.RootPreferenceTree[CandidatePosition])
  private type StateAtElection = (SenateElection, State)

  def apply(baseBucket: S3BucketName): IO[Nothing, RecountEntityCache] =
    Semaphore(permits = 1).map(new RecountEntityCache(baseBucket, _))

  def withGroupsCandidatesAndPreferencesWhilePopulatingCache[E, A](
                                                                    election: SenateElection,
                                                                    state: State,
                                                                  )(
                                                                    action: GroupsCandidatesAndPreferences => IO[E, A],
                                                                    mapEntityFetchError: RecountEntityCacheException => E,
                                                                    mapCachePopulateError: RecountEntityCacheException => E,
                                                                  )(implicit
                                                                    cache: RecountEntityCache,
                                                                  ): IO[E, A] = {
    for {
      recountEntityCache <- RecountEntityCache(S3BucketName("recount-data.buckets.ausvotes.info"))

      result = for {
        entitiesPromise <- RecountEntityCache.groupsCandidatesAndPreferencesFor(election, state)(recountEntityCache)
        entities <- entitiesPromise.get
            .leftMap(mapEntityFetchError)
        result <- action(entities)
      } yield result

      populateCache = for {
        _ <- RecountEntityCache.populateCacheFor(election)(recountEntityCache)
            .leftMap(mapCachePopulateError)
      } yield ()

      resultForkIO = result.fork
      populateCacheForkIO = populateCache.fork

      resultFork <- resultForkIO
      populateCacheFork <- populateCacheForkIO

      resultAndUnit <- (resultFork zip populateCacheFork).join
    } yield resultAndUnit._1
  }

  def populateCacheFor(election: SenateElection)(implicit cache: RecountEntityCache): IO[RecountEntityCacheException, Unit] = {
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
                                       )(implicit
                                         cache: RecountEntityCache,
                                       ): IO[Nothing, Promise[RecountEntityCacheException, GroupsCandidatesAndPreferences]] = {
    cache.mutex.withPermit(getGroupsCandidatesAndPreferencesPromise(election, state))
  }

  private def getGroupsCandidatesAndPreferencesPromise(
                                                        election: SenateElection,
                                                        state: State,
                                                      )(implicit
                                                        cache: RecountEntityCache,
                                                      ): IO[Nothing, Promise[RecountEntityCacheException, GroupsCandidatesAndPreferences]] =
    getPromiseFor(_.groupsCandidatesAndPreferences, election, state) {
      for {
        groupsAndCandidatesPromise <- getGroupsAndCandidatesPromise(election, state)
        preferenceTreeBytesPromise <- getPreferenceTreeBytesPromise(election, state)

        groupsAndCandidates <- groupsAndCandidatesPromise.get
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
        ).leftMap(RecountEntityCacheException.PreferenceTreeDeserialisationException)
      } yield (groupsAndCandidates.groups, groupsAndCandidates.candidates, preferenceTree)
    }

  private def getPreferenceTreeBytesPromise(
                                             election: SenateElection,
                                             state: State,
                                           )(implicit
                                             cache: RecountEntityCache,
                                           ): IO[Nothing, Promise[RecountEntityCacheException, Array[Byte]]] =
    getPromiseFor(_.preferenceTreeBytes, election, state) {
      val objectKey = EntityLocations.locationOfPreferenceTree(election, state)

      ReadsS3.useInputStream[IO, Array[Byte]](cache.baseBucket, objectKey) { inputStream =>
        IO.syncException(IOUtils.toByteArray(inputStream))
      }.timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "preference_tree_bytes",
        "election" -> election,
        "state" -> state,
      ).leftMap(RecountEntityCacheException.PreferenceTreeFetchException)
    }

  private def getGroupsAndCandidatesPromise(
                                             election: SenateElection,
                                             state: State,
                                           )(implicit
                                             cache: RecountEntityCache,
                                           ): IO[Nothing, Promise[RecountEntityCacheException, GroupsAndCandidates]] =
    getPromiseFor(_.groupsAndCandidates, election, state) {
      for {
        groupsPromise <- getGroupsPromise(election, state)
        candidateJsonPromise <- getCandidatesJsonPromise(election, state)

        groups <- groupsPromise.get
        candidatesJson <- candidateJsonPromise.get

        groupsAndCandidates <- Logging.timedLog(
          "COMPLETE_ENTITY_CACHE_PROMISE",
          "entity_name" -> "candidates",
          "election" -> election,
          "state" -> state,
        ) {
          import cache.codecs._

          implicit val decodeCandidates: DecodeJson[Candidate] = CandidateCodec.decodeCandidate(groups)

          candidatesJson.as[Set[Candidate]].toMessageOrResult.map { candidates =>
            GroupsAndCandidates(groups, candidates)
          }.left.map(RecountEntityCacheException.CandidateDecodeException)
        }
      } yield groupsAndCandidates
    }

  private def getCandidatesJsonPromise(
                                        election: SenateElection,
                                        state: State,
                                      )(implicit
                                        cache: RecountEntityCache,
                                      ): IO[Nothing, Promise[RecountEntityCacheException, Json]] =
    getPromiseFor(_.candidateJsons, election, state) {
      val objectKey = EntityLocations.locationOfCandidatesObject(election, state)

      (for {
        jsonString <- ReadsS3.readAsString(cache.baseBucket, objectKey)
          .leftMap(RecountEntityCacheException.CandidateFetchException)
        json <- IO.fromEither {
          val jsonOrErrorString = Parse.parse(jsonString)

          jsonOrErrorString
            .left.map(InvalidJsonException)
            .left.map(RecountEntityCacheException.CandidateJsonException)
        }
      } yield json).timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "candidates_json",
        "election" -> election,
        "state" -> state,
      )
    }

  private def getGroupsPromise(
                                election: SenateElection,
                                state: State,
                              )(implicit
                                cache: RecountEntityCache,
                              ): IO[Nothing, Promise[RecountEntityCacheException, Set[Group]]] =
    getPromiseFor(_.groups, election, state) {
      import cache.codecs._

      val objectKey = EntityLocations.locationOfGroupsObject(election, state)

      (for {
        jsonString <- ReadsS3.readAsString(cache.baseBucket, objectKey)
          .leftMap(RecountEntityCacheException.GroupFetchException)
        groups <- IO.fromEither {
          val decodeResult = Parse.decodeEither[Set[Group]](jsonString)

          decodeResult.left.map(RecountEntityCacheException.GroupDecodeException)
        }
      } yield groups).timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "groups",
        "election" -> election,
        "state" -> state,
      )
    }

  private def getPromiseFor[A](
                                promiseMapInCache: RecountEntityCache => mutable.Map[StateAtElection, Promise[RecountEntityCacheException, A]],
                                election: SenateElection,
                                state: State,
                              )(
                                fetch: IO[RecountEntityCacheException, A]
                              )(implicit
                                cache: RecountEntityCache,
                              ): IO[Nothing, Promise[RecountEntityCacheException, A]] = {
    promiseMapInCache(cache).get((election, state)).foreach(promise => return IO.point(promise))

    for {
      promise <- Promise.make[RecountEntityCacheException, A]

      fetchAndCompletePromise = fetch.attempt.flatMap {
        case Right(entity) => promise.complete(entity)
        case Left(exception) => cache.mutex.withPermit {
          promiseMapInCache(cache).remove((election, state))

          promise.error(exception)
        }
      }

      _ <- fetchAndCompletePromise.fork
    } yield {
      promiseMapInCache(cache).update((election, state), promise)

      promise
    }
  }

  // TODO make this shared
  implicit class DecodeResultOps[A](decodeResult: DecodeResult[A]) {
    def toMessageOrResult: Either[String, A] = decodeResult.toEither.left.map {
      case (message, cursorHistory) => message + ": " + cursorHistory.toString
    }
  }

  sealed abstract class RecountEntityCacheException extends ExceptionCaseClass

  object RecountEntityCacheException {
    final case class GroupFetchException(cause: Exception) extends RecountEntityCacheException with ExceptionCaseClass.WithCause
    final case class GroupDecodeException(message: String) extends RecountEntityCacheException
    final case class CandidateFetchException(cause: Exception) extends RecountEntityCacheException with ExceptionCaseClass.WithCause
    final case class CandidateJsonException(cause: InvalidJsonException) extends RecountEntityCacheException with ExceptionCaseClass.WithCause
    final case class CandidateDecodeException(message: String) extends RecountEntityCacheException
    final case class PreferenceTreeFetchException(cause: Exception) extends RecountEntityCacheException with ExceptionCaseClass.WithCause
    final case class PreferenceTreeDeserialisationException(cause: Exception) extends RecountEntityCacheException with ExceptionCaseClass.WithCause
  }
}

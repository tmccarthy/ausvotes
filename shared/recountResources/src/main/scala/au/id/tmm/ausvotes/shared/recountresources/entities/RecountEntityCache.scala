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
import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.shared.recountresources.entities.RecountEntityCache.{GroupsCandidatesAndPreferences, StateAtElection}
import au.id.tmm.ausvotes.shared.recountresources.exceptions.{CandidateDecodeException, GroupDecodeException, InvalidJsonException}
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

  private val candidateJsons: mutable.Map[StateAtElection, Promise[Exception, Json]] = mutable.Map()
  private val preferenceTreeBytes: mutable.Map[StateAtElection, Promise[Exception, Array[Byte]]] = mutable.Map()

  private val groups: mutable.Map[StateAtElection, Promise[Exception, Set[Group]]] = mutable.Map()

  private val groupsAndCandidates: mutable.Map[StateAtElection, Promise[Exception, GroupsAndCandidates]] = mutable.Map()

  private val groupsCandidatesAndPreferences: mutable.Map[StateAtElection, Promise[Exception, GroupsCandidatesAndPreferences]] = mutable.Map()

}

object RecountEntityCache {

  type GroupsCandidatesAndPreferences = (Set[Group], Set[Candidate], PreferenceTree[CandidatePosition])
  private type StateAtElection = (SenateElection, State)

  def apply(baseBucket: S3BucketName): IO[Nothing, RecountEntityCache] =
    Semaphore(permits = 1).map(new RecountEntityCache(baseBucket, _))

  def withGroupsCandidatesAndPreferencesWhilePopulatingCache[A](
                                                                 election: SenateElection,
                                                                 state: State,
                                                               )(
                                                                 action: GroupsCandidatesAndPreferences => IO[Exception, A],
                                                               )(implicit
                                                                 cache: RecountEntityCache,
                                                               ): IO[Exception, A] = {
    for {
      recountEntityCache <- RecountEntityCache(S3BucketName("recount-data.buckets.ausvotes.info"))

      result = for {
        entitiesPromise <- RecountEntityCache.groupsCandidatesAndPreferencesFor(election, state)(recountEntityCache)
        entities <- entitiesPromise.get
        result <- action(entities)
      } yield result

      populateCache = for {
        _ <- RecountEntityCache.populateCacheFor(election)(recountEntityCache)
      } yield ()

      resultForkIO = result.fork
      populateCacheForkIO = populateCache.fork

      resultFork <- resultForkIO
      populateCacheFork <- populateCacheForkIO

      resultAndUnit <- (resultFork zip populateCacheFork).join
    } yield resultAndUnit._1
  }

  def populateCacheFor(election: SenateElection)(implicit cache: RecountEntityCache): IO[Exception, Unit] = {
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
                                       ): IO[Nothing, Promise[Exception, GroupsCandidatesAndPreferences]] = {
    cache.mutex.withPermit(getGroupsCandidatesAndPreferencesPromise(election, state))
  }

  private def getGroupsCandidatesAndPreferencesPromise(
                                                        election: SenateElection,
                                                        state: State,
                                                      )(implicit
                                                        cache: RecountEntityCache,
                                                      ): IO[Nothing, Promise[Exception, GroupsCandidatesAndPreferences]] =
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
        )
      } yield (groupsAndCandidates.groups, groupsAndCandidates.candidates, preferenceTree)
    }

  private def getPreferenceTreeBytesPromise(
                                             election: SenateElection,
                                             state: State,
                                           )(implicit
                                             cache: RecountEntityCache,
                                           ): IO[Nothing, Promise[Exception, Array[Byte]]] =
    getPromiseFor(_.preferenceTreeBytes, election, state) {
      val objectKey = EntityLocations.locationOfPreferenceTree(election, state)

      ReadsS3.useInputStream[IO, Array[Byte]](cache.baseBucket, objectKey) { inputStream =>
        IO.syncException(IOUtils.toByteArray(inputStream))
      }.timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "preference_tree_bytes",
        "election" -> election,
        "state" -> state,
      )
    }

  private def getGroupsAndCandidatesPromise(
                                             election: SenateElection,
                                             state: State,
                                           )(implicit
                                             cache: RecountEntityCache,
                                           ): IO[Nothing, Promise[Exception, GroupsAndCandidates]] =
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
          }.left.map(CandidateDecodeException)
        }
      } yield groupsAndCandidates
    }

  private def getCandidatesJsonPromise(
                                        election: SenateElection,
                                        state: State,
                                      )(implicit
                                        cache: RecountEntityCache,
                                      ): IO[Nothing, Promise[Exception, Json]] =
    getPromiseFor(_.candidateJsons, election, state) {
      val objectKey = EntityLocations.locationOfCandidatesObject(election, state)

      (for {
        jsonString <- ReadsS3.readAsString(cache.baseBucket, objectKey)
        json <- IO.fromEither {
          val jsonOrErrorString = Parse.parse(jsonString)

          jsonOrErrorString.left.map(InvalidJsonException)
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
                              ): IO[Nothing, Promise[Exception, Set[Group]]] =
    getPromiseFor(_.groups, election, state) {
      import cache.codecs._

      val objectKey = EntityLocations.locationOfGroupsObject(election, state)

      (for {
        jsonString <- ReadsS3.readAsString(cache.baseBucket, objectKey)
        groups <- IO.fromEither {
          val decodeResult = Parse.decodeEither[Set[Group]](jsonString)

          decodeResult.left.map(GroupDecodeException)
        }
      } yield groups).timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "groups",
        "election" -> election,
        "state" -> state,
      )
    }

  private def getPromiseFor[A](
                                promiseMapInCache: RecountEntityCache => mutable.Map[StateAtElection, Promise[Exception, A]],
                                election: SenateElection,
                                state: State,
                              )(
                                fetch: IO[Exception, A]
                              )(implicit
                                cache: RecountEntityCache,
                              ): IO[Nothing, Promise[Exception, A]] = {
    promiseMapInCache(cache).get((election, state)).foreach(promise => return IO.point(promise))

    for {
      promise <- Promise.make[Exception, A]

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
}

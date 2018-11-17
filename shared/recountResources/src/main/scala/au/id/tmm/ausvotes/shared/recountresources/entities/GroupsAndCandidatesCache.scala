package au.id.tmm.ausvotes.shared.recountresources.entities

import argonaut.{DecodeJson, Json, Parse}
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.Logging
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.shared.recountresources.entities.GroupsAndCandidatesCache.GroupsAndCandidatesCacheException
import au.id.tmm.ausvotes.shared.recountresources.entities.RecountEntityCacheUtils.getPromiseFor
import au.id.tmm.ausvotes.shared.recountresources.exceptions.InvalidJsonException
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.{IO, Promise, Semaphore}

import scala.collection.mutable

final class GroupsAndCandidatesCache(
                                      val groupsCache: GroupsCache,
                                      private[entities] val mutex: Semaphore,
                                    ) {

  private[entities] def baseBucket = groupsCache.baseBucket

  private val candidateJsons: CacheMap[GroupsAndCandidatesCacheException, Json] = mutable.Map()

  private val groupsAndCandidates: CacheMap[GroupsAndCandidatesCacheException, GroupsAndCandidates] = mutable.Map()

  def groupsAndCandidatesFor(
                              election: SenateElection,
                              state: State,
                            ): IO[Nothing, Promise[GroupsAndCandidatesCacheException, GroupsAndCandidates]] =
    mutex.withPermit {
      getPromiseFor(election, state, groupsAndCandidates, mutex) {
        for {
          groupsPromise <- groupsCache.groupsFor(election, state)
          candidateJsonPromise <- getCandidatesJsonPromise(election, state)

          groups <- groupsPromise.get
            .leftMap(GroupsAndCandidatesCacheException.GroupsFetchException)
          candidatesJson <- candidateJsonPromise.get

          groupsAndCandidates <- Logging.timedLog(
            "COMPLETE_ENTITY_CACHE_PROMISE",
            "entity_name" -> "candidates",
            "election" -> election,
            "state" -> state,
          ) {
            import groupsCache.codecs._

            implicit val decodeCandidates: DecodeJson[Candidate] = CandidateCodec.decodeCandidate(groups)

            candidatesJson.as[Set[Candidate]].toMessageOrResult.map { candidates =>
              GroupsAndCandidates(groups, candidates)
            }.left.map(GroupsAndCandidatesCacheException.CandidateDecodeException)
          }
        } yield groupsAndCandidates
      }
    }

  private def getCandidatesJsonPromise(
                                        election: SenateElection,
                                        state: State,
                                      ): IO[Nothing, Promise[GroupsAndCandidatesCacheException, Json]] =
    getPromiseFor(election, state, candidateJsons, mutex) {
      val objectKey = EntityLocations.locationOfCandidatesObject(election, state)

      (for {
        jsonString <- ReadsS3.readAsString(baseBucket, objectKey)
          .leftMap(GroupsAndCandidatesCacheException.CandidateFetchException)
        json <- IO.fromEither {
          val jsonOrErrorString = Parse.parse(jsonString)

          jsonOrErrorString
            .left.map(InvalidJsonException)
            .left.map(GroupsAndCandidatesCacheException.CandidateJsonException)
        }
      } yield json).timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "candidates_json",
        "election" -> election,
        "state" -> state,
      )
    }

}

object GroupsAndCandidatesCache {

  def apply(groupsCache: GroupsCache): IO[Nothing, GroupsAndCandidatesCache] =
    Semaphore(permits = 1).map(new GroupsAndCandidatesCache(groupsCache, _))

  sealed abstract class GroupsAndCandidatesCacheException extends ExceptionCaseClass

  object GroupsAndCandidatesCacheException {
    final case class GroupsFetchException(cause: GroupsCache.GroupsCacheException) extends GroupsAndCandidatesCacheException with ExceptionCaseClass.WithCause
    final case class CandidateFetchException(cause: Exception) extends GroupsAndCandidatesCacheException with ExceptionCaseClass.WithCause
    final case class CandidateJsonException(cause: InvalidJsonException) extends GroupsAndCandidatesCacheException with ExceptionCaseClass.WithCause
    final case class CandidateDecodeException(message: String) extends GroupsAndCandidatesCacheException
  }

}

package au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching

import argonaut.{DecodeJson, DecodeResult, Json, Parse}
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, Group, Party}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.Logging
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates.FetchGroupsAndCandidatesException
import au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching.GroupsAndCandidatesCache.DecodeResultOps
import au.id.tmm.ausvotes.shared.recountresources.exceptions.InvalidJsonException
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.{IO, Promise, Semaphore}

import scala.collection.mutable

final class GroupsAndCandidatesCache(
                                      private[cached_fetching] val baseBucket: S3BucketName,
                                      private val mutex: Semaphore,
                                    ) extends FetchGroupsAndCandidates[IO] {
  private[cached_fetching] object codecs {
    implicit val decodeParty: DecodeJson[Party] = PartyCodec.decodeParty
    implicit val decodeGroup: DecodeJson[Group] = GroupCodec.decodeGroup
  }

  private val groups: CacheMap[FetchGroupsAndCandidatesException, Set[Group]] = mutable.Map()

  // TODO why bother storing the json?
  private val candidateJsons: CacheMap[FetchGroupsAndCandidatesException, Json] = mutable.Map()

  private val groupsAndCandidates: CacheMap[FetchGroupsAndCandidatesException, GroupsAndCandidates] = mutable.Map()

  override def fetchGroupsAndCandidatesFor(
                                            election: SenateElection,
                                            state: State,
                                          ): IO[FetchGroupsAndCandidatesException, GroupsAndCandidates] =
    for {
      promise <- groupsAndCandidatesPromiseFor(election, state)
      groupsAndCandidates <- promise.get
    } yield groupsAndCandidates

  private[cached_fetching] def groupsAndCandidatesPromiseFor(
                                                              election: SenateElection,
                                                              state: State,
                                                            ): IO[Nothing, Promise[FetchGroupsAndCandidatesException, GroupsAndCandidates]] =
    mutex.withPermit {
      getPromiseFor(election, state, groupsAndCandidates, mutex) {
        for {
          groupsPromise <- groupsPromiseFor(election, state)
          candidateJsonPromise <- getCandidatesJsonPromise(election, state)

          groups <- groupsPromise.get
          candidatesJson <- candidateJsonPromise.get

          groupsAndCandidates <- Logging.timedLog(
            "COMPLETE_ENTITY_CACHE_PROMISE",
            "entity_name" -> "candidates",
            "election" -> election,
            "state" -> state,
          ) {
            import codecs._

            implicit val decodeCandidates: DecodeJson[Candidate] = CandidateCodec.decodeCandidate(groups)

            candidatesJson.as[Set[Candidate]].toMessageOrResult.map { candidates =>
              GroupsAndCandidates(groups, candidates)
            }.left.map(FetchGroupsAndCandidatesException.DecodeCandidatesJsonException)
          }
        } yield groupsAndCandidates
      }
    }

  private def getCandidatesJsonPromise(
                                        election: SenateElection,
                                        state: State,
                                      ): IO[Nothing, Promise[FetchGroupsAndCandidatesException, Json]] =
    getPromiseFor(election, state, candidateJsons, mutex) {
      val objectKey = EntityLocations.locationOfCandidatesObject(election, state)

      (for {
        jsonString <- ReadsS3.readAsString(baseBucket, objectKey)
          .leftMap(FetchGroupsAndCandidatesException.LoadCandidatesJsonException)
        json <- IO.fromEither {
          val jsonOrErrorString = Parse.parse(jsonString)

          jsonOrErrorString
            .left.map(InvalidJsonException)
            .left.map(FetchGroupsAndCandidatesException.InvalidCandidatesJsonException)
        }
      } yield json).timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "candidates_json",
        "election" -> election,
        "state" -> state,
      )
    }

  private def groupsPromiseFor(
                                election: SenateElection,
                                state: State,
                              ): IO[Nothing, Promise[FetchGroupsAndCandidatesException, Set[Group]]] =
    getPromiseFor(election, state, groups, mutex) {
      import codecs._

      val objectKey = EntityLocations.locationOfGroupsObject(election, state)

      (for {
        jsonString <- ReadsS3.readAsString(baseBucket, objectKey)
          .leftMap(FetchGroupsAndCandidatesException.LoadGroupsJsonException)
        groups <- IO.fromEither {
          val decodeResult = Parse.decodeEither[Set[Group]](jsonString)

          decodeResult.left.map(FetchGroupsAndCandidatesException.DecodeGroupsJsonException)
        }
      } yield groups).timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "groups",
        "election" -> election,
        "state" -> state,
      )
    }

}

object GroupsAndCandidatesCache {

  def apply(baseBucket: S3BucketName): IO[Nothing, GroupsAndCandidatesCache] =
    Semaphore(permits = 1).map(new GroupsAndCandidatesCache(baseBucket, _))

  // TODO make this shared
  implicit class DecodeResultOps[A](decodeResult: DecodeResult[A]) {
    def toMessageOrResult: Either[String, A] = decodeResult.toEither.left.map {
      case (message, cursorHistory) => message + ": " + cursorHistory.toString
    }
  }
}

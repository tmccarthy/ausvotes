package au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching

import au.id.tmm.ausvotes.model.federal.senate.{SenateGroupsAndCandidates, _}
import au.id.tmm.ausvotes.model.stv.{CandidatePosition, Ungrouped}
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.Logging
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates.FetchGroupsAndCandidatesException
import au.id.tmm.ausvotes.shared.recountresources.exceptions.InvalidJsonException
import cats.syntax.show.toShow
import io.circe.parser._
import io.circe.{Decoder, Json}
import scalaz.zio.{IO, Promise, Semaphore}

import scala.collection.mutable

final class GroupsAndCandidatesCache(
                                      private[cached_fetching] val baseBucket: S3BucketName,
                                      private val mutex: Semaphore,
                                    ) extends FetchGroupsAndCandidates[IO] {

  private val groups: CacheMap[FetchGroupsAndCandidatesException, Set[SenateGroup]] = mutable.Map()

  // TODO why bother storing the json?
  private val candidateJsons: CacheMap[FetchGroupsAndCandidatesException, Json] = mutable.Map()

  private val groupsAndCandidates: CacheMap[FetchGroupsAndCandidatesException, SenateGroupsAndCandidates] = mutable.Map()

  override def fetchGroupsAndCandidatesFor(
                                            election: SenateElectionForState,
                                          ): IO[FetchGroupsAndCandidatesException, SenateGroupsAndCandidates] =
    for {
      promise <- groupsAndCandidatesPromiseFor(election)
      groupsAndCandidates <- promise.get
    } yield groupsAndCandidates

  private[cached_fetching] def groupsAndCandidatesPromiseFor(
                                                              election: SenateElectionForState,
                                                            ): IO[Nothing, Promise[FetchGroupsAndCandidatesException, SenateGroupsAndCandidates]] =
    mutex.withPermit {
      getPromiseFor(election, groupsAndCandidates, mutex) {
        for {
          groupsPromise <- groupsPromiseFor(election)
          candidateJsonPromise <- getCandidatesJsonPromise(election)

          groups <- groupsPromise.get
          candidatesJson <- candidateJsonPromise.get

          groupsAndCandidates <- Logging.timedLog(
            "COMPLETE_ENTITY_CACHE_PROMISE",
            "entity_name" -> "candidates",
            "election" -> election.election,
            "state" -> election.state,
          ) {

            implicit val candidatePositionDecoder: Decoder[SenateCandidatePosition] =
              CandidatePosition.decoderUsing(groups, Ungrouped(election))

            candidatesJson.as[Set[SenateCandidate]].left.map(_.show).map { candidates =>
              SenateGroupsAndCandidates(groups, candidates)
            }.left.map(FetchGroupsAndCandidatesException.DecodeCandidatesJsonException)
          }
        } yield groupsAndCandidates
      }
    }

  private def getCandidatesJsonPromise(
                                        election: SenateElectionForState,
                                      ): IO[Nothing, Promise[FetchGroupsAndCandidatesException, Json]] =
    getPromiseFor(election, candidateJsons, mutex) {
      val objectKey = EntityLocations.locationOfCandidatesObject(election)

      (for {
        jsonString <- ReadsS3.readAsString(baseBucket, objectKey)
          .leftMap(FetchGroupsAndCandidatesException.LoadCandidatesJsonException)
        json <- IO.fromEither {
          val jsonOrError = parse(jsonString)
              .left.map(_.show)

          jsonOrError
            .left.map(InvalidJsonException)
            .left.map(FetchGroupsAndCandidatesException.InvalidCandidatesJsonException)
        }
      } yield json).timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "candidates_json",
        "election" -> election.election,
        "state" -> election.state,
      )
    }

  private def groupsPromiseFor(
                                election: SenateElectionForState,
                              ): IO[Nothing, Promise[FetchGroupsAndCandidatesException, Set[SenateGroup]]] =
    getPromiseFor(election, groups, mutex) {

      val objectKey = EntityLocations.locationOfGroupsObject(election)

      (for {
        jsonString <- ReadsS3.readAsString(baseBucket, objectKey)
          .leftMap(FetchGroupsAndCandidatesException.LoadGroupsJsonException)
        groups <- IO.fromEither {
          for {
            parsedJson <- parse(jsonString)
                .left.map(e => FetchGroupsAndCandidatesException.DecodeGroupsJsonException(e.show))
            ballotGroups <- parsedJson.as[Set[SenateBallotGroup]]
                .left.map(e => FetchGroupsAndCandidatesException.DecodeGroupsJsonException(e.show))
          } yield ballotGroups.collect {
            case g: SenateGroup => g
          }
        }
      } yield groups).timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "groups",
        "election" -> election.election,
        "state" -> election.state,
      )
    }

}

object GroupsAndCandidatesCache {

  def apply(baseBucket: S3BucketName): IO[Nothing, GroupsAndCandidatesCache] =
    Semaphore(permits = 1).map(new GroupsAndCandidatesCache(baseBucket, _))

}

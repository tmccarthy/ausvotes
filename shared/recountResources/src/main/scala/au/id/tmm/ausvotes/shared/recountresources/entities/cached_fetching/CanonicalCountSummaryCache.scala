package au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching

import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidatePosition, SenateElectionForState}
import au.id.tmm.ausvotes.model.stv.{CandidatePosition, Ungrouped}
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountSummary
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountSummary.FetchCanonicalCountSummaryException
import au.id.tmm.ausvotes.shared.recountresources.{CountSummary, EntityLocations}
import au.id.tmm.bfect.BME._
import au.id.tmm.bfect.ziointerop._
import cats.syntax.show.toShow
import io.circe.Decoder
import io.circe.parser._
import scalaz.zio.{IO, Promise, Semaphore}

import scala.collection.mutable

final class CanonicalCountSummaryCache(
                                        private val groupsAndCandidatesCache: GroupsAndCandidatesCache,
                                        private val mutex: Semaphore,
                                      ) extends FetchCanonicalCountSummary[IO] {

  private def baseBucket = groupsAndCandidatesCache.baseBucket

  private val canonicalCountResults: CacheMap[FetchCanonicalCountSummaryException, CountSummary] = mutable.Map()

  override def fetchCanonicalCountSummaryFor(
                                              election: SenateElectionForState,
                                            ): IO[FetchCanonicalCountSummaryException, CountSummary] =
    for {
      promise <- canonicalCountPromiseFor(election)
      canonicalCount <- promise.await
    } yield canonicalCount

  private def canonicalCountPromiseFor(
                                        election: SenateElectionForState,
                                      ): IO[Nothing, Promise[FetchCanonicalCountSummaryException, CountSummary]] =
    mutex.withPermit {
      getPromiseFor(election, canonicalCountResults, mutex) {
        val objectKey = EntityLocations.locationOfCanonicalRecount(election)

        val fetchJsonLogic = ReadsS3.readAsString(baseBucket, objectKey)
          .leftMap(FetchCanonicalCountSummaryException.LoadCanonicalRecountJsonException)

        val fetchGroupsLogic = groupsAndCandidatesCache.senateGroupsAndCandidatesFor(election).map(_.groups)
          .leftMap(FetchCanonicalCountSummaryException.FetchGroupsAndCandidatesException)

        (fetchJsonLogic zipPar fetchGroupsLogic).map { case (canonicalResultJson, groups) =>

          implicit val candidatePositionDecoder: Decoder[SenateCandidatePosition] =
            CandidatePosition.decoderUsing(groups, Ungrouped(election))

          for {
            json <- parse(canonicalResultJson)
              .left.map(e => FetchCanonicalCountSummaryException.DecodeCanonicalRecountJsonException(e.show))

            countSummary <- json.as[CountSummary]
              .left.map(e => FetchCanonicalCountSummaryException.DecodeCanonicalRecountJsonException(e.show))

          } yield countSummary
        }.absolve
      }
    }
}

object CanonicalCountSummaryCache {
  def apply(groupsAndCandidatesCache: GroupsAndCandidatesCache): IO[Nothing, CanonicalCountSummaryCache] =
    Semaphore.make(permits = 1).map(new CanonicalCountSummaryCache(groupsAndCandidatesCache, _))
}

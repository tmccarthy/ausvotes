package au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching

import argonaut.{DecodeJson, Parse}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.EitherOps
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountSummary
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountSummary.FetchCanonicalCountSummaryException
import au.id.tmm.ausvotes.shared.recountresources.{CountSummary, EntityLocations}
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.{IO, Promise, Semaphore}

import scala.collection.mutable

final class CanonicalCountSummaryCache(
                                       private val groupsAndCandidatesCache: GroupsAndCandidatesCache,
                                       private val mutex: Semaphore,
                                     ) extends FetchCanonicalCountSummary[IO] {

  private def baseBucket = groupsAndCandidatesCache.baseBucket

  private val canonicalCountResults: CacheMap[FetchCanonicalCountSummaryException, CountSummary] = mutable.Map()

  override def fetchCanonicalCountSummaryFor(
                                             election: SenateElection,
                                             state: State,
                                           ): IO[FetchCanonicalCountSummaryException, CountSummary] =
    for {
      promise <- canonicalCountPromiseFor(election, state)
      canonicalCount <- promise.get
    } yield canonicalCount

  private def canonicalCountPromiseFor(
                                        election: SenateElection,
                                        state: State,
                                      ): IO[Nothing, Promise[FetchCanonicalCountSummaryException, CountSummary]] =
    mutex.withPermit {
      getPromiseFor(election, state, canonicalCountResults, mutex) {
        val objectKey = EntityLocations.locationOfCanonicalRecount(election, state)

        val fetchJsonLogic = ReadsS3.readAsString(baseBucket, objectKey)
          .leftMap(FetchCanonicalCountSummaryException.LoadCanonicalRecountJsonException)

        val fetchGroupsLogic = groupsAndCandidatesCache.fetchGroupsAndCandidatesFor(election, state).map(_.groups)
          .leftMap(FetchCanonicalCountSummaryException.FetchGroupsAndCandidatesException)

        (fetchJsonLogic par fetchGroupsLogic).map { case (canonicalResultJson, groups) =>
          import groupsAndCandidatesCache.codecs._

          implicit val decodeCandidates: DecodeJson[Candidate] = CandidateCodec.decodeCandidate(groups)

          Parse.decodeEither[CountSummary](canonicalResultJson)
            .left.map(FetchCanonicalCountSummaryException.DecodeCanonicalRecountJsonException)
        }.absolve
      }
    }
}

object CanonicalCountSummaryCache {
  def apply(groupsAndCandidatesCache: GroupsAndCandidatesCache): IO[Nothing, CanonicalCountSummaryCache] =
    Semaphore(permits = 1).map(new CanonicalCountSummaryCache(groupsAndCandidatesCache, _))
}

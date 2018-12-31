package au.id.tmm.ausvotes.shared.recountresources.entities.core_fetching

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateElectionForState}
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountResult.FetchCanonicalCountResultException
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.{FetchCanonicalCountResult, FetchGroupsAndCandidates}
import au.id.tmm.countstv.model.CompletedCount
import scalaz.zio.IO

class CanonicalRecountComputation(
                                   parsedDataStore: ParsedDataStore,
                                   fetchGroupsAndCandidates: FetchGroupsAndCandidates[IO],
                                 ) extends FetchCanonicalCountResult[IO]{

  override def fetchCanonicalCountResultFor(
                                             election: SenateElectionForState,
                                           ): IO[FetchCanonicalCountResultException, CompletedCount[SenateCandidate]] =
    for {
      groupsAndCandidates <- fetchGroupsAndCandidates.fetchGroupsAndCandidatesFor(election)
          .leftMap(FetchCanonicalCountResultException.FetchGroupsAndCandidatesException)

      canonicalRecount <- IO.syncException {
        parsedDataStore.countDataFor(election, groupsAndCandidates)
      }.leftMap(FetchCanonicalCountResultException.BuildCanonicalRecountException)
    } yield canonicalRecount.completedCount
}

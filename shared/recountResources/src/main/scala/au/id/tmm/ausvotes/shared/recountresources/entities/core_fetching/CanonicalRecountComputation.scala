package au.id.tmm.ausvotes.shared.recountresources.entities.core_fetching

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountResult.FetchCanonicalCountResultException
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.{FetchCanonicalCountResult, FetchGroupsAndCandidates}
import au.id.tmm.countstv.model.CompletedCount
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.IO

class CanonicalRecountComputation(
                                   parsedDataStore: ParsedDataStore,
                                   fetchGroupsAndCandidates: FetchGroupsAndCandidates[IO],
                                 ) extends FetchCanonicalCountResult[IO]{

  override def fetchCanonicalCountResultFor(
                                             election: SenateElection,
                                             state: State,
                                           ): IO[FetchCanonicalCountResultException, CompletedCount[Candidate]] =
    for {
      groupsAndCandidates <- fetchGroupsAndCandidates.fetchGroupsAndCandidatesFor(election, state)
          .leftMap(FetchCanonicalCountResultException.FetchGroupsAndCandidatesException)

      canonicalRecount <- IO.syncException {
        parsedDataStore.countDataFor(election, groupsAndCandidates, state)
      }.leftMap(FetchCanonicalCountResultException.BuildCanonicalRecountException)
    } yield canonicalRecount.completedCount // TODO fix
}

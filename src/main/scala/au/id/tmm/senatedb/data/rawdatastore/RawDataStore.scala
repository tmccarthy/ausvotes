package au.id.tmm.senatedb.data.rawdatastore

import java.nio.file.Path

import au.id.tmm.senatedb.data.BallotWithPreferences
import au.id.tmm.senatedb.data.database.{CandidatesRow, GroupsRow}
import au.id.tmm.senatedb.data.rawdatastore.download.{LoadingFirstPreferences, LoadingFormalPreferences}
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.{parseFirstPreferencesCsv, parseFormalPreferencesCsv}
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.collection.CloseableIterator

import scala.util.Try

final class RawDataStore private (val location: Path) {

  def retrieveBallots(election: SenateElection,
                      state: State,
                      allCandidates: Set[CandidatesRow],
                      downloadAllowed: Boolean = true): Try[CloseableIterator[BallotWithPreferences]] = {
    for {
      csvSource <- LoadingFormalPreferences.csvLinesOf(location, election, state, downloadAllowed)
      ballots <- parseFormalPreferencesCsv(election, state, allCandidates, csvSource)
    } yield ballots
  }

  def retrieveGroupsAndCandidates(election: SenateElection,
                                  downloadAllowed: Boolean = true): Try[(Set[GroupsRow], Set[CandidatesRow])] = {

    LoadingFirstPreferences.csvLinesOf(location, election, downloadAllowed)
      .flatMap { csvSource =>
        try {
          parseFirstPreferencesCsv(election, csvSource)
        } finally {
          csvSource.close()
        }
      }
  }

}

object RawDataStore {
  def apply(location: Path): RawDataStore = new RawDataStore(location)
}
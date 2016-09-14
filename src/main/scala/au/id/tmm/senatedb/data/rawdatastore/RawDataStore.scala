package au.id.tmm.senatedb.data.rawdatastore

import java.nio.file.Path

import au.id.tmm.senatedb.data.database.model.CandidatesRow
import au.id.tmm.senatedb.data.rawdatastore.download.{LoadingDistributionsOfPreferences, LoadingFirstPreferences, LoadingFormalPreferences}
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences.parseDistributionOfPreferencesCsv
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.formalpreferences.parseFormalPreferencesCsv
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.parseFirstPreferencesCsv
import au.id.tmm.senatedb.data.{BallotWithPreferences, CountData, GroupsAndCandidates}
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.collection.CloseableIterator

import scala.util.Try

final class RawDataStore private (val location: Path) {

  def retrieveBallots(election: SenateElection,
                      state: State,
                      groupsAndCandidates: GroupsAndCandidates,
                      countData: CountData,
                      downloadAllowed: Boolean = true): Try[CloseableIterator[BallotWithPreferences]] = {
    for {
      csvSource <- LoadingFormalPreferences.csvLinesOf(location, election, state, downloadAllowed)
      ballots <- parseFormalPreferencesCsv(election, state, groupsAndCandidates, countData, csvSource)
    } yield ballots
  }

  def retrieveGroupsAndCandidates(election: SenateElection,
                                  downloadAllowed: Boolean = true): Try[GroupsAndCandidates] = {

    LoadingFirstPreferences.csvLinesOf(location, election, downloadAllowed)
      .flatMap { csvSource =>
        try {
          parseFirstPreferencesCsv(election, csvSource)
        } finally {
          csvSource.close()
        }
      }
  }

  def retrieveCountData(election: SenateElection,
                        state: State,
                        allCandidates: Set[CandidatesRow],
                        downloadAllowed: Boolean = true): Try[CountData] = {
    for {
      csvSource <- LoadingDistributionsOfPreferences.csvLinesOf(location, election, state, downloadAllowed)
      countData <- parseDistributionOfPreferencesCsv(election, state, allCandidates, csvSource)
    } yield countData
  }
}

object RawDataStore {
  def apply(location: Path): RawDataStore = new RawDataStore(location)
}
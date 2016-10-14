package au.id.tmm.senatedb.rawdata

import java.nio.file.Path

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.csv.{ParsingDistributionOfPreferences, ParsingFirstPreferences, ParsingFormalPreferences}
import au.id.tmm.senatedb.rawdata.download.{LoadingDistributionsOfPreferences, LoadingFirstPreferences, LoadingFormalPreferences}
import au.id.tmm.senatedb.rawdata.model.{DistributionOfPreferencesRow, FirstPreferencesRow, FormalPreferencesRow}
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State

import scala.util.Try

final class RawDataStore private (val location: Path) {
  def distributionsOfPreferencesFor(election: SenateElection, state: State): Try[CloseableIterator[DistributionOfPreferencesRow]] = {
    LoadingDistributionsOfPreferences.csvLinesOf(location, election, state)
      .flatMap(ParsingDistributionOfPreferences.parseLines)
  }

  def firstPreferencesFor(election: SenateElection): Try[CloseableIterator[FirstPreferencesRow]] = {
    LoadingFirstPreferences.csvLinesOf(location, election)
      .flatMap(ParsingFirstPreferences.parseLines)
  }

  def formalPreferencesFor(election: SenateElection, state: State): Try[CloseableIterator[FormalPreferencesRow]] = {
    LoadingFormalPreferences.csvLinesOf(location, election, state)
      .flatMap(ParsingFormalPreferences.parseLines)
  }
}


object RawDataStore {
  def apply(location: Path): RawDataStore = new RawDataStore(location)
}
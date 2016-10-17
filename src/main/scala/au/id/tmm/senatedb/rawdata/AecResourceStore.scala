package au.id.tmm.senatedb.rawdata

import java.nio.file.Path

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.download.{LoadingDistributionsOfPreferences, LoadingFirstPreferences, LoadingFormalPreferences, LoadingPollingPlaces}
import au.id.tmm.utilities.geo.australia.State

import scala.io.Source
import scala.util.Try

trait AecResourceStore {
  def distributionOfPreferencesFor(election: SenateElection, state: State): Try[Source]
  def firstPreferencesFor(election: SenateElection): Try[Source]
  def formalPreferencesFor(election: SenateElection, state: State): Try[Source]
  def pollingPlacesFor(election: SenateElection): Try[Source]
}

private final class LocalAecResourceStore(val location: Path) extends AecResourceStore {
  override def distributionOfPreferencesFor(election: SenateElection, state: State): Try[Source] =
    LoadingDistributionsOfPreferences.csvLinesOf(location, election, state)

  override def firstPreferencesFor(election: SenateElection): Try[Source] =
    LoadingFirstPreferences.csvLinesOf(location, election)

  override def formalPreferencesFor(election: SenateElection, state: State): Try[Source] =
    LoadingFormalPreferences.csvLinesOf(location, election, state)

  override def pollingPlacesFor(election: SenateElection): Try[Source] =
    LoadingPollingPlaces.csvLinesOf(location, election)
}

object AecResourceStore {
  def at(location: Path): AecResourceStore = new LocalAecResourceStore(location)
}
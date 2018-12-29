package au.id.tmm.ausvotes.core.rawdata.download

import java.nio.file.Path

import au.id.tmm.ausvotes.core.rawdata.resources.FirstPreferencesResource
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.Try

// Note that we don't perform a data integrity check here because this file is generated dynamically on download, and
// has a timestamp
object LoadingFirstPreferences {

  def csvLinesOf(localRawDataFile: Path): Try[Source] = {
    for {
      source <- Try(Source.fromFile(localRawDataFile.toFile))
    } yield source
  }

  def resourceMatching(election: SenateElection): Try[FirstPreferencesResource] =
    FirstPreferencesResource.of(election)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find raw first preferences data for $election"))
}

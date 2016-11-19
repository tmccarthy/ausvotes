package au.id.tmm.senatedb.rawdata.download

import java.nio.file.Path

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.resources.FirstPreferencesResource
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.Try

// Note that we don't perform a data integrity check here because this file is generated dynamically on download, and
// has a timestamp
object LoadingFirstPreferences {

  def csvLinesOf(dataDir: Path, election: SenateElection): Try[Source] = {
    for {
      matchingResource <- resourceMatching(election)
      localRawDataFile <- StorageUtils.findRawDataFor(dataDir, matchingResource)
      source <- Try(Source.fromFile(localRawDataFile.toFile))
    } yield source
  }

  private def resourceMatching(election: SenateElection): Try[FirstPreferencesResource] =
    FirstPreferencesResource.of(election)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find raw first preferences data for $election"))
}

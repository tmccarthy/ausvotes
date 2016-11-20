package au.id.tmm.senatedb.core.rawdata.download

import java.nio.file.Path

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.rawdata.resources.PollingPlacesResource
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.Try

object LoadingPollingPlaces {
  def csvLinesOf(dataDir: Path, election: SenateElection): Try[Source] = {
    for {
      matchingResource <- resourceMatching(election)
      localRawDataFile <- StorageUtils.findRawDataFor(dataDir, matchingResource)
      source <- Try(Source.fromFile(localRawDataFile.toFile))
    } yield source
  }

  private def resourceMatching(election: SenateElection): Try[PollingPlacesResource] =
    PollingPlacesResource.of(election)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find raw polling places data for $election"))

}

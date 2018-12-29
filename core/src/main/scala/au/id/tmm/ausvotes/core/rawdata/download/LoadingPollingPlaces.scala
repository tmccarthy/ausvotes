package au.id.tmm.ausvotes.core.rawdata.download

import java.nio.file.Path

import au.id.tmm.ausvotes.core.rawdata.resources.PollingPlacesResource
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.utilities.option.OptionUtils.ImprovedOption

import scala.io.Source
import scala.util.Try

object LoadingPollingPlaces {
  def csvLinesOf(localRawDataFile: Path): Try[Source] = {
    for {
      source <- Try(Source.fromFile(localRawDataFile.toFile))
    } yield source
  }

  def resourceMatching(election: FederalElection): Try[PollingPlacesResource] =
    PollingPlacesResource.of(election)
      .failIfAbsent(new UnsupportedOperationException(s"Could not find raw polling places data for $election"))

}

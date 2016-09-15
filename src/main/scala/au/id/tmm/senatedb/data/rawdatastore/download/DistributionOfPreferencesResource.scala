package au.id.tmm.senatedb.data.rawdatastore.download

import java.net.URL
import java.nio.file.{Path, Paths}

import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.hashing.Digest

private[this] final case class DistributionOfPreferencesResource(election: SenateElection, digest: Digest) {
  private val nameSansExtension = s"SenateDopDownload-${election.aecID}"

  val url: URL = new URL(s"http://results.aec.gov.au/20499/Website/External/$nameSansExtension.zip")

  val localFilePath: Path = Paths.get(s"$nameSansExtension.zip")

  def zipEntryNameOf(state: State): String = s"SenateStateDOPDownload-${election.aecID}-${state.shortName.toUpperCase}.csv"
}

private[this] object DistributionOfPreferencesResource {
  val `2016` = DistributionOfPreferencesResource(SenateElection.`2016`,
    Digest("c55be91a1c8d7f9b06ff2b3d3f128947c5c807f0ac4efd6045318eeecaa05f37"))

  def of(election: SenateElection): Option[DistributionOfPreferencesResource] = election match {
    case SenateElection.`2016` => Some(`2016`)
    case _ => None
  }
}
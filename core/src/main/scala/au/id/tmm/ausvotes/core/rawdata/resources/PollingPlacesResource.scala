package au.id.tmm.ausvotes.core.rawdata.resources

import java.net.URL
import java.nio.file.{Path, Paths}

import au.id.tmm.ausvotes.core.model.SenateElection

final case class PollingPlacesResource(election: SenateElection) extends Resource {
  private val nameSansExtension = s"GeneralPollingPlacesDownload-${election.aecID}"

  override val url: URL = new URL(s"http://results.aec.gov.au/20499/Website/Downloads/$nameSansExtension.csv")

  override val localFileName: Path = Paths.get(s"$nameSansExtension.csv").getFileName
}


object PollingPlacesResource {
  val `2016` = PollingPlacesResource(SenateElection.`2016`)

  def of(election: SenateElection) = election match {
    case SenateElection.`2016` => Some(`2016`)
    case _ => None
  }
}
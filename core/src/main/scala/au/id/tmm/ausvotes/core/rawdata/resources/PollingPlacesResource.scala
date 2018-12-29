package au.id.tmm.ausvotes.core.rawdata.resources

import java.net.URL
import java.nio.file.{Path, Paths}

import au.id.tmm.ausvotes.model.federal.FederalElection

final case class PollingPlacesResource(election: FederalElection) extends Resource {
  private val nameSansExtension = s"GeneralPollingPlacesDownload-${election.id.asString}"

  override val url: URL = new URL(s"https://results.aec.gov.au/20499/Website/Downloads/$nameSansExtension.csv")

  override val localFileName: Path = Paths.get(s"$nameSansExtension.csv").getFileName
}


object PollingPlacesResource {
  val `2016` = PollingPlacesResource(FederalElection.`2016`)

  def of(election: FederalElection): Option[PollingPlacesResource] = election match {
    case FederalElection.`2016` => Some(`2016`)
    case _ => None
  }
}

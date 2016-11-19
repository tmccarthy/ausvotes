package au.id.tmm.senatedb.rawdata.resources

import java.net.URL
import java.nio.file.{Path, Paths}

import au.id.tmm.senatedb.model.SenateElection

final case class FirstPreferencesResource(election: SenateElection) extends Resource {
  private val nameSansExtension = s"SenateFirstPrefsByStateByVoteTypeDownload-${election.aecID}"

  override val url: URL = new URL(s"http://results.aec.gov.au/20499/Website/Downloads/$nameSansExtension.csv")

  override val localFileName: Path = Paths.get(s"$nameSansExtension.csv").getFileName
}

object FirstPreferencesResource {
  val `2016` = FirstPreferencesResource(SenateElection.`2016`)

  def of(election: SenateElection) = election match {
    case SenateElection.`2016` => Some(`2016`)
    case _ => None
  }
}
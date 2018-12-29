package au.id.tmm.ausvotes.core.rawdata.resources

import java.net.URL
import java.nio.file.{Path, Paths}

import au.id.tmm.ausvotes.model.federal.senate.SenateElection

final case class FirstPreferencesResource(election: SenateElection) extends Resource {
  private val nameSansExtension = s"SenateFirstPrefsByStateByVoteTypeDownload-${election.id.asString}"

  override val url: URL = new URL(s"https://results.aec.gov.au/20499/Website/Downloads/$nameSansExtension.csv")

  override val localFileName: Path = Paths.get(s"$nameSansExtension.csv").getFileName
}

object FirstPreferencesResource {
  val `2016` = FirstPreferencesResource(SenateElection.`2016`)

  def of(election: SenateElection): Option[FirstPreferencesResource] = election match {
    case SenateElection.`2016` => Some(`2016`)
    case _ => None
  }
}

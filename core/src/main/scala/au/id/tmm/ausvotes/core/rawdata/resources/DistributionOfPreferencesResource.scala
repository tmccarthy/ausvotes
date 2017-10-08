package au.id.tmm.ausvotes.core.rawdata.resources

import java.net.URL
import java.nio.file.{Path, Paths}

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.hashing.Digest

final case class DistributionOfPreferencesResource(election: SenateElection, digest: Digest) extends ResourceWithDigest {
  private val nameSansExtension = s"SenateDopDownload-${election.aecID}"

  override val url: URL = new URL(s"http://results.aec.gov.au/20499/Website/External/$nameSansExtension.zip")

  override val localFileName: Path = Paths.get(s"$nameSansExtension.zip")

  def zipEntryNameOf(state: State): String = s"SenateStateDOPDownload-${election.aecID}-${state.abbreviation.toUpperCase}.csv"
}

object DistributionOfPreferencesResource {
  val `2016` = DistributionOfPreferencesResource(SenateElection.`2016`,
    Digest("9c64a5a8d535fe9b28704c2214d5b65f6b54ccfee0e1fa22f7612f1410df6bb1"))

  def of(election: SenateElection): Option[DistributionOfPreferencesResource] = election match {
    case SenateElection.`2016` => Some(`2016`)
    case _ => None
  }
}
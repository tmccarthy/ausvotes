package au.id.tmm.senatedb.download

import java.net.URL
import java.nio.file.{Path, Paths}

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.hashing.Digest

private[download] final case class FirstPreferencesResource(election: SenateElection, digest: Digest) {
  private val nameSansExtension = s"SenateFirstPrefsByStateByVoteTypeDownload-${election.aecID}"

  val url: URL = new URL(s"http://vtr.aec.gov.au/Downloads/$nameSansExtension.csv")

  val localFileName: Path = Paths.get(s"$nameSansExtension.csv").getFileName
}

object FirstPreferencesResource {
  val `2016` = FirstPreferencesResource(SenateElection.`2016`,
    Digest("02a679c33881fbef59ccdd0ea86b8c58951a1f22069a1a99691181c96b0a66af"))

  def of(election: SenateElection) = election match {
    case SenateElection.`2016` => Some(`2016`)
    case _ => None
  }
}
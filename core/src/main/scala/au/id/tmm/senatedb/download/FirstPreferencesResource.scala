package au.id.tmm.senatedb.download

import java.net.URL
import java.nio.file.{Path, Paths}

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.hashing.Digest

private[download] final case class FirstPreferencesResource(election: SenateElection, digest: Digest) {
  private val nameSansExtension = s"SenateFirstPrefsByStateByVoteTypeDownload-${election.aecID}"

  val url: URL = new URL(s"http://vtr.aec.gov.au/External/$nameSansExtension.csv")

  val localFileName: Path = Paths.get(s"$nameSansExtension.csv").getFileName
}

object FirstPreferencesResource {
  val `2016` = FirstPreferencesResource(SenateElection.`2016`,
    Digest("e970c6201bcfe15948c2e0a356c5a537ffb31b0fd0d38103cc4e849e75c10148"))

  def of(election: SenateElection) = election match {
    case SenateElection.`2016` => Some(`2016`)
    case _ => None
  }
}
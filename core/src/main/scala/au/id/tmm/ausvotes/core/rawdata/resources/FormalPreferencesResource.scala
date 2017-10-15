package au.id.tmm.ausvotes.core.rawdata.resources

import java.net.URL
import java.nio.file.{Path, Paths}

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.SenateElection._
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.geo.australia.State._
import au.id.tmm.utilities.hashing.Digest

final case class FormalPreferencesResource private(election: SenateElection,
                                                   state: State,
                                                   digest: Digest) extends ResourceWithDigest {
  private val nameSansExtension = s"aec-senate-formalpreferences-${election.aecID}-${state.abbreviation}"

  override val url: URL = new URL(s"http://results.aec.gov.au/20499/Website/External/$nameSansExtension.zip")

  override val localFileName: Path = Paths.get(s"$nameSansExtension.zip").getFileName

  val zipEntryName: String = s"$nameSansExtension.csv"
}

object FormalPreferencesResource {
  val NSW_2016 = FormalPreferencesResource(`2016`, NSW,
    Digest("62911ba83f46d539de6d00d8ac8046904c1a2de51e5ac73c46c3bd6f1a5b2c8c"))

  val QLD_2016 = FormalPreferencesResource(`2016`, QLD,
    Digest("a1313274de80d70ea1642cde59e9dea9491c6bfcd8adc1681fa70b72323c0e9e"))

  val SA_2016 = FormalPreferencesResource(`2016`, SA,
    Digest("aa9ad4b53c7ba7e3b6d286a9e58ae1ef2ef9899b585f916e7b644a5897eeaca7"))

  val TAS_2016 = FormalPreferencesResource(`2016`, TAS,
    Digest("216069ef5641e7cc73cff193108638742fff6bba77a09bb7f6999bf0dc5d2c14"))

  val VIC_2016 = FormalPreferencesResource(`2016`, VIC,
    Digest("0bb8a977931c2d9e340fe234f3ce8da67cac82bf293b11eb0e31948765be59f1"))

  val WA_2016 = FormalPreferencesResource(`2016`, WA,
    Digest("969a0ea31bb1cebacb145ae76e55d0902f31b16f4c3b9c396e84e6ae39690ffc"))

  val NT_2016 = FormalPreferencesResource(`2016`, NT,
    Digest("82d3980b7654f79765e125397e711a943ca2f56f558db43259435879ec9fdd1a"))

  val ACT_2016 = FormalPreferencesResource(`2016`, ACT,
    Digest("81f30f3bbbc66d01dd11f6288cb7e7567c2eec037a8eeaca69ce88a722fb86c0"))


  def of(election: SenateElection, state: State): Option[FormalPreferencesResource] = {
    election match {
      case `2016` => Some(for2016(state))
      case _ => None
    }
  }

  def for2016(state: State): FormalPreferencesResource = {
    state match {
      case NSW => NSW_2016
      case QLD => QLD_2016
      case SA => SA_2016
      case TAS => TAS_2016
      case VIC => VIC_2016
      case WA => WA_2016
      case NT => NT_2016
      case ACT => ACT_2016
    }
  }
}
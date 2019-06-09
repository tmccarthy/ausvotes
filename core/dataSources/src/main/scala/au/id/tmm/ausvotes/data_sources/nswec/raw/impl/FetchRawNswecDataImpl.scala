package au.id.tmm.ausvotes.data_sources.nswec.raw.impl

import au.id.tmm.ausvotes.data_sources.common.CsvParsing._
import au.id.tmm.ausvotes.data_sources.common.{CsvStreaming, MakeSource}
import au.id.tmm.ausvotes.data_sources.nswec.raw.FetchRawLegCoPreferences
import au.id.tmm.ausvotes.data_sources.nswec.resources.LegCoPreferencesResource
import au.id.tmm.ausvotes.model.nsw.legco.NswLegCoElection
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.effects.Sync
import org.apache.commons.lang3.StringUtils

final class FetchRawNswecDataImpl[F[+_, +_] : Sync] private()(
  implicit
  makeSourceForLegCoPreferencesResource: MakeSource[F, Exception, LegCoPreferencesResource],
) extends FetchRawLegCoPreferences[F] {

  private def noneIfBlank(string: String): Option[String] = if (StringUtils.isBlank(string)) None else Some(string)

  override def legCoPreferencesFor(election: NswLegCoElection): F[FetchRawLegCoPreferences.Error, fs2.Stream[F[Throwable, +?], FetchRawLegCoPreferences.Row]] =
    BME.pure {
      CsvStreaming.from[F[Throwable, +?]](makeSourceForLegCoPreferencesResource.makeSourceFor(LegCoPreferencesResource(election)))
        .drop(1)
        .map { row =>
          FetchRawLegCoPreferences.Row(
            row(0).toInt,
            row(1),
            row(2),
            row(3),
            row(4),
            parsePossibleString(row(5)),
            parsePossibleInt(row(6)),
            noneIfBlank(row(7)),
            noneIfBlank(row(8)),
            parsePossibleInt(row(9)),
            row(10) == "Formal",
            parsePossibleString(row(11)),
          )
        }
    }

}

object FetchRawNswecDataImpl {
  def apply[F[+_, +_] : Sync](implicit makeSourceForLegCoPreferencesResource: MakeSource[F, Exception, LegCoPreferencesResource]) = new FetchRawNswecDataImpl[F]()
}

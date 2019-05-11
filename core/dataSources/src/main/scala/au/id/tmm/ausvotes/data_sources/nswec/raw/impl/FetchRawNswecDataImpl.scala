package au.id.tmm.ausvotes.data_sources.nswec.raw.impl

import au.id.tmm.ausvotes.data_sources.common.Fs2Interop._
import au.id.tmm.ausvotes.data_sources.common.{CsvStreaming, MakeSource}
import au.id.tmm.ausvotes.data_sources.nswec.raw.FetchRawLegCoPreferences
import au.id.tmm.ausvotes.data_sources.nswec.resources.LegCoPreferencesResource
import au.id.tmm.ausvotes.model.nsw.legco.NswLegCoElection
import au.id.tmm.ausvotes.shared.io.typeclasses.{SyncEffects, BifunctorMonadError => BME}
import org.apache.commons.lang3.StringUtils

final class FetchRawNswecDataImpl[F[+_, +_] : SyncEffects] private()(
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
            row(4).toInt,
            row(5),
            row(6).toInt,
            noneIfBlank(row(7)),
            noneIfBlank(row(8)),
            row(9).toInt,
            row(10) == "Formal",
            row(11),
          )
        }
    }

}

object FetchRawNswecDataImpl {
  def apply[F[+_, +_] : SyncEffects](implicit makeSourceForLegCoPreferencesResource: MakeSource[F, Exception, LegCoPreferencesResource]) = new FetchRawNswecDataImpl[F]()
}

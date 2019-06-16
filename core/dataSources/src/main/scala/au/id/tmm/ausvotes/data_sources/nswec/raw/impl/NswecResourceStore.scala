package au.id.tmm.ausvotes.data_sources.nswec.raw.impl

import java.net.URL
import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.common.UrlUtils.StringOps
import au.id.tmm.ausvotes.data_sources.common.streaming.MakeSource
import au.id.tmm.ausvotes.data_sources.nswec.resources.LegCoPreferencesResource
import au.id.tmm.ausvotes.model.nsw.NswElection
import au.id.tmm.ausvotes.model.nsw.legco.NswLegCoElection
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops

final class NswecResourceStore[F[+_, +_] : Sync] private (resourceStoreLocation: Path, replaceExisting: Boolean) {

  implicit val makeSourceForLegCoPreferencesResource: MakeSource[F, Exception, LegCoPreferencesResource] =
    MakeSource.withZipUrlFrom[F, LegCoPreferencesResource](resourceStoreLocation, replaceExisting) {
      case LegCoPreferencesResource(NswLegCoElection(NswElection.`2019`)) =>
        for {
          url <- Sync.fromEither("https://vtrprodragrsstorage01-secondary.blob.core.windows.net/vtrdata-sg1901/lc/SGE2019%20LC%20Pref%20Data%20Statewide.zip?st=2019-03-01T01%3A00%3A00Z&se=2020-03-01T01%3A00%3A00Z&sp=r&sv=2018-03-28&sr=c&sig=KPBiRIYtRCT3aWxdLhdcPWb3qbC3wHubyftHBwIjg2Q%3D".parseUrl): F[Exception, URL]
          zipEntryName = "SGE2019 LC Pref Data_NA_State.txt"
        } yield (url, zipEntryName)

      case election => Sync.leftPure(new RuntimeException(s"Cannot download resource for $election"))
    }

}

object NswecResourceStore {
  def apply[F[+_, +_] : Sync](resourceStoreLocation: Path, replaceExisting: Boolean): NswecResourceStore[F] =
    new NswecResourceStore(resourceStoreLocation, replaceExisting)
}

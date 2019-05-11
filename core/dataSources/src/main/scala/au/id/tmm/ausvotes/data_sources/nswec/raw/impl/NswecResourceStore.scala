package au.id.tmm.ausvotes.data_sources.nswec.raw.impl

import java.net.URL
import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.common.MakeSource.FromZipFile
import au.id.tmm.ausvotes.data_sources.common.UrlUtils.StringOps
import au.id.tmm.ausvotes.data_sources.common.{DownloadToPath, MakeSource}
import au.id.tmm.ausvotes.data_sources.nswec.resources.LegCoPreferencesResource
import au.id.tmm.ausvotes.model.nsw.NswElection
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError._
import au.id.tmm.ausvotes.shared.io.typeclasses.{SyncEffects, BifunctorMonadError => BME}

final class NswecResourceStore[F[+_, +_] : DownloadToPath : SyncEffects] private (resourceStoreLocation: Path) {

  implicit val makeSourceForLegCoPreferencesResource: MakeSource[F, Exception, LegCoPreferencesResource] =
    MakeSource.fromDownloadedZipFile(pathForDownloads = resourceStoreLocation.resolve("nsw")).butFirst { resource =>
      resource.election.stateElection match {
        case NswElection.`2019` =>
          for {
            url <- BME.fromEither("https://vtrprodragrsstorage01-secondary.blob.core.windows.net/vtrdata-sg1901/lc/SGE2019%20LC%20Pref%20Data%20Statewide.zip?st=2019-03-01T01%3A00%3A00Z&se=2020-03-01T01%3A00%3A00Z&sp=r&sv=2018-03-28&sr=c&sig=KPBiRIYtRCT3aWxdLhdcPWb3qbC3wHubyftHBwIjg2Q%3D".parseUrl): F[Exception, URL]
          } yield (url, FromZipFile.ZipEntryName("SGE2019 LC Pref Data_NA_State.txt"))
        case NswElection.`2015` | NswElection.`2011` => BME.leftPure(new RuntimeException(s"Cannot download resource for $resource"))
      }
    }

}

object NswecResourceStore {
  def apply[F[+_, +_] : DownloadToPath : SyncEffects](resourceStoreLocation: Path): NswecResourceStore[F] = new NswecResourceStore(resourceStoreLocation)
}

package au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl

import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.aec.federal.resources.{FederalPollingPlacesResource, FormalSenatePreferencesResource, SenateDistributionOfPreferencesResource, SenateFirstPreferencesResource}
import au.id.tmm.ausvotes.data_sources.common.UrlUtils.StringOps
import au.id.tmm.ausvotes.data_sources.common.{DownloadToPath, MakeSource}
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError, SyncEffects}

final class AecResourceStore[F[+_, +_] : DownloadToPath : SyncEffects] private (resourceStoreLocation: Path) {

  implicit val makeSourceForFederalPollingPlaceResource: MakeSource[F, Exception, FederalPollingPlacesResource] =
    MakeSource.fromDownloadedFile(pathForDownloads = resourceStoreLocation).butFirst {
      case FederalPollingPlacesResource(election) =>
        BifunctorMonadError.fromEither {
          s"https://results.aec.gov.au/${election.aecId.asInt}/Website/Downloads/GeneralPollingPlacesDownload-${election.aecId.asInt}.csv".parseUrl
        }
    }

  implicit val makeSourceForSenateFirstPreferencesResource: MakeSource[F, Exception, SenateFirstPreferencesResource] =
    MakeSource.fromDownloadedFile(pathForDownloads = resourceStoreLocation).butFirst {
      case SenateFirstPreferencesResource(election) =>
        BifunctorMonadError.fromEither {
          s"https://results.aec.gov.au/${election.federalElection.aecId.asInt}/Website/Downloads/SenateFirstPrefsByStateByVoteTypeDownload-${election.federalElection.aecId.asInt}.csv".parseUrl
        }
    }

  implicit val makeSourceForSenateDistributionOfPreferencesResource: MakeSource[F, Exception, SenateDistributionOfPreferencesResource] =
    MakeSource.fromDownloadedZipFile(pathForDownloads = resourceStoreLocation).butFirst {
      case SenateDistributionOfPreferencesResource(SenateElectionForState(election, state)) =>
        for {
          url <- BifunctorMonadError.fromEither(s"https://results.aec.gov.au/${election.federalElection.aecId.asInt}/Website/External/SenateDopDownload-${election.federalElection.aecId.asInt}.zip".parseUrl)

          zipEntryName = MakeSource.FromZipFile.ZipEntryName(s"SenateStateDOPDownload-${election.federalElection.aecId.asInt}-${state.abbreviation.toUpperCase}.csv")
        } yield (url, zipEntryName)
    }

  implicit val makeSourceForFormalSenatePreferencesResource: MakeSource[F, Exception, FormalSenatePreferencesResource] =
    MakeSource.fromDownloadedZipFile(pathForDownloads = resourceStoreLocation).butFirst {
      case FormalSenatePreferencesResource(SenateElectionForState(election, state)) =>
        for {
          url <- BifunctorMonadError.fromEither(s"https://results.aec.gov.au/${election.federalElection.aecId.asInt}/Website/External/aec-senate-formalpreferences-${election.federalElection.aecId.asInt}-${state.abbreviation}.zip".parseUrl)

          zipEntryName = MakeSource.FromZipFile.ZipEntryName(s"aec-senate-formalpreferences-${election.federalElection.aecId.asInt}-${state.abbreviation}.csv")
        } yield (url, zipEntryName)
    }

}

object AecResourceStore {

  def apply[F[+_, +_] : DownloadToPath : SyncEffects](resourceStoreLocation: Path): AecResourceStore[F] =
    new AecResourceStore(resourceStoreLocation)

}

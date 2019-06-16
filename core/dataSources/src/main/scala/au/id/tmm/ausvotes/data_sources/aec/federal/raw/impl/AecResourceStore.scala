package au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl

import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.aec.federal.resources.{FederalPollingPlacesResource, FormalSenatePreferencesResource, SenateDistributionOfPreferencesResource, SenateFirstPreferencesResource}
import au.id.tmm.ausvotes.data_sources.common.UrlUtils.StringOps
import au.id.tmm.ausvotes.data_sources.common.streaming.MakeSource
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME._
import au.id.tmm.bfect.effects.Sync

final class AecResourceStore[F[+_, +_] : Sync] private (resourceStoreLocation: Path, replaceExisting: Boolean) {

  val makeSourceForFederalPollingPlaceResource: MakeSource[F, Exception, FederalPollingPlacesResource] =
    MakeSource.withUrlFrom[F, FederalPollingPlacesResource](resourceStoreLocation, replaceExisting) {
      case FederalPollingPlacesResource(election) =>
        BME.fromEither {
          s"https://results.aec.gov.au/${election.aecId.asInt}/Website/Downloads/GeneralPollingPlacesDownload-${election.aecId.asInt}.csv".parseUrl
        }
    }

  val makeSourceForSenateFirstPreferencesResource: MakeSource[F, Exception, SenateFirstPreferencesResource] =
    MakeSource.withUrlFrom[F, SenateFirstPreferencesResource](resourceStoreLocation, replaceExisting) {
      case SenateFirstPreferencesResource(election) =>
        BME.fromEither {
          s"https://results.aec.gov.au/${election.federalElection.aecId.asInt}/Website/Downloads/SenateFirstPrefsByStateByVoteTypeDownload-${election.federalElection.aecId.asInt}.csv".parseUrl
        }
    }

  val makeSourceForSenateDistributionOfPreferencesResource: MakeSource[F, Exception, SenateDistributionOfPreferencesResource] =
    MakeSource.withZipUrlFrom[F, SenateDistributionOfPreferencesResource](resourceStoreLocation, replaceExisting) {
      case SenateDistributionOfPreferencesResource(SenateElectionForState(election, state)) =>
        for {
          url <- BME.fromEither(s"https://results.aec.gov.au/${election.federalElection.aecId.asInt}/Website/External/SenateDopDownload-${election.federalElection.aecId.asInt}.zip".parseUrl)
          zipEntryName = s"SenateStateDOPDownload-${election.federalElection.aecId.asInt}-${state.abbreviation.toUpperCase}.csv"
        } yield (url, zipEntryName)
    }

  val makeSourceForFormalSenatePreferencesResource: MakeSource[F, Exception, FormalSenatePreferencesResource] =
    MakeSource.withZipUrlFrom[F, FormalSenatePreferencesResource](resourceStoreLocation, replaceExisting) {
      case FormalSenatePreferencesResource(SenateElectionForState(election, state)) =>
        for {
          url <- BME.fromEither(s"https://results.aec.gov.au/${election.federalElection.aecId.asInt}/Website/External/aec-senate-formalpreferences-${election.federalElection.aecId.asInt}-${state.abbreviation}.zip".parseUrl)
          zipEntryName = s"aec-senate-formalpreferences-${election.federalElection.aecId.asInt}-${state.abbreviation}.csv"
        } yield (url, zipEntryName)
    }

}

object AecResourceStore {

  def apply[F[+_, +_] : Sync](resourceStoreLocation: Path, replaceExisting: Boolean): AecResourceStore[F] =
    new AecResourceStore(resourceStoreLocation, replaceExisting)

}

package au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl

import au.id.tmm.ausvotes.data_sources.aec.federal.raw.{FetchRawFederalPollingPlaces, FetchRawFormalSenatePreferences, FetchRawSenateDistributionOfPreferences, FetchRawSenateFirstPreferences}
import au.id.tmm.ausvotes.data_sources.aec.federal.resources.{FederalPollingPlacesResource, FormalSenatePreferencesResource, SenateDistributionOfPreferencesResource, SenateFirstPreferencesResource}
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop._
import au.id.tmm.ausvotes.data_sources.common.{CsvStreaming, MakeSource}
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.ausvotes.shared.io.typeclasses.{SyncEffects, BifunctorMonadError => BME}
import fs2.Stream

final class FetchRawFederalElectionData[F[+_, +_] : SyncEffects] private ()(
  implicit
  makeFederalPollingPlacesResourceSource: MakeSource[F, Exception, FederalPollingPlacesResource],
  makeFormalSenatePreferencesResourceSource: MakeSource[F, Exception, FormalSenatePreferencesResource],
  makeSenateDistributionOfPreferencesResourceSource: MakeSource[F, Exception, SenateDistributionOfPreferencesResource],
  makeSenateFirstPreferencesResourceSource: MakeSource[F, Exception, SenateFirstPreferencesResource],
)
  extends FetchRawSenateFirstPreferences[F]
    with FetchRawSenateDistributionOfPreferences[F]
    with FetchRawFederalPollingPlaces[F]
    with FetchRawFormalSenatePreferences[F] {

  private def fetchStreamFor[R](resource: R)(implicit makeSource: MakeSource[F, Exception, R]): Stream[F[Throwable, +?], List[String]] =
    CsvStreaming.from[F[Throwable, +?]](makeSource.makeSourceFor(resource))

  override def senateFirstPreferencesFor(
                                          election: SenateElection,
                                        ): F[FetchRawSenateFirstPreferences.Error, Stream[F[Throwable, +?], FetchRawSenateFirstPreferences.Row]] =
    BME.pure {
      fetchStreamFor(SenateFirstPreferencesResource(election))
        .drop(2)
        .map { row =>
          FetchRawSenateFirstPreferences.Row(
            state = row(0),
            ticket = row(1),
            candidateId = row(2),
            positionInGroup = row(3).toInt,
            candidateDetails = row(4),
            party = row(5),
            ordinaryVotes = row(6).toInt,
            absentVotes = row(7).toInt,
            provisionalVotes = row(8).toInt,
            prePollVotes = row(9).toInt,
            postalVotes = row(10).toInt,
            totalVotes = row(10).toInt,
          )
        }
    }

  override def federalPollingPlacesForElection(
                                                election: FederalElection,
                                              ): F[FetchRawFederalPollingPlaces.Error, Stream[F[Throwable, +?], FetchRawFederalPollingPlaces.Row]] =
    BME.pure {
      fetchStreamFor(FederalPollingPlacesResource(election))
        .drop(2)
        .map { row =>
          FetchRawFederalPollingPlaces.Row(
            state = row(0),
            divisionId = row(1).toInt,
            divisionName = row(2),
            pollingPlaceId = row(3).toInt,
            pollingPlaceTypeId = row(4).toInt,
            pollingPlaceName = row(5),
            premisesName = row(6),
            premisesAddress1 = row(7),
            premisesAddress2 = row(8),
            premisesAddress3 = row(9),
            premisesSuburb = row(10),
            premisesState = row(11),
            premisesPostcode = row(12),
            latitude = parsePossibleDouble(row(13)),
            longitude = parsePossibleDouble(row(14)),
          )
        }
    }

  override def senateDistributionOfPreferencesFor(
                                                   election: SenateElectionForState,
                                                 ): F[FetchRawSenateDistributionOfPreferences.Error, Stream[F[Throwable, +?], FetchRawSenateDistributionOfPreferences.Row]] =
    BME.pure {
      fetchStreamFor(SenateDistributionOfPreferencesResource(election))
        .drop(1)
        .map { row =>
          FetchRawSenateDistributionOfPreferences.Row(
            state = row(0),
            numberOfVacancies = row(1).toInt,
            totalFormalPapers = row(2).toInt,
            quota = row(3).toInt,
            count = row(4).toInt,
            ballotPosition = row(5).toInt,
            ticket = row(6),
            surname = row(7),
            givenName = row(8),
            papers = row(9).toInt,
            votesTransferred = row(10).toInt,
            progressiveVoteTotal = row(11).toInt,
            transferValue = row(12).toDouble,
            status = row(13),
            changed = parsePossibleBoolean(row(14)),
            orderElected = row(15).toInt,
            comment = row(16),
          )
        }
    }

  override def formalSenatePreferencesFor(
                                           election: SenateElectionForState,
                                         ): F[FetchRawFormalSenatePreferences.Error, Stream[F[Throwable, +?], FetchRawFormalSenatePreferences.Row]] =
    BME.pure {
      fetchStreamFor(FormalSenatePreferencesResource(election))
        .drop(2)
        .map { row =>
          FetchRawFormalSenatePreferences.Row(
            electorateName = row(0),
            voteCollectionPointName = row(1),
            voteCollectionPointId = row(2).toInt,
            batchNumber = row(3).toInt,
            paperNumber = row(4).toInt,
            preferences = row(5),
          )
        }
    }

  private def parsePossibleDouble(string: String): Option[Double] = {
    if (string.isEmpty) {
      None
    } else {
      Some(string.toDouble)
    }
  }

  private def parsePossibleBoolean(string: String): Option[Boolean] = {
    if (string.isEmpty) {
      None
    } else {
      Some(string.toBoolean)
    }
  }
}

object FetchRawFederalElectionData {
  def apply[F[+_, +_] : SyncEffects]()(
    implicit
    makeFederalPollingPlacesResourceSource: MakeSource[F, Exception, FederalPollingPlacesResource],
    makeFormalSenatePreferencesResourceSource: MakeSource[F, Exception, FormalSenatePreferencesResource],
    makeSenateDistributionOfPreferencesResourceSource: MakeSource[F, Exception, SenateDistributionOfPreferencesResource],
    makeSenateFirstPreferencesResourceSource: MakeSource[F, Exception, SenateFirstPreferencesResource],
  ): FetchRawFederalElectionData[F] =
    new FetchRawFederalElectionData()
}

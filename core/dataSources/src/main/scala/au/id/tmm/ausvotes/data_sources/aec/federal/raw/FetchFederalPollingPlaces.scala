package au.id.tmm.ausvotes.data_sources.aec.federal.raw

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.FederalElection
import fs2.Stream

trait FetchFederalPollingPlaces[F[+_, +_]] {

  def federalPollingPlacesForElection(election: FederalElection): F[FetchFederalPollingPlaces.Error, Stream[F[Throwable, +?], FetchFederalPollingPlaces.Row]]

}

object FetchFederalPollingPlaces {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  final case class Row(
                        state: String,
                        divisionId: Int,
                        divisionName: String,
                        pollingPlaceId: Int,
                        pollingPlaceTypeId: Int,
                        pollingPlaceName: String,
                        premisesName: String,
                        premisesAddress1: String,
                        premisesAddress2: String,
                        premisesAddress3: String,
                        premisesSuburb: String,
                        premisesState: String,
                        premisesPostcode: String,
                        latitude: Option[Double],
                        longitude: Option[Double],
                      )

}

package au.id.tmm.ausvotes.data_sources.aec.federal.parsed

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.{DivisionsAndPollingPlaces, FederalElection}

trait FetchDivisionsAndFederalPollingPlaces[F[+_, +_]] {

  def divisionsAndFederalPollingPlacesFor(election: FederalElection): F[FetchDivisionsAndFederalPollingPlaces.Error, DivisionsAndPollingPlaces]

}

object FetchDivisionsAndFederalPollingPlaces {

  def divisionsAndFederalPollingPlacesFor[F[+_, +_] : FetchDivisionsAndFederalPollingPlaces](election: FederalElection): F[FetchDivisionsAndFederalPollingPlaces.Error, DivisionsAndPollingPlaces] =
    implicitly[FetchDivisionsAndFederalPollingPlaces[F]].divisionsAndFederalPollingPlacesFor(election)

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

}

package au.id.tmm.ausvotes.data_sources.aec.federal.parsed

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.{DivisionsAndPollingPlaces, FederalElection}

trait FetchDivisionsAndFederalPollingPlaces[F[+_, +_]] {

  def divisionsAndFederalPollingPlacesFor(election: FederalElection): F[FetchDivisionsAndFederalPollingPlaces.Error, DivisionsAndPollingPlaces]

}

object FetchDivisionsAndFederalPollingPlaces {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

}

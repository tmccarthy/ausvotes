package au.id.tmm.ausvotes.core.io_actions

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.{DivisionsAndPollingPlaces, FederalElection}

trait FetchDivisionsAndPollingPlaces[F[+_, +_]] {

  def fetchDivisionsAndPollingPlacesFor(federalElection: FederalElection): F[FetchDivisionsAndPollingPlaces.Error, DivisionsAndPollingPlaces]

}

object FetchDivisionsAndPollingPlaces {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  def fetchFor[F[+_, +_] : FetchDivisionsAndPollingPlaces](federalElection: FederalElection): F[FetchDivisionsAndPollingPlaces.Error, DivisionsAndPollingPlaces] =
    implicitly[FetchDivisionsAndPollingPlaces[F]].fetchDivisionsAndPollingPlacesFor(federalElection)

}

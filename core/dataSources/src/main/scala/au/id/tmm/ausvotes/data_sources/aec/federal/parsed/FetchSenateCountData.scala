package au.id.tmm.ausvotes.data_sources.aec.federal.parsed

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.{SenateCountData, SenateElectionForState}

trait FetchSenateCountData[F[+_, +_]] {

  def senateCountDataFor(election: SenateElectionForState): F[FetchSenateCountData.Error, SenateCountData]

}

object FetchSenateCountData {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

}

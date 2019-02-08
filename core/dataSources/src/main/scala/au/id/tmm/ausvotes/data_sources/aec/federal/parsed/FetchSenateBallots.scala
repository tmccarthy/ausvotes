package au.id.tmm.ausvotes.data_sources.aec.federal.parsed

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallot, SenateElectionForState}
import fs2.Stream

trait FetchSenateBallots[F[+_, +_]] {

  def senateBallotsFor(election: SenateElectionForState): F[FetchSenateBallots.Error, Stream[F[Throwable, +?], SenateBallot]]

}

object FetchSenateBallots {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

}

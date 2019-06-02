package au.id.tmm.ausvotes.data_sources.nswec.parsed

import au.id.tmm.ausvotes.model.nsw.legco._
import fs2.Stream

trait FetchLegCoBallots[F[+_, +_]] {

  def legCoBallotsFor(
                     election: NswLegCoElection,
                     ): F[Exception, Stream[F[Throwable, +?], Ballot]]

}

object FetchLegCoBallots {
  def apply[F[+_, +_] : FetchLegCoBallots]: FetchLegCoBallots[F] = implicitly[FetchLegCoBallots[F]]
}

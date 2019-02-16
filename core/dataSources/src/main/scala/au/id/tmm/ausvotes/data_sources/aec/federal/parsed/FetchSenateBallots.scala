package au.id.tmm.ausvotes.data_sources.aec.federal.parsed

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallot, SenateElectionForState, SenateGroupsAndCandidates}
import fs2.Stream

trait FetchSenateBallots[F[+_, +_]] {

  def senateBallotsFor(
                        election: SenateElectionForState,
                        allGroupsAndCandidates: SenateGroupsAndCandidates,
                        divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                      ): F[FetchSenateBallots.Error, Stream[F[Throwable, +?], SenateBallot]]

}

object FetchSenateBallots {

  def senateBallotsFor[F[+_, +_] : FetchSenateBallots](
                                                        election: SenateElectionForState,
                                                        allGroupsAndCandidates: SenateGroupsAndCandidates,
                                                        divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                                      ): F[FetchSenateBallots.Error, Stream[F[Throwable, +?], SenateBallot]] =
    implicitly[FetchSenateBallots[F]].senateBallotsFor(election, allGroupsAndCandidates, divisionsAndPollingPlaces)

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

}

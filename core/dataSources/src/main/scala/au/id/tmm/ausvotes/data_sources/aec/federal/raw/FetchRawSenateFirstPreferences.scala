package au.id.tmm.ausvotes.data_sources.aec.federal.raw

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import fs2.Stream

trait FetchRawSenateFirstPreferences[F[+_, +_]] {

  def senateFirstPreferencesFor(election: SenateElection): F[FetchRawSenateFirstPreferences.Error, Stream[F[Throwable, +?], FetchRawSenateFirstPreferences.Row]]

}

object FetchRawSenateFirstPreferences {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  final case class Row(
                        state: String,
                        ticket: String,
                        candidateId: String,
                        positionInGroup: Int,
                        candidateDetails: String,
                        party: String,
                        ordinaryVotes: Int,
                        absentVotes: Int,
                        provisionalVotes: Int,
                        prePollVotes: Int,
                        postalVotes: Int,
                        totalVotes: Int,
                      )

}

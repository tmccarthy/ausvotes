package au.id.tmm.ausvotes.data_sources.aec.federal.raw

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import fs2.Stream

trait FetchRawSenateDistributionOfPreferences[F[+_, +_]] {

  def senateDistributionOfPreferencesFor(election: SenateElectionForState): F[FetchRawSenateDistributionOfPreferences.Error, Stream[F[Throwable, +?], FetchRawSenateDistributionOfPreferences.Row]]

}

object FetchRawSenateDistributionOfPreferences {

  def senateDistributionOfPreferencesFor[F[+_, +_] : FetchRawSenateDistributionOfPreferences](election: SenateElectionForState): F[FetchRawSenateDistributionOfPreferences.Error, Stream[F[Throwable, +?], FetchRawSenateDistributionOfPreferences.Row]] =
    implicitly[FetchRawSenateDistributionOfPreferences[F]].senateDistributionOfPreferencesFor(election)

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  final case class Row(
                        state: String,
                        numberOfVacancies: Int,
                        totalFormalPapers: Int,
                        quota: Int,
                        count: Int,
                        ballotPosition: Int,
                        ticket: String,
                        surname: String,
                        givenName: String,
                        papers: Int,
                        votesTransferred: Int,
                        progressiveVoteTotal: Int,
                        transferValue: Double,
                        status: String,
                        changed: Option[Boolean],
                        orderElected: Int,
                        comment: String,
                      )

}

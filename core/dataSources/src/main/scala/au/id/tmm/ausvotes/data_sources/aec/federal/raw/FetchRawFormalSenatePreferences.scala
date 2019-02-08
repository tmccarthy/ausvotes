package au.id.tmm.ausvotes.data_sources.aec.federal.raw

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import fs2.Stream

trait FetchRawFormalSenatePreferences[F[+_, +_]] {

  def formalSenatePreferencesFor(election: SenateElectionForState): F[FetchRawFormalSenatePreferences.Error, Stream[F[Throwable, ?], FetchRawFormalSenatePreferences.Row]]

}

object FetchRawFormalSenatePreferences {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  final case class Row(
                        electorateName: String,
                        voteCollectionPointName: String,
                        voteCollectionPointId: Int,
                        batchNumber: Int,
                        paperNumber: Int,
                        preferences: String,
                      )

}

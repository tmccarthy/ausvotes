package au.id.tmm.ausvotes.data_sources.aec.federal.raw

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import fs2.Stream

trait FetchFormalSenatePreferences[F[+_, +_]] {

  def formalSenatePreferencesFor(election: SenateElectionForState): F[FetchFormalSenatePreferences.Error, Stream[F[Throwable, ?], FetchFormalSenatePreferences.Row]]

}

object FetchFormalSenatePreferences {

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

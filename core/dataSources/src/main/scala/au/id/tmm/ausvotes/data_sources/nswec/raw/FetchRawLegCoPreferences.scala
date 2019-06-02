package au.id.tmm.ausvotes.data_sources.nswec.raw

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.nsw.legco.NswLegCoElection
import fs2.Stream

trait FetchRawLegCoPreferences[F[+_, +_]] {

  def legCoPreferencesFor(election: NswLegCoElection): F[FetchRawLegCoPreferences.Error, Stream[F[Throwable, +?], FetchRawLegCoPreferences.Row]]

}

object FetchRawLegCoPreferences {

  def apply[F[+_, +_] : FetchRawLegCoPreferences]: FetchRawLegCoPreferences[F] = implicitly[FetchRawLegCoPreferences[F]]

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  final case class Row(
                        sequenceNumber: Int,
                        districtName: String,
                        voteTypeName: String,
                        venueName: String,
                        ballotPaperID: Int,
                        preferenceMark: String,
                        preferenceNumber: Int,
                        candidateName: Option[String],
                        groupCode: Option[String],
                        drawOrder: Int,
                        formal: Boolean,
                        typeName: String,
                      )
}

package au.id.tmm.ausvotes.core.io_actions

import au.id.tmm.ausvotes.core.tallies._
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import io.circe.{Decoder, Encoder}

trait FetchTally[F[+_, +_]] {

  def fetchTally0(election: SenateElection, tallier: Tallier0): F[FetchTally.Error, Tally0]

  def fetchTally1[T_GROUP_1 : Encoder : Decoder](election: SenateElection, tallier: Tallier1[T_GROUP_1]): F[FetchTally.Error, Tally1[T_GROUP_1]]

  def fetchTally2[T_GROUP_1 : Encoder : Decoder, T_GROUP_2 : Encoder : Decoder](election: SenateElection, tallier: Tallier2[T_GROUP_1, T_GROUP_2]): F[FetchTally.Error, Tally2[T_GROUP_1, T_GROUP_2]]

  def fetchTally3[T_GROUP_1 : Encoder : Decoder, T_GROUP_2 : Encoder : Decoder, T_GROUP_3 : Encoder : Decoder](election: SenateElection, tallier: Tallier3[T_GROUP_1, T_GROUP_2, T_GROUP_3]): F[FetchTally.Error, Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]]

}

object FetchTally {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

}

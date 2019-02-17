package au.id.tmm.ausvotes.core.tallying

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

  def fetchTally0[F[+_, +_] : FetchTally](election: SenateElection, tallier: Tallier0): F[FetchTally.Error, Tally0] =
    implicitly[FetchTally[F]].fetchTally0(election, tallier)

  def fetchTally1[F[+_, +_] : FetchTally, T_GROUP_1 : Encoder : Decoder](election: SenateElection, tallier: Tallier1[T_GROUP_1]): F[FetchTally.Error, Tally1[T_GROUP_1]] =
    implicitly[FetchTally[F]].fetchTally1(election, tallier)

  def fetchTally2[F[+_, +_] : FetchTally, T_GROUP_1 : Encoder : Decoder, T_GROUP_2 : Encoder : Decoder](election: SenateElection, tallier: Tallier2[T_GROUP_1, T_GROUP_2]): F[FetchTally.Error, Tally2[T_GROUP_1, T_GROUP_2]] =
    implicitly[FetchTally[F]].fetchTally2(election, tallier)

  def fetchTally3[F[+_, +_] : FetchTally, T_GROUP_1 : Encoder : Decoder, T_GROUP_2 : Encoder : Decoder, T_GROUP_3 : Encoder : Decoder](election: SenateElection, tallier: Tallier3[T_GROUP_1, T_GROUP_2, T_GROUP_3]): F[FetchTally.Error, Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]] =
    implicitly[FetchTally[F]].fetchTally3(election, tallier)

}

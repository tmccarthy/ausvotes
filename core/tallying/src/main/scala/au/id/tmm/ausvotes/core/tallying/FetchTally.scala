package au.id.tmm.ausvotes.core.tallying

import au.id.tmm.ausvotes.core.tallies.Tallier
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import cats.Monoid

trait FetchTally[F[+_, +_]] {

  def fetchTally1[B, A1 : cats.Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: Tallier[B, A1]): F[FetchTally.Error, A1]

  def fetchTally2[B, A1 : Monoid, A2 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: Tallier[B, A1], tallier2: Tallier[B, A2]): F[FetchTally.Error, (A1, A2)]

  def fetchTally3[B, A1 : Monoid, A2 : Monoid, A3 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: Tallier[B, A1], tallier2: Tallier[B, A2], tallier3: Tallier[B, A3]): F[FetchTally.Error, (A1, A2, A3)]

  def fetchTally4[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: Tallier[B, A1], tallier2: Tallier[B, A2], tallier3: Tallier[B, A3], tallier4: Tallier[B, A4]): F[FetchTally.Error, (A1, A2, A3, A4)]

  def fetchTally5[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: Tallier[B, A1], tallier2: Tallier[B, A2], tallier3: Tallier[B, A3], tallier4: Tallier[B, A4], tallier5: Tallier[B, A5]): F[FetchTally.Error, (A1, A2, A3, A4, A5)]

  def fetchTally6[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: Tallier[B, A1], tallier2: Tallier[B, A2], tallier3: Tallier[B, A3], tallier4: Tallier[B, A4], tallier5: Tallier[B, A5], tallier6: Tallier[B, A6]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6)]

  def fetchTally7[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: Tallier[B, A1], tallier2: Tallier[B, A2], tallier3: Tallier[B, A3], tallier4: Tallier[B, A4], tallier5: Tallier[B, A5], tallier6: Tallier[B, A6], tallier7: Tallier[B, A7]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7)]

  def fetchTally8[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: Tallier[B, A1], tallier2: Tallier[B, A2], tallier3: Tallier[B, A3], tallier4: Tallier[B, A4], tallier5: Tallier[B, A5], tallier6: Tallier[B, A6], tallier7: Tallier[B, A7], tallier8: Tallier[B, A8]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8)]

  def fetchTally9[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid, A9 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: Tallier[B, A1], tallier2: Tallier[B, A2], tallier3: Tallier[B, A3], tallier4: Tallier[B, A4], tallier5: Tallier[B, A5], tallier6: Tallier[B, A6], tallier7: Tallier[B, A7], tallier8: Tallier[B, A8], tallier9: Tallier[B, A9]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9)]

  def fetchTally10[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid, A9 : Monoid, A10 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: Tallier[B, A1], tallier2: Tallier[B, A2], tallier3: Tallier[B, A3], tallier4: Tallier[B, A4], tallier5: Tallier[B, A5], tallier6: Tallier[B, A6], tallier7: Tallier[B, A7], tallier8: Tallier[B, A8], tallier9: Tallier[B, A9], tallier10: Tallier[B, A10]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)]

}

object FetchTally {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  def apply[F[+ _, + _] : FetchTally]: FetchTally[F] = implicitly[FetchTally[F]]

}

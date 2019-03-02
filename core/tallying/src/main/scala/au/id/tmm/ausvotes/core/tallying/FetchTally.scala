package au.id.tmm.ausvotes.core.tallying

import au.id.tmm.ausvotes.core.tallies.typeclasses.Tallier
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import cats.Monoid

trait FetchTally[F[+_, +_]] {

  def fetchTally1[B, T1, A1 : cats.Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1)(implicit tallierInstance: Tallier[T1, B, A1]): F[FetchTally.Error, A1]

  def fetchTally2[B, T1, T2, A1 : Monoid, A2 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2]): F[FetchTally.Error, (A1, A2)]

  def fetchTally3[B, T1, T2, T3, A1 : Monoid, A2 : Monoid, A3 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3]): F[FetchTally.Error, (A1, A2, A3)]

  def fetchTally4[B, T1, T2, T3, T4, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4]): F[FetchTally.Error, (A1, A2, A3, A4)]

  def fetchTally5[B, T1, T2, T3, T4, T5, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5]): F[FetchTally.Error, (A1, A2, A3, A4, A5)]

  def fetchTally6[B, T1, T2, T3, T4, T5, T6, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5, tallier6: T6)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5], t6: Tallier[T6, B, A6]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6)]

  def fetchTally7[B, T1, T2, T3, T4, T5, T6, T7, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5, tallier6: T6, tallier7: T7)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5], t6: Tallier[T6, B, A6], t7: Tallier[T7, B, A7]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7)]

  def fetchTally8[B, T1, T2, T3, T4, T5, T6, T7, T8, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5, tallier6: T6, tallier7: T7, tallier8: T8)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5], t6: Tallier[T6, B, A6], t7: Tallier[T7, B, A7], t8: Tallier[T8, B, A8]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8)]

  def fetchTally9[B, T1, T2, T3, T4, T5, T6, T7, T8, T9, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid, A9 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5, tallier6: T6, tallier7: T7, tallier8: T8, tallier9: T9)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5], t6: Tallier[T6, B, A6], t7: Tallier[T7, B, A7], t8: Tallier[T8, B, A8], t9: Tallier[T9, B, A9]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9)]

  def fetchTally10[B, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid, A9 : Monoid, A10 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5, tallier6: T6, tallier7: T7, tallier8: T8, tallier9: T9, tallier10: T10)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5], t6: Tallier[T6, B, A6], t7: Tallier[T7, B, A7], t8: Tallier[T8, B, A8], t9: Tallier[T9, B, A9], t10: Tallier[T10, B, A10]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)]

}

object FetchTally {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  def apply[F[+ _, + _] : FetchTally]: FetchTally[F] = implicitly[FetchTally[F]]

}

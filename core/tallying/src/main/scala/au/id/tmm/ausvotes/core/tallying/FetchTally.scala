package au.id.tmm.ausvotes.core.tallying

import au.id.tmm.ausvotes.core.tallies.typeclasses.Tallier
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import cats.Monoid

import scala.reflect.runtime.universe.TypeTag

trait FetchTally[F[+_, +_]] {

  def fetchTally1[B, T_TALLIER_1, A_1 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier: T_TALLIER_1)(implicit tallierInstance: Tallier[T_TALLIER_1, B, A_1]): F[FetchTally.Error, A_1]

  def fetchTally2[B, T_TALLIER_1, T_TALLIER_2, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2]): F[FetchTally.Error, (A_1, A_2)]

  def fetchTally3[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3]): F[FetchTally.Error, (A_1, A_2, A_3)]

  def fetchTally4[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4]): F[FetchTally.Error, (A_1, A_2, A_3, A_4)]

  def fetchTally5[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5)]

  def fetchTally6[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, T_TALLIER_6, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag, A_6 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5, tallier6: T_TALLIER_6)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5], t6: Tallier[T_TALLIER_6, B, A_6]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5, A_6)]

  def fetchTally7[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, T_TALLIER_6, T_TALLIER_7, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag, A_6 : Monoid : TypeTag, A_7 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5, tallier6: T_TALLIER_6, tallier7: T_TALLIER_7)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5], t6: Tallier[T_TALLIER_6, B, A_6], t7: Tallier[T_TALLIER_7, B, A_7]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5, A_6, A_7)]

  def fetchTally8[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, T_TALLIER_6, T_TALLIER_7, T_TALLIER_8, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag, A_6 : Monoid : TypeTag, A_7 : Monoid : TypeTag, A_8 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5, tallier6: T_TALLIER_6, tallier7: T_TALLIER_7, tallier8: T_TALLIER_8)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5], t6: Tallier[T_TALLIER_6, B, A_6], t7: Tallier[T_TALLIER_7, B, A_7], t8: Tallier[T_TALLIER_8, B, A_8]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5, A_6, A_7, A_8)]

  def fetchTally9[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, T_TALLIER_6, T_TALLIER_7, T_TALLIER_8, T_TALLIER_9, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag, A_6 : Monoid : TypeTag, A_7 : Monoid : TypeTag, A_8 : Monoid : TypeTag, A_9 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5, tallier6: T_TALLIER_6, tallier7: T_TALLIER_7, tallier8: T_TALLIER_8, tallier9: T_TALLIER_9)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5], t6: Tallier[T_TALLIER_6, B, A_6], t7: Tallier[T_TALLIER_7, B, A_7], t8: Tallier[T_TALLIER_8, B, A_8], t9: Tallier[T_TALLIER_9, B, A_9]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5, A_6, A_7, A_8, A_9)]

  def fetchTally10[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, T_TALLIER_6, T_TALLIER_7, T_TALLIER_8, T_TALLIER_9, T_TALLIER_10, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag, A_6 : Monoid : TypeTag, A_7 : Monoid : TypeTag, A_8 : Monoid : TypeTag, A_9 : Monoid : TypeTag, A_10 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5, tallier6: T_TALLIER_6, tallier7: T_TALLIER_7, tallier8: T_TALLIER_8, tallier9: T_TALLIER_9, tallier10: T_TALLIER_10)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5], t6: Tallier[T_TALLIER_6, B, A_6], t7: Tallier[T_TALLIER_7, B, A_7], t8: Tallier[T_TALLIER_8, B, A_8], t9: Tallier[T_TALLIER_9, B, A_9], t10: Tallier[T_TALLIER_10, B, A_10]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5, A_6, A_7, A_8, A_9, A_10)]

}

object FetchTally {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  def apply[F[+ _, + _] : FetchTally]: FetchTally[F] = implicitly[FetchTally[F]]

}

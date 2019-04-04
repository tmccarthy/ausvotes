package au.id.tmm.ausvotes.core.tallying

import au.id.tmm.ausvotes.core.tallies.redo.BallotTallier
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import cats.Monoid

trait FetchTally[F[+_, +_]] {

  def fetchTally1[B, A1 : cats.Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1]): F[FetchTally.Error, A1]

  def fetchTally2[B, A1 : Monoid, A2 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2]): F[FetchTally.Error, (A1, A2)]

  def fetchTally3[B, A1 : Monoid, A2 : Monoid, A3 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3]): F[FetchTally.Error, (A1, A2, A3)]

  def fetchTally4[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4]): F[FetchTally.Error, (A1, A2, A3, A4)]

  def fetchTally5[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5]): F[FetchTally.Error, (A1, A2, A3, A4, A5)]

  def fetchTally6[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5], tallier6: BallotTallier[B, A6]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6)]

  def fetchTally7[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5], tallier6: BallotTallier[B, A6], tallier7: BallotTallier[B, A7]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7)]

  def fetchTally8[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5], tallier6: BallotTallier[B, A6], tallier7: BallotTallier[B, A7], tallier8: BallotTallier[B, A8]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8)]

  def fetchTally9[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid, A9 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5], tallier6: BallotTallier[B, A6], tallier7: BallotTallier[B, A7], tallier8: BallotTallier[B, A8], tallier9: BallotTallier[B, A9]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9)]

  def fetchTally10[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid, A9 : Monoid, A10 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5], tallier6: BallotTallier[B, A6], tallier7: BallotTallier[B, A7], tallier8: BallotTallier[B, A8], tallier9: BallotTallier[B, A9], tallier10: BallotTallier[B, A10]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)]

}

object FetchTally {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  def apply[F[+ _, + _] : FetchTally]: FetchTally[F] = implicitly[FetchTally[F]]

}

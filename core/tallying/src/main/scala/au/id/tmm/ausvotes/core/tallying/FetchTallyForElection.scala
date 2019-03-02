package au.id.tmm.ausvotes.core.tallying

import au.id.tmm.ausvotes.core.tallies.UnitTallier._
import au.id.tmm.ausvotes.core.tallies.{Tallier, UnitTallier}
import au.id.tmm.ausvotes.core.tallying.FetchTallyForElection.TallyRequest
import au.id.tmm.ausvotes.data_sources.common.JsonCache
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError, Concurrent}
import cats.Monoid
import io.circe.{Decoder, Encoder}

final class FetchTallyForElection[F[+_, +_] : Concurrent : JsonCache : FetchTally, E : Encoder, B] private (ballotsForElection: E => F[Exception, fs2.Stream[F[Throwable, +?], B]]) {

  private type UT = UnitTallier.type

  def fetchTally1[T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder](election: E)(t1: T1): F[FetchTallyForElection.Error, A1] =
    fetchTally10[T1, A1, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit](election)(t1, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier).map {
      case (a1, _, _, _, _, _, _, _, _, _) => a1
    }

  def fetchTally2[T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder, T2 <: Tallier[B, A2] : Encoder, A2 : Monoid : Encoder : Decoder](election: E)(t1: T1, t2: T2): F[FetchTallyForElection.Error, (A1, A2)] =
    fetchTally10[T1, A1, T2, A2, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit](election)(t1, t2, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier).map {
      case (a1, a2, _, _, _, _, _, _, _, _) => (a1, a2)
    }

  def fetchTally3[T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder, T2 <: Tallier[B, A2] : Encoder, A2 : Monoid : Encoder : Decoder, T3 <: Tallier[B, A3] : Encoder, A3 : Monoid : Encoder : Decoder](election: E)(t1: T1, t2: T2, t3: T3): F[FetchTallyForElection.Error, (A1, A2, A3)] =
    fetchTally10[T1, A1, T2, A2, T3, A3, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit](election)(t1, t2, t3, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier).map {
      case (a1, a2, a3, _, _, _, _, _, _, _) => (a1, a2, a3)
    }

  def fetchTally4[T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder, T2 <: Tallier[B, A2] : Encoder, A2 : Monoid : Encoder : Decoder, T3 <: Tallier[B, A3] : Encoder, A3 : Monoid : Encoder : Decoder, T4 <: Tallier[B, A4] : Encoder, A4 : Monoid : Encoder : Decoder](election: E)(t1: T1, t2: T2, t3: T3, t4: T4): F[FetchTallyForElection.Error, (A1, A2, A3, A4)] =
    fetchTally10[T1, A1, T2, A2, T3, A3, T4, A4, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit](election)(t1, t2, t3, t4, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier).map {
      case (a1, a2, a3, a4, _, _, _, _, _, _) => (a1, a2, a3, a4)
    }

  def fetchTally5[T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder, T2 <: Tallier[B, A2] : Encoder, A2 : Monoid : Encoder : Decoder, T3 <: Tallier[B, A3] : Encoder, A3 : Monoid : Encoder : Decoder, T4 <: Tallier[B, A4] : Encoder, A4 : Monoid : Encoder : Decoder, T5 <: Tallier[B, A5] : Encoder, A5 : Monoid : Encoder : Decoder](election: E)(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5)] =
    fetchTally10[T1, A1, T2, A2, T3, A3, T4, A4, T5, A5, UT, Unit, UT, Unit, UT, Unit, UT, Unit, UT, Unit](election)(t1, t2, t3, t4, t5, UnitTallier, UnitTallier, UnitTallier, UnitTallier, UnitTallier).map {
      case (a1, a2, a3, a4, a5, _, _, _, _, _) => (a1, a2, a3, a4, a5)
    }

  def fetchTally6[T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder, T2 <: Tallier[B, A2] : Encoder, A2 : Monoid : Encoder : Decoder, T3 <: Tallier[B, A3] : Encoder, A3 : Monoid : Encoder : Decoder, T4 <: Tallier[B, A4] : Encoder, A4 : Monoid : Encoder : Decoder, T5 <: Tallier[B, A5] : Encoder, A5 : Monoid : Encoder : Decoder, T6 <: Tallier[B, A6] : Encoder, A6 : Monoid : Encoder : Decoder](election: E)(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5, A6)] =
    fetchTally10[T1, A1, T2, A2, T3, A3, T4, A4, T5, A5, T6, A6, UT, Unit, UT, Unit, UT, Unit, UT, Unit](election)(t1, t2, t3, t4, t5, t6, UnitTallier, UnitTallier, UnitTallier, UnitTallier).map {
      case (a1, a2, a3, a4, a5, a6, _, _, _, _) => (a1, a2, a3, a4, a5, a6)
    }

  def fetchTally7[T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder, T2 <: Tallier[B, A2] : Encoder, A2 : Monoid : Encoder : Decoder, T3 <: Tallier[B, A3] : Encoder, A3 : Monoid : Encoder : Decoder, T4 <: Tallier[B, A4] : Encoder, A4 : Monoid : Encoder : Decoder, T5 <: Tallier[B, A5] : Encoder, A5 : Monoid : Encoder : Decoder, T6 <: Tallier[B, A6] : Encoder, A6 : Monoid : Encoder : Decoder, T7 <: Tallier[B, A7] : Encoder, A7 : Monoid : Encoder : Decoder](election: E)(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5, A6, A7)] =
    fetchTally10[T1, A1, T2, A2, T3, A3, T4, A4, T5, A5, T6, A6, T7, A7, UT, Unit, UT, Unit, UT, Unit](election)(t1, t2, t3, t4, t5, t6, t7, UnitTallier, UnitTallier, UnitTallier).map {
      case (a1, a2, a3, a4, a5, a6, a7, _, _, _) => (a1, a2, a3, a4, a5, a6, a7)
    }

  def fetchTally8[T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder, T2 <: Tallier[B, A2] : Encoder, A2 : Monoid : Encoder : Decoder, T3 <: Tallier[B, A3] : Encoder, A3 : Monoid : Encoder : Decoder, T4 <: Tallier[B, A4] : Encoder, A4 : Monoid : Encoder : Decoder, T5 <: Tallier[B, A5] : Encoder, A5 : Monoid : Encoder : Decoder, T6 <: Tallier[B, A6] : Encoder, A6 : Monoid : Encoder : Decoder, T7 <: Tallier[B, A7] : Encoder, A7 : Monoid : Encoder : Decoder, T8 <: Tallier[B, A8] : Encoder, A8 : Monoid : Encoder : Decoder](election: E)(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5, A6, A7, A8)] =
    fetchTally10[T1, A1, T2, A2, T3, A3, T4, A4, T5, A5, T6, A6, T7, A7, T8, A8, UT, Unit, UT, Unit](election)(t1, t2, t3, t4, t5, t6, t7, t8, UnitTallier, UnitTallier).map {
      case (a1, a2, a3, a4, a5, a6, a7, a8, _, _) => (a1, a2, a3, a4, a5, a6, a7, a8)
    }

  def fetchTally9[T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder, T2 <: Tallier[B, A2] : Encoder, A2 : Monoid : Encoder : Decoder, T3 <: Tallier[B, A3] : Encoder, A3 : Monoid : Encoder : Decoder, T4 <: Tallier[B, A4] : Encoder, A4 : Monoid : Encoder : Decoder, T5 <: Tallier[B, A5] : Encoder, A5 : Monoid : Encoder : Decoder, T6 <: Tallier[B, A6] : Encoder, A6 : Monoid : Encoder : Decoder, T7 <: Tallier[B, A7] : Encoder, A7 : Monoid : Encoder : Decoder, T8 <: Tallier[B, A8] : Encoder, A8 : Monoid : Encoder : Decoder, T9 <: Tallier[B, A9] : Encoder, A9 : Monoid : Encoder : Decoder](election: E)(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9)] =
    fetchTally10[T1, A1, T2, A2, T3, A3, T4, A4, T5, A5, T6, A6, T7, A7, T8, A8, T9, A9, UT, Unit](election)(t1, t2, t3, t4, t5, t6, t7, t8, t9, UnitTallier).map {
      case (a1, a2, a3, a4, a5, a6, a7, a8, a9, _) => (a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }

  def fetchTally10[
  T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder,
  T2 <: Tallier[B, A2] : Encoder, A2 : Monoid : Encoder : Decoder,
  T3 <: Tallier[B, A3] : Encoder, A3 : Monoid : Encoder : Decoder,
  T4 <: Tallier[B, A4] : Encoder, A4 : Monoid : Encoder : Decoder,
  T5 <: Tallier[B, A5] : Encoder, A5 : Monoid : Encoder : Decoder,
  T6 <: Tallier[B, A6] : Encoder, A6 : Monoid : Encoder : Decoder,
  T7 <: Tallier[B, A7] : Encoder, A7 : Monoid : Encoder : Decoder,
  T8 <: Tallier[B, A8] : Encoder, A8 : Monoid : Encoder : Decoder,
  T9 <: Tallier[B, A9] : Encoder, A9 : Monoid : Encoder : Decoder,
  T10 <: Tallier[B, A10] : Encoder, A10 : Monoid : Encoder : Decoder,
  ]
  (election: E)(
    t1: T1,
    t2: T2,
    t3: T3,
    t4: T4,
    t5: T5,
    t6: T6,
    t7: T7,
    t8: T8,
    t9: T9,
    t10: T10,
  ): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)] = {
    for {
      cachedAs <- Concurrent.par10(
        fetchTallyFromCache[T1, A1](election, t1),
        fetchTallyFromCache[T2, A2](election, t2),
        fetchTallyFromCache[T3, A3](election, t3),
        fetchTallyFromCache[T4, A4](election, t4),
        fetchTallyFromCache[T5, A5](election, t5),
        fetchTallyFromCache[T6, A6](election, t6),
        fetchTallyFromCache[T7, A7](election, t7),
        fetchTallyFromCache[T8, A8](election, t8),
        fetchTallyFromCache[T9, A9](election, t9),
        fetchTallyFromCache[T10, A10](election, t10),
      )

      allTallies <- cachedAs match {
        case (Some(a1), Some(a2), Some(a3), Some(a4), Some(a5), Some(a6), Some(a7), Some(a8), Some(a9), Some(a10)) => BifunctorMonadError.pure((a1, a2, a3, a4, a5, a6, a7, a8, a9, a10))
        case _ => computeAndCacheTallies[T1, A1, T2, A2, T3, A3, T4, A4, T5, A5, T6, A6, T7, A7, T8, A8, T9, A9, T10, A10](election)(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10)
      }
    } yield allTallies
  }.leftMap(FetchTallyForElection.Error)

  private def fetchTallyFromCache[T : Encoder, A : Decoder](election: E, tallier: T): F[Exception, Option[A]] =
    tallier match {
      case UnitTallier => BifunctorMonadError.pure(Some(().asInstanceOf[A]))
      case tallier => JsonCache.get(TallyRequest(election, tallier))
    }

  private def computeAndCacheTallies[
  T1 <: Tallier[B, A1] : Encoder, A1 : Monoid : Encoder : Decoder,
  T2 <: Tallier[B, A2] : Encoder, A2 : Monoid : Encoder : Decoder,
  T3 <: Tallier[B, A3] : Encoder, A3 : Monoid : Encoder : Decoder,
  T4 <: Tallier[B, A4] : Encoder, A4 : Monoid : Encoder : Decoder,
  T5 <: Tallier[B, A5] : Encoder, A5 : Monoid : Encoder : Decoder,
  T6 <: Tallier[B, A6] : Encoder, A6 : Monoid : Encoder : Decoder,
  T7 <: Tallier[B, A7] : Encoder, A7 : Monoid : Encoder : Decoder,
  T8 <: Tallier[B, A8] : Encoder, A8 : Monoid : Encoder : Decoder,
  T9 <: Tallier[B, A9] : Encoder, A9 : Monoid : Encoder : Decoder,
  T10 <: Tallier[B, A10] : Encoder, A10 : Monoid : Encoder : Decoder,
  ]
  (election: E)(
    t1: T1,
    t2: T2,
    t3: T3,
    t4: T4,
    t5: T5,
    t6: T6,
    t7: T7,
    t8: T8,
    t9: T9,
    t10: T10,
  ): F[Exception, (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)] =
    for {
      ballots <- ballotsForElection(election)

      tallyResults <- implicitly[FetchTally[F]].fetchTally10[B, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10](ballots)(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10)

      _ <- Concurrent.par10(
        JsonCache.put(t1, tallyResults._1),
        JsonCache.put(t2, tallyResults._2),
        JsonCache.put(t3, tallyResults._3),
        JsonCache.put(t4, tallyResults._4),
        JsonCache.put(t5, tallyResults._5),
        JsonCache.put(t6, tallyResults._6),
        JsonCache.put(t7, tallyResults._7),
        JsonCache.put(t8, tallyResults._8),
        JsonCache.put(t9, tallyResults._9),
        JsonCache.put(t10, tallyResults._10),
      )
    } yield tallyResults

}

object FetchTallyForElection {

  def apply[F[+_, +_] : Concurrent : JsonCache : FetchTally, E : Encoder, B](ballotsForElection: E => F[Exception, fs2.Stream[F[Throwable, +?], B]]): FetchTallyForElection[F, E, B] =
    new FetchTallyForElection(ballotsForElection)

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  private final case class TallyRequest[E, T](election: E, tallier: T)

  private object TallyRequest {
    implicit def encoder[E : Encoder, T : Encoder]: Encoder[TallyRequest[E, T]] =
      Encoder.forProduct2("election", "tallier")(t => (t.election, t.tallier))
  }
}

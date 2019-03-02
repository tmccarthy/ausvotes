package au.id.tmm.ausvotes.core.tallying

import au.id.tmm.ausvotes.core.tallies.UnitTallier
import au.id.tmm.ausvotes.core.tallies.typeclasses.Tallier
import au.id.tmm.ausvotes.core.tallying.FetchTallyForElection.TallyRequest
import au.id.tmm.ausvotes.data_sources.common.JsonCache
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError, Concurrent}
import cats.Monoid
import io.circe.{Decoder, Encoder}

final class FetchTallyForElection[F[+_, +_] : Concurrent : JsonCache : FetchTally, E : Encoder, B] private (ballotsForElection: E => F[Exception, fs2.Stream[F[Throwable, +?], B]]) {

  def fetchTally10[
  T1 : Encoder, A1 : Monoid : Encoder : Decoder,
  T2 : Encoder, A2 : Monoid : Encoder : Decoder,
  T3 : Encoder, A3 : Monoid : Encoder : Decoder,
  T4 : Encoder, A4 : Monoid : Encoder : Decoder,
  T5 : Encoder, A5 : Monoid : Encoder : Decoder,
  T6 : Encoder, A6 : Monoid : Encoder : Decoder,
  T7 : Encoder, A7 : Monoid : Encoder : Decoder,
  T8 : Encoder, A8 : Monoid : Encoder : Decoder,
  T9 : Encoder, A9 : Monoid : Encoder : Decoder,
  T10 : Encoder, A10 : Monoid : Encoder : Decoder,
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
  )(implicit
    ti1: Tallier[T1, B, A1],
    ti2: Tallier[T2, B, A2],
    ti3: Tallier[T3, B, A3],
    ti4: Tallier[T4, B, A4],
    ti5: Tallier[T5, B, A5],
    ti6: Tallier[T6, B, A6],
    ti7: Tallier[T7, B, A7],
    ti8: Tallier[T8, B, A8],
    ti9: Tallier[T9, B, A9],
    ti10: Tallier[T10, B, A10],
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

  private def fetchTallyFromCache[T : Encoder, A : Decoder](election: E, tallier: T)(implicit tallierInstance: Tallier[T, B, A]): F[Exception, Option[A]] =
    tallier match {
      case UnitTallier => BifunctorMonadError.pure(Some(().asInstanceOf[A]))
      case tallier => JsonCache.get(TallyRequest(election, tallier))
    }

  private def computeAndCacheTallies[
  T1 : Encoder, A1 : Monoid : Encoder : Decoder,
  T2 : Encoder, A2 : Monoid : Encoder : Decoder,
  T3 : Encoder, A3 : Monoid : Encoder : Decoder,
  T4 : Encoder, A4 : Monoid : Encoder : Decoder,
  T5 : Encoder, A5 : Monoid : Encoder : Decoder,
  T6 : Encoder, A6 : Monoid : Encoder : Decoder,
  T7 : Encoder, A7 : Monoid : Encoder : Decoder,
  T8 : Encoder, A8 : Monoid : Encoder : Decoder,
  T9 : Encoder, A9 : Monoid : Encoder : Decoder,
  T10 : Encoder, A10 : Monoid : Encoder : Decoder,
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
  )(implicit
    ti1: Tallier[T1, B, A1],
    ti2: Tallier[T2, B, A2],
    ti3: Tallier[T3, B, A3],
    ti4: Tallier[T4, B, A4],
    ti5: Tallier[T5, B, A5],
    ti6: Tallier[T6, B, A6],
    ti7: Tallier[T7, B, A7],
    ti8: Tallier[T8, B, A8],
    ti9: Tallier[T9, B, A9],
    ti10: Tallier[T10, B, A10],
  ): F[Exception, (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)] =
    for {
      ballots <- ballotsForElection(election)

      tallyResults <- implicitly[FetchTally[F]].fetchTally10[B, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10](ballots)(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10)

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

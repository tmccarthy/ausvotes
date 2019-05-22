package au.id.tmm.ausvotes.core.tallying

import au.id.tmm.ausvotes.core.tallies.UnitTallier._
import au.id.tmm.ausvotes.core.tallies.redo.BallotTallier
import au.id.tmm.ausvotes.core.tallies.redo.BallotTallier.{UnitBallotTallier => UT}
import au.id.tmm.ausvotes.core.tallying.FetchTallyForElection.TallyRequest
import au.id.tmm.ausvotes.data_sources.common.JsonCache
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.bfect.BifunctorMonadError
import au.id.tmm.bfect.effects.Concurrent
import au.id.tmm.bfect.effects.Concurrent._
import cats.Monoid
import io.circe.{Decoder, Encoder}

final class FetchTallyForElection[F[+_, +_] : Concurrent : JsonCache : FetchTally, E : Encoder, B] private (ballotsForElection: E => F[Exception, fs2.Stream[F[Throwable, +?], B]]) {

  def fetchTally1[A1 : Monoid : Encoder : Decoder](election: E)(t1: BallotTallier[B, A1]): F[FetchTallyForElection.Error, A1] =
    fetchTally10[A1, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit](election)(t1, UT, UT, UT, UT, UT, UT, UT, UT, UT).map {
      case (a1, _, _, _, _, _, _, _, _, _) => a1
    }

  def fetchTally2[A1 : Monoid : Encoder : Decoder, A2 : Monoid : Encoder : Decoder](election: E)(t1: BallotTallier[B, A1], t2: BallotTallier[B, A2]): F[FetchTallyForElection.Error, (A1, A2)] =
    fetchTally10[A1, A2, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit](election)(t1, t2, UT, UT, UT, UT, UT, UT, UT, UT).map {
      case (a1, a2, _, _, _, _, _, _, _, _) => (a1, a2)
    }

  def fetchTally3[A1 : Monoid : Encoder : Decoder, A2 : Monoid : Encoder : Decoder, A3 : Monoid : Encoder : Decoder](election: E)(t1: BallotTallier[B, A1], t2: BallotTallier[B, A2], t3: BallotTallier[B, A3]): F[FetchTallyForElection.Error, (A1, A2, A3)] =
    fetchTally10[A1, A2, A3, Unit, Unit, Unit, Unit, Unit, Unit, Unit](election)(t1, t2, t3, UT, UT, UT, UT, UT, UT, UT).map {
      case (a1, a2, a3, _, _, _, _, _, _, _) => (a1, a2, a3)
    }

  def fetchTally4[A1 : Monoid : Encoder : Decoder, A2 : Monoid : Encoder : Decoder, A3 : Monoid : Encoder : Decoder, A4 : Monoid : Encoder : Decoder](election: E)(t1: BallotTallier[B, A1], t2: BallotTallier[B, A2], t3: BallotTallier[B, A3], t4: BallotTallier[B, A4]): F[FetchTallyForElection.Error, (A1, A2, A3, A4)] =
    fetchTally10[A1, A2, A3, A4, Unit, Unit, Unit, Unit, Unit, Unit](election)(t1, t2, t3, t4, UT, UT, UT, UT, UT, UT).map {
      case (a1, a2, a3, a4, _, _, _, _, _, _) => (a1, a2, a3, a4)
    }

  def fetchTally5[A1 : Monoid : Encoder : Decoder, A2 : Monoid : Encoder : Decoder, A3 : Monoid : Encoder : Decoder, A4 : Monoid : Encoder : Decoder, A5 : Monoid : Encoder : Decoder](election: E)(t1: BallotTallier[B, A1], t2: BallotTallier[B, A2], t3: BallotTallier[B, A3], t4: BallotTallier[B, A4], t5: BallotTallier[B, A5]): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5)] =
    fetchTally10[A1, A2, A3, A4, A5, Unit, Unit, Unit, Unit, Unit](election)(t1, t2, t3, t4, t5, UT, UT, UT, UT, UT).map {
      case (a1, a2, a3, a4, a5, _, _, _, _, _) => (a1, a2, a3, a4, a5)
    }

  def fetchTally6[A1 : Monoid : Encoder : Decoder, A2 : Monoid : Encoder : Decoder, A3 : Monoid : Encoder : Decoder, A4 : Monoid : Encoder : Decoder, A5 : Monoid : Encoder : Decoder, A6 : Monoid : Encoder : Decoder](election: E)(t1: BallotTallier[B, A1], t2: BallotTallier[B, A2], t3: BallotTallier[B, A3], t4: BallotTallier[B, A4], t5: BallotTallier[B, A5], t6: BallotTallier[B, A6]): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5, A6)] =
    fetchTally10[A1, A2, A3, A4, A5, A6, Unit, Unit, Unit, Unit](election)(t1, t2, t3, t4, t5, t6, UT, UT, UT, UT).map {
      case (a1, a2, a3, a4, a5, a6, _, _, _, _) => (a1, a2, a3, a4, a5, a6)
    }

  def fetchTally7[A1 : Monoid : Encoder : Decoder, A2 : Monoid : Encoder : Decoder, A3 : Monoid : Encoder : Decoder, A4 : Monoid : Encoder : Decoder, A5 : Monoid : Encoder : Decoder, A6 : Monoid : Encoder : Decoder, A7 : Monoid : Encoder : Decoder](election: E)(t1: BallotTallier[B, A1], t2: BallotTallier[B, A2], t3: BallotTallier[B, A3], t4: BallotTallier[B, A4], t5: BallotTallier[B, A5], t6: BallotTallier[B, A6], t7: BallotTallier[B, A7]): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5, A6, A7)] =
    fetchTally10[A1, A2, A3, A4, A5, A6, A7, Unit, Unit, Unit](election)(t1, t2, t3, t4, t5, t6, t7, UT, UT, UT).map {
      case (a1, a2, a3, a4, a5, a6, a7, _, _, _) => (a1, a2, a3, a4, a5, a6, a7)
    }

  def fetchTally8[A1 : Monoid : Encoder : Decoder, A2 : Monoid : Encoder : Decoder, A3 : Monoid : Encoder : Decoder, A4 : Monoid : Encoder : Decoder, A5 : Monoid : Encoder : Decoder, A6 : Monoid : Encoder : Decoder, A7 : Monoid : Encoder : Decoder, A8 : Monoid : Encoder : Decoder](election: E)(t1: BallotTallier[B, A1], t2: BallotTallier[B, A2], t3: BallotTallier[B, A3], t4: BallotTallier[B, A4], t5: BallotTallier[B, A5], t6: BallotTallier[B, A6], t7: BallotTallier[B, A7], t8: BallotTallier[B, A8]): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5, A6, A7, A8)] =
    fetchTally10[A1, A2, A3, A4, A5, A6, A7, A8, Unit, Unit](election)(t1, t2, t3, t4, t5, t6, t7, t8, UT, UT).map {
      case (a1, a2, a3, a4, a5, a6, a7, a8, _, _) => (a1, a2, a3, a4, a5, a6, a7, a8)
    }

  def fetchTally9[A1 : Monoid : Encoder : Decoder, A2 : Monoid : Encoder : Decoder, A3 : Monoid : Encoder : Decoder, A4 : Monoid : Encoder : Decoder, A5 : Monoid : Encoder : Decoder, A6 : Monoid : Encoder : Decoder, A7 : Monoid : Encoder : Decoder, A8 : Monoid : Encoder : Decoder, A9 : Monoid : Encoder : Decoder](election: E)(t1: BallotTallier[B, A1], t2: BallotTallier[B, A2], t3: BallotTallier[B, A3], t4: BallotTallier[B, A4], t5: BallotTallier[B, A5], t6: BallotTallier[B, A6], t7: BallotTallier[B, A7], t8: BallotTallier[B, A8], t9: BallotTallier[B, A9]): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9)] =
    fetchTally10[A1, A2, A3, A4, A5, A6, A7, A8, A9, Unit](election)(t1, t2, t3, t4, t5, t6, t7, t8, t9, UT).map {
      case (a1, a2, a3, a4, a5, a6, a7, a8, a9, _) => (a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }

  def fetchTally10[
  A1 : Monoid : Encoder : Decoder,
  A2 : Monoid : Encoder : Decoder,
  A3 : Monoid : Encoder : Decoder,
  A4 : Monoid : Encoder : Decoder,
  A5 : Monoid : Encoder : Decoder,
  A6 : Monoid : Encoder : Decoder,
  A7 : Monoid : Encoder : Decoder,
  A8 : Monoid : Encoder : Decoder,
  A9 : Monoid : Encoder : Decoder,
  A10 : Monoid : Encoder : Decoder,
  ]
  (election: E)(
    t1: BallotTallier[B, A1],
    t2: BallotTallier[B, A2],
    t3: BallotTallier[B, A3],
    t4: BallotTallier[B, A4],
    t5: BallotTallier[B, A5],
    t6: BallotTallier[B, A6],
    t7: BallotTallier[B, A7],
    t8: BallotTallier[B, A8],
    t9: BallotTallier[B, A9],
    t10: BallotTallier[B, A10],
  ): F[FetchTallyForElection.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)] = {
    for {
      cachedAs <- Concurrent.par10(
        fetchTallyFromCache[A1](election, t1),
        fetchTallyFromCache[A2](election, t2),
        fetchTallyFromCache[A3](election, t3),
        fetchTallyFromCache[A4](election, t4),
        fetchTallyFromCache[A5](election, t5),
        fetchTallyFromCache[A6](election, t6),
        fetchTallyFromCache[A7](election, t7),
        fetchTallyFromCache[A8](election, t8),
        fetchTallyFromCache[A9](election, t9),
        fetchTallyFromCache[A10](election, t10),
      )

      allTallies <- cachedAs match {
        case (Some(a1), Some(a2), Some(a3), Some(a4), Some(a5), Some(a6), Some(a7), Some(a8), Some(a9), Some(a10)) => BifunctorMonadError.pure((a1, a2, a3, a4, a5, a6, a7, a8, a9, a10))
        case _ => computeAndCacheTallies[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10](election)(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10)
      }
    } yield allTallies
  }.leftMap(FetchTallyForElection.Error)

  private def fetchTallyFromCache[A : Decoder](election: E, tallier: BallotTallier[B, A]): F[Exception, Option[A]] =
    tallier match {
      case UT => BifunctorMonadError.pure(Some(().asInstanceOf[A]))
      case tallier => JsonCache.get(TallyRequest(election, tallier))
    }

  private def computeAndCacheTallies[
  A1 : Monoid : Encoder : Decoder,
  A2 : Monoid : Encoder : Decoder,
  A3 : Monoid : Encoder : Decoder,
  A4 : Monoid : Encoder : Decoder,
  A5 : Monoid : Encoder : Decoder,
  A6 : Monoid : Encoder : Decoder,
  A7 : Monoid : Encoder : Decoder,
  A8 : Monoid : Encoder : Decoder,
  A9 : Monoid : Encoder : Decoder,
  A10 : Monoid : Encoder : Decoder,
  ]
  (election: E)(
    t1: BallotTallier[B, A1],
    t2: BallotTallier[B, A2],
    t3: BallotTallier[B, A3],
    t4: BallotTallier[B, A4],
    t5: BallotTallier[B, A5],
    t6: BallotTallier[B, A6],
    t7: BallotTallier[B, A7],
    t8: BallotTallier[B, A8],
    t9: BallotTallier[B, A9],
    t10: BallotTallier[B, A10],
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

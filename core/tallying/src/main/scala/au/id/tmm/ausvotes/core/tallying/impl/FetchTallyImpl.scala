package au.id.tmm.ausvotes.core.tallying.impl

import au.id.tmm.ausvotes.core.tallies.redo.BallotTallier
import au.id.tmm.ausvotes.core.tallying.FetchTally
import au.id.tmm.ausvotes.core.tallying.impl.FetchTallyImpl.{TallyBundle, TallyRequest, TallyRequests}
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop.ThrowableEOps
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.CatsInterop._
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError, Concurrent, SyncEffects}
import cats.Monoid

final class FetchTallyImpl[F[+_, +_] : Concurrent] private (chunkSize: Int = 5000) extends FetchTally[F] {
  override def fetchTally1[B, A1 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1]): F[FetchTally.Error, A1] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[B, A1](tallier1))).map { bundle =>
      val tally1 = bundle.getTallySafe[A1](tallier1)
      tally1
    }

  override def fetchTally2[B, A1 : Monoid, A2 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2]): F[FetchTally.Error, (A1, A2)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[B, A1](tallier1), TallyRequest[B, A2](tallier2))).map { bundle =>
      val tally1 = bundle.getTallySafe[A1](tallier1)
      val tally2 = bundle.getTallySafe[A2](tallier2)
      (tally1, tally2)
    }

  override def fetchTally3[B, A1 : Monoid, A2 : Monoid, A3 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3]): F[FetchTally.Error, (A1, A2, A3)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[B, A1](tallier1), TallyRequest[B, A2](tallier2), TallyRequest[B, A3](tallier3))).map { bundle =>
      val tally1 = bundle.getTallySafe[A1](tallier1)
      val tally2 = bundle.getTallySafe[A2](tallier2)
      val tally3 = bundle.getTallySafe[A3](tallier3)
      (tally1, tally2, tally3)
    }

  override def fetchTally4[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4]): F[FetchTally.Error, (A1, A2, A3, A4)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[B, A1](tallier1), TallyRequest[B, A2](tallier2), TallyRequest[B, A3](tallier3), TallyRequest[B, A4](tallier4))).map { bundle =>
      val tally1 = bundle.getTallySafe[A1](tallier1)
      val tally2 = bundle.getTallySafe[A2](tallier2)
      val tally3 = bundle.getTallySafe[A3](tallier3)
      val tally4 = bundle.getTallySafe[A4](tallier4)
      (tally1, tally2, tally3, tally4)
    }

  override def fetchTally5[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5]): F[FetchTally.Error, (A1, A2, A3, A4, A5)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[B, A1](tallier1), TallyRequest[B, A2](tallier2), TallyRequest[B, A3](tallier3), TallyRequest[B, A4](tallier4), TallyRequest[B, A5](tallier5))).map { bundle =>
      val tally1 = bundle.getTallySafe[A1](tallier1)
      val tally2 = bundle.getTallySafe[A2](tallier2)
      val tally3 = bundle.getTallySafe[A3](tallier3)
      val tally4 = bundle.getTallySafe[A4](tallier4)
      val tally5 = bundle.getTallySafe[A5](tallier5)
      (tally1, tally2, tally3, tally4, tally5)
    }

  override def fetchTally6[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5], tallier6: BallotTallier[B, A6]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[B, A1](tallier1), TallyRequest[B, A2](tallier2), TallyRequest[B, A3](tallier3), TallyRequest[B, A4](tallier4), TallyRequest[B, A5](tallier5), TallyRequest[B, A6](tallier6))).map { bundle =>
      val tally1 = bundle.getTallySafe[A1](tallier1)
      val tally2 = bundle.getTallySafe[A2](tallier2)
      val tally3 = bundle.getTallySafe[A3](tallier3)
      val tally4 = bundle.getTallySafe[A4](tallier4)
      val tally5 = bundle.getTallySafe[A5](tallier5)
      val tally6 = bundle.getTallySafe[A6](tallier6)
      (tally1, tally2, tally3, tally4, tally5, tally6)
    }

  override def fetchTally7[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5], tallier6: BallotTallier[B, A6], tallier7: BallotTallier[B, A7]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[B, A1](tallier1), TallyRequest[B, A2](tallier2), TallyRequest[B, A3](tallier3), TallyRequest[B, A4](tallier4), TallyRequest[B, A5](tallier5), TallyRequest[B, A6](tallier6), TallyRequest[B, A7](tallier7))).map { bundle =>
      val tally1 = bundle.getTallySafe[A1](tallier1)
      val tally2 = bundle.getTallySafe[A2](tallier2)
      val tally3 = bundle.getTallySafe[A3](tallier3)
      val tally4 = bundle.getTallySafe[A4](tallier4)
      val tally5 = bundle.getTallySafe[A5](tallier5)
      val tally6 = bundle.getTallySafe[A6](tallier6)
      val tally7 = bundle.getTallySafe[A7](tallier7)
      (tally1, tally2, tally3, tally4, tally5, tally6, tally7)
    }

  override def fetchTally8[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5], tallier6: BallotTallier[B, A6], tallier7: BallotTallier[B, A7], tallier8: BallotTallier[B, A8]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[B, A1](tallier1), TallyRequest[B, A2](tallier2), TallyRequest[B, A3](tallier3), TallyRequest[B, A4](tallier4), TallyRequest[B, A5](tallier5), TallyRequest[B, A6](tallier6), TallyRequest[B, A7](tallier7), TallyRequest[B, A8](tallier8))).map { bundle =>
      val tally1 = bundle.getTallySafe[A1](tallier1)
      val tally2 = bundle.getTallySafe[A2](tallier2)
      val tally3 = bundle.getTallySafe[A3](tallier3)
      val tally4 = bundle.getTallySafe[A4](tallier4)
      val tally5 = bundle.getTallySafe[A5](tallier5)
      val tally6 = bundle.getTallySafe[A6](tallier6)
      val tally7 = bundle.getTallySafe[A7](tallier7)
      val tally8 = bundle.getTallySafe[A8](tallier8)
      (tally1, tally2, tally3, tally4, tally5, tally6, tally7, tally8)
    }

  override def fetchTally9[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid, A9 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5], tallier6: BallotTallier[B, A6], tallier7: BallotTallier[B, A7], tallier8: BallotTallier[B, A8], tallier9: BallotTallier[B, A9]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[B, A1](tallier1), TallyRequest[B, A2](tallier2), TallyRequest[B, A3](tallier3), TallyRequest[B, A4](tallier4), TallyRequest[B, A5](tallier5), TallyRequest[B, A6](tallier6), TallyRequest[B, A7](tallier7), TallyRequest[B, A8](tallier8), TallyRequest[B, A9](tallier9))).map { bundle =>
      val tally1 = bundle.getTallySafe[A1](tallier1)
      val tally2 = bundle.getTallySafe[A2](tallier2)
      val tally3 = bundle.getTallySafe[A3](tallier3)
      val tally4 = bundle.getTallySafe[A4](tallier4)
      val tally5 = bundle.getTallySafe[A5](tallier5)
      val tally6 = bundle.getTallySafe[A6](tallier6)
      val tally7 = bundle.getTallySafe[A7](tallier7)
      val tally8 = bundle.getTallySafe[A8](tallier8)
      val tally9 = bundle.getTallySafe[A9](tallier9)
      (tally1, tally2, tally3, tally4, tally5, tally6, tally7, tally8, tally9)
    }

  override def fetchTally10[B, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid, A9 : Monoid, A10 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: BallotTallier[B, A1], tallier2: BallotTallier[B, A2], tallier3: BallotTallier[B, A3], tallier4: BallotTallier[B, A4], tallier5: BallotTallier[B, A5], tallier6: BallotTallier[B, A6], tallier7: BallotTallier[B, A7], tallier8: BallotTallier[B, A8], tallier9: BallotTallier[B, A9], tallier10: BallotTallier[B, A10]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[B, A1](tallier1), TallyRequest[B, A2](tallier2), TallyRequest[B, A3](tallier3), TallyRequest[B, A4](tallier4), TallyRequest[B, A5](tallier5), TallyRequest[B, A6](tallier6), TallyRequest[B, A7](tallier7), TallyRequest[B, A8](tallier8), TallyRequest[B, A9](tallier9), TallyRequest[B, A10](tallier10))).map { bundle =>
      val tally1 = bundle.getTallySafe[A1](tallier1)
      val tally2 = bundle.getTallySafe[A2](tallier2)
      val tally3 = bundle.getTallySafe[A3](tallier3)
      val tally4 = bundle.getTallySafe[A4](tallier4)
      val tally5 = bundle.getTallySafe[A5](tallier5)
      val tally6 = bundle.getTallySafe[A6](tallier6)
      val tally7 = bundle.getTallySafe[A7](tallier7)
      val tally8 = bundle.getTallySafe[A8](tallier8)
      val tally9 = bundle.getTallySafe[A9](tallier9)
      val tally10 = bundle.getTallySafe[A10](tallier10)
      (tally1, tally2, tally3, tally4, tally5, tally6, tally7, tally8, tally9, tally10)
    }

  private def fetchTalliesUnsafe[B](ballots: fs2.Stream[F[Throwable, +?], B], tallyRequests: TallyRequests[B]): F[FetchTally.Error, TallyBundle[B]] =
    ballots.chunkN(chunkSize)
      .parEvalMapUnordered(maxConcurrent = Runtime.getRuntime.availableProcessors()) { chunk =>
        if (chunk.nonEmpty) {
          SyncEffects.syncThrowable(applyTallyRequests[B](tallyRequests, chunk.toVector))
        } else {
          BifunctorMonadError.pure(TallyBundle.empty[B])
        }
      }
      .foldMonoid
      .compile
      .lastOrError
      .swallowThrowablesAndWrapIn(FetchTally.Error)

  private def applyTallyRequests[B](tallyRequests: TallyRequests[B], ballots: Vector[B]): TallyBundle[B] = TallyBundle {
    tallyRequests.requests.map { case tallyRequest @ TallyRequest(_) =>
      val tallyValue = tallyRequest.tallier.tallyAll(ballots)

      tallyRequest.tallier -> TallyBundle.Value(tallyValue, tallyRequest.valueMonoid)
    }.toMap
  }

}

object FetchTallyImpl {

  def apply[F[+_, +_] : Concurrent]: FetchTallyImpl[F] = new FetchTallyImpl()

  private final case class TallyRequest[B, A](tallier: BallotTallier[B, A])(implicit val valueMonoid: Monoid[A])

  private final case class TallyRequests[B](requests: List[TallyRequest[B, _]])

  private object TallyRequests {
    def apply[B](requests: TallyRequest[B, _]*): TallyRequests[B] = TallyRequests(requests.toList)
  }

  private final case class TallyBundle[B](underlying: Map[BallotTallier[B, _], TallyBundle.Value[_]]) {
    def getTallySafe[A](tallier: BallotTallier[B, A]): A =
      underlying(tallier).value.asInstanceOf[A]
  }

  private object TallyBundle {
    def empty[B]: TallyBundle[B] = TallyBundle(Map.empty)

    implicit def monoid[B]: Monoid[TallyBundle[B]] = new Monoid[TallyBundle[B]] {
      override def empty: TallyBundle[B] = TallyBundle.empty
      override def combine(left: TallyBundle[B], right: TallyBundle[B]): TallyBundle[B] = {
        val talliers = left.underlying.keySet ++ right.underlying.keySet

        val newUnderlyingMap: Map[BallotTallier[B, _], Value[_]] = talliers.flatMap { tallier =>
          val leftTallyBundleValue: Option[Value[_]] = left.underlying.get(tallier)
          val rightTallyBundleValue: Option[Value[_]] = right.underlying.get(tallier)

          val maybeMonoid = leftTallyBundleValue.map(_.monoidForValueType) orElse rightTallyBundleValue.map(_.monoidForValueType)

          maybeMonoid.map { monoid =>
            val left = leftTallyBundleValue.map(_.value).getOrElse(monoid.empty)
            val right = rightTallyBundleValue.map(_.value).getOrElse(monoid.empty)

            tallier -> Value(
              value = monoid.asInstanceOf[Monoid[Any]].combine(left, right),
              monoid.asInstanceOf[Monoid[Any]],
            )
          }
        }.toMap

        TallyBundle(newUnderlyingMap)
      }
    }

    final case class Value[A](value: A, monoidForValueType: Monoid[A])
  }

}

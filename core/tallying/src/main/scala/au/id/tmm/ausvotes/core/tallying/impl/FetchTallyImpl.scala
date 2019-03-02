package au.id.tmm.ausvotes.core.tallying.impl

import au.id.tmm.ausvotes.core.tallies.typeclasses.Tallier
import au.id.tmm.ausvotes.core.tallying.FetchTally
import au.id.tmm.ausvotes.core.tallying.impl.FetchTallyImpl.{TallyBundle, TallyRequest, TallyRequests}
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop.ThrowableEOps
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.CatsInterop._
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError, Concurrent}
import cats.Monoid

final class FetchTallyImpl[F[+_, +_] : Concurrent](chunkSize: Int = 5000) extends FetchTally[F] {
  override def fetchTally1[B, T1, A1 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1)(implicit t1: Tallier[T1, B, A1]): F[FetchTally.Error, A1] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T1, B, A1](tallier1))).map { bundle =>
      val tally1 = bundle.getTallySafe[T1, A1](tallier1)(t1)
      tally1
    }

  override def fetchTally2[B, T1, T2, A1 : Monoid, A2 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2]): F[FetchTally.Error, (A1, A2)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T1, B, A1](tallier1), TallyRequest[T2, B, A2](tallier2))).map { bundle =>
      val tally1 = bundle.getTallySafe[T1, A1](tallier1)(t1)
      val tally2 = bundle.getTallySafe[T2, A2](tallier2)(t2)
      (tally1, tally2)
    }

  override def fetchTally3[B, T1, T2, T3, A1 : Monoid, A2 : Monoid, A3 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3]): F[FetchTally.Error, (A1, A2, A3)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T1, B, A1](tallier1), TallyRequest[T2, B, A2](tallier2), TallyRequest[T3, B, A3](tallier3))).map { bundle =>
      val tally1 = bundle.getTallySafe[T1, A1](tallier1)(t1)
      val tally2 = bundle.getTallySafe[T2, A2](tallier2)(t2)
      val tally3 = bundle.getTallySafe[T3, A3](tallier3)(t3)
      (tally1, tally2, tally3)
    }

  override def fetchTally4[B, T1, T2, T3, T4, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4]): F[FetchTally.Error, (A1, A2, A3, A4)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T1, B, A1](tallier1), TallyRequest[T2, B, A2](tallier2), TallyRequest[T3, B, A3](tallier3), TallyRequest[T4, B, A4](tallier4))).map { bundle =>
      val tally1 = bundle.getTallySafe[T1, A1](tallier1)(t1)
      val tally2 = bundle.getTallySafe[T2, A2](tallier2)(t2)
      val tally3 = bundle.getTallySafe[T3, A3](tallier3)(t3)
      val tally4 = bundle.getTallySafe[T4, A4](tallier4)(t4)
      (tally1, tally2, tally3, tally4)
    }

  override def fetchTally5[B, T1, T2, T3, T4, T5, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5]): F[FetchTally.Error, (A1, A2, A3, A4, A5)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T1, B, A1](tallier1), TallyRequest[T2, B, A2](tallier2), TallyRequest[T3, B, A3](tallier3), TallyRequest[T4, B, A4](tallier4), TallyRequest[T5, B, A5](tallier5))).map { bundle =>
      val tally1 = bundle.getTallySafe[T1, A1](tallier1)(t1)
      val tally2 = bundle.getTallySafe[T2, A2](tallier2)(t2)
      val tally3 = bundle.getTallySafe[T3, A3](tallier3)(t3)
      val tally4 = bundle.getTallySafe[T4, A4](tallier4)(t4)
      val tally5 = bundle.getTallySafe[T5, A5](tallier5)(t5)
      (tally1, tally2, tally3, tally4, tally5)
    }

  override def fetchTally6[B, T1, T2, T3, T4, T5, T6, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5, tallier6: T6)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5], t6: Tallier[T6, B, A6]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T1, B, A1](tallier1), TallyRequest[T2, B, A2](tallier2), TallyRequest[T3, B, A3](tallier3), TallyRequest[T4, B, A4](tallier4), TallyRequest[T5, B, A5](tallier5), TallyRequest[T6, B, A6](tallier6))).map { bundle =>
      val tally1 = bundle.getTallySafe[T1, A1](tallier1)(t1)
      val tally2 = bundle.getTallySafe[T2, A2](tallier2)(t2)
      val tally3 = bundle.getTallySafe[T3, A3](tallier3)(t3)
      val tally4 = bundle.getTallySafe[T4, A4](tallier4)(t4)
      val tally5 = bundle.getTallySafe[T5, A5](tallier5)(t5)
      val tally6 = bundle.getTallySafe[T6, A6](tallier6)(t6)
      (tally1, tally2, tally3, tally4, tally5, tally6)
    }

  override def fetchTally7[B, T1, T2, T3, T4, T5, T6, T7, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5, tallier6: T6, tallier7: T7)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5], t6: Tallier[T6, B, A6], t7: Tallier[T7, B, A7]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T1, B, A1](tallier1), TallyRequest[T2, B, A2](tallier2), TallyRequest[T3, B, A3](tallier3), TallyRequest[T4, B, A4](tallier4), TallyRequest[T5, B, A5](tallier5), TallyRequest[T6, B, A6](tallier6), TallyRequest[T7, B, A7](tallier7))).map { bundle =>
      val tally1 = bundle.getTallySafe[T1, A1](tallier1)(t1)
      val tally2 = bundle.getTallySafe[T2, A2](tallier2)(t2)
      val tally3 = bundle.getTallySafe[T3, A3](tallier3)(t3)
      val tally4 = bundle.getTallySafe[T4, A4](tallier4)(t4)
      val tally5 = bundle.getTallySafe[T5, A5](tallier5)(t5)
      val tally6 = bundle.getTallySafe[T6, A6](tallier6)(t6)
      val tally7 = bundle.getTallySafe[T7, A7](tallier7)(t7)
      (tally1, tally2, tally3, tally4, tally5, tally6, tally7)
    }

  override def fetchTally8[B, T1, T2, T3, T4, T5, T6, T7, T8, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5, tallier6: T6, tallier7: T7, tallier8: T8)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5], t6: Tallier[T6, B, A6], t7: Tallier[T7, B, A7], t8: Tallier[T8, B, A8]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T1, B, A1](tallier1), TallyRequest[T2, B, A2](tallier2), TallyRequest[T3, B, A3](tallier3), TallyRequest[T4, B, A4](tallier4), TallyRequest[T5, B, A5](tallier5), TallyRequest[T6, B, A6](tallier6), TallyRequest[T7, B, A7](tallier7), TallyRequest[T8, B, A8](tallier8))).map { bundle =>
      val tally1 = bundle.getTallySafe[T1, A1](tallier1)(t1)
      val tally2 = bundle.getTallySafe[T2, A2](tallier2)(t2)
      val tally3 = bundle.getTallySafe[T3, A3](tallier3)(t3)
      val tally4 = bundle.getTallySafe[T4, A4](tallier4)(t4)
      val tally5 = bundle.getTallySafe[T5, A5](tallier5)(t5)
      val tally6 = bundle.getTallySafe[T6, A6](tallier6)(t6)
      val tally7 = bundle.getTallySafe[T7, A7](tallier7)(t7)
      val tally8 = bundle.getTallySafe[T8, A8](tallier8)(t8)
      (tally1, tally2, tally3, tally4, tally5, tally6, tally7, tally8)
    }

  override def fetchTally9[B, T1, T2, T3, T4, T5, T6, T7, T8, T9, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid, A9 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5, tallier6: T6, tallier7: T7, tallier8: T8, tallier9: T9)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5], t6: Tallier[T6, B, A6], t7: Tallier[T7, B, A7], t8: Tallier[T8, B, A8], t9: Tallier[T9, B, A9]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T1, B, A1](tallier1), TallyRequest[T2, B, A2](tallier2), TallyRequest[T3, B, A3](tallier3), TallyRequest[T4, B, A4](tallier4), TallyRequest[T5, B, A5](tallier5), TallyRequest[T6, B, A6](tallier6), TallyRequest[T7, B, A7](tallier7), TallyRequest[T8, B, A8](tallier8), TallyRequest[T9, B, A9](tallier9))).map { bundle =>
      val tally1 = bundle.getTallySafe[T1, A1](tallier1)(t1)
      val tally2 = bundle.getTallySafe[T2, A2](tallier2)(t2)
      val tally3 = bundle.getTallySafe[T3, A3](tallier3)(t3)
      val tally4 = bundle.getTallySafe[T4, A4](tallier4)(t4)
      val tally5 = bundle.getTallySafe[T5, A5](tallier5)(t5)
      val tally6 = bundle.getTallySafe[T6, A6](tallier6)(t6)
      val tally7 = bundle.getTallySafe[T7, A7](tallier7)(t7)
      val tally8 = bundle.getTallySafe[T8, A8](tallier8)(t8)
      val tally9 = bundle.getTallySafe[T9, A9](tallier9)(t9)
      (tally1, tally2, tally3, tally4, tally5, tally6, tally7, tally8, tally9)
    }

  override def fetchTally10[B, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, A1 : Monoid, A2 : Monoid, A3 : Monoid, A4 : Monoid, A5 : Monoid, A6 : Monoid, A7 : Monoid, A8 : Monoid, A9 : Monoid, A10 : Monoid](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T1, tallier2: T2, tallier3: T3, tallier4: T4, tallier5: T5, tallier6: T6, tallier7: T7, tallier8: T8, tallier9: T9, tallier10: T10)(implicit t1: Tallier[T1, B, A1], t2: Tallier[T2, B, A2], t3: Tallier[T3, B, A3], t4: Tallier[T4, B, A4], t5: Tallier[T5, B, A5], t6: Tallier[T6, B, A6], t7: Tallier[T7, B, A7], t8: Tallier[T8, B, A8], t9: Tallier[T9, B, A9], t10: Tallier[T10, B, A10]): F[FetchTally.Error, (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T1, B, A1](tallier1), TallyRequest[T2, B, A2](tallier2), TallyRequest[T3, B, A3](tallier3), TallyRequest[T4, B, A4](tallier4), TallyRequest[T5, B, A5](tallier5), TallyRequest[T6, B, A6](tallier6), TallyRequest[T7, B, A7](tallier7), TallyRequest[T8, B, A8](tallier8), TallyRequest[T9, B, A9](tallier9), TallyRequest[T10, B, A10](tallier10))).map { bundle =>
      val tally1 = bundle.getTallySafe[T1, A1](tallier1)(t1)
      val tally2 = bundle.getTallySafe[T2, A2](tallier2)(t2)
      val tally3 = bundle.getTallySafe[T3, A3](tallier3)(t3)
      val tally4 = bundle.getTallySafe[T4, A4](tallier4)(t4)
      val tally5 = bundle.getTallySafe[T5, A5](tallier5)(t5)
      val tally6 = bundle.getTallySafe[T6, A6](tallier6)(t6)
      val tally7 = bundle.getTallySafe[T7, A7](tallier7)(t7)
      val tally8 = bundle.getTallySafe[T8, A8](tallier8)(t8)
      val tally9 = bundle.getTallySafe[T9, A9](tallier9)(t9)
      val tally10 = bundle.getTallySafe[T10, A10](tallier10)(t10)
      (tally1, tally2, tally3, tally4, tally5, tally6, tally7, tally8, tally9, tally10)
    }

  private def fetchTalliesUnsafe[B](ballots: fs2.Stream[F[Throwable, +?], B], tallyRequests: TallyRequests[B]): F[FetchTally.Error, TallyBundle[B]] =
    ballots.chunkN(5000)
      .parEvalMapUnordered(maxConcurrent = Runtime.getRuntime.availableProcessors()) { chunk =>
        if (chunk.nonEmpty) {
          BifunctorMonadError.pure(applyTallyRequests[B](tallyRequests, chunk.toVector))
        } else {
          BifunctorMonadError.pure(TallyBundle.empty[B])
        }
      }
      .foldMonoid
      .compile
      .lastOrError
      .swallowThrowablesAndWrapIn(FetchTally.Error)

  private def applyTallyRequests[B](tallyRequests: TallyRequests[B], ballots: Vector[B]): TallyBundle[B] = TallyBundle {
    tallyRequests.requests.map { case TallyRequest(tallier, tallierInstance, valueMonoid) =>
      val tallyValue = tallierInstance.tallyAll(tallier)(ballots)

      tallier -> TallyBundle.Value(tallyValue, valueMonoid)
    }.toMap
  }

}

object FetchTallyImpl {

  import au.id.tmm.ausvotes.core.tallying.impl.FetchTallyImpl.TallyBundle.UnknownTallier

  private final case class TallyRequest[T_TALLIER, B, A](tallier: T_TALLIER, tallierInstance: Tallier[T_TALLIER, B, A], valueMonoid: Monoid[A])

  private object TallyRequest {
    def apply[T_TALLIER, B, A : Monoid](tallier: T_TALLIER)(implicit tallierInstance: Tallier[T_TALLIER, B, A]): TallyRequest[T_TALLIER, B, A] = TallyRequest(tallier, tallierInstance, implicitly[Monoid[A]])
  }

  private final case class TallyRequests[B](requests: List[TallyRequest[_, B, _]])

  private object TallyRequests {
    def apply[B](requests: TallyRequest[_, B, _]*): TallyRequests[B] = TallyRequests(requests.toList)
  }

  private final case class TallyBundle[B](underlying: Map[UnknownTallier, TallyBundle.Value[_]]) {
    def getTallySafe[T_TALLIER, A](tallier: T_TALLIER)(tallierInstance: Tallier[T_TALLIER, B, A]): A =
      underlying(tallier).asInstanceOf[A]
  }

  private object TallyBundle {
    def empty[B]: TallyBundle[B] = TallyBundle(Map.empty)

    type UnknownTallier = Any

    implicit def monoid[B]: Monoid[TallyBundle[B]] = new Monoid[TallyBundle[B]] {
      override def empty: TallyBundle[B] = TallyBundle.empty
      override def combine(left: TallyBundle[B], right: TallyBundle[B]): TallyBundle[B] = {
        val talliers = left.underlying.keySet ++ right.underlying.keySet

        val newUnderlyingMap = talliers.flatMap { tallier =>
          val leftTallyBundleValue = left.underlying.get(tallier)
          val rightTallyBundleValue = right.underlying.get(tallier)

          val maybeMonoid = leftTallyBundleValue.map(_.monoidForValueType) orElse rightTallyBundleValue.map(_.monoidForValueType)

          maybeMonoid.map { monoid =>
            tallier -> monoid.asInstanceOf[Monoid[Value[Any]]]
              .combine(leftTallyBundleValue.getOrElse(monoid.empty).asInstanceOf[Value[Any]], rightTallyBundleValue.getOrElse(monoid.empty).asInstanceOf[Value[Any]])
          }
        }.toMap

        TallyBundle(newUnderlyingMap)
      }
    }

    final case class Value[A](value: A, monoidForValueType: Monoid[A])

    object Value {
      implicit def isAMonoid[A : Monoid]: Monoid[Value[A]] = new Monoid[Value[A]] {
        override def empty: Value[A] = Value(Monoid[A].empty, Monoid[A])

        override def combine(left: Value[A], right: Value[A]): Value[A] =
          Value(Monoid.combine(left.value, right.value), Monoid[A])
      }
    }
  }

}

package au.id.tmm.ausvotes.core.tallying.impl

import au.id.tmm.ausvotes.core.tallies.typeclasses.Tallier
import au.id.tmm.ausvotes.core.tallying.FetchTally
import au.id.tmm.ausvotes.core.tallying.impl.FetchTallyImpl.{TallyBundle, TallyRequest, TallyRequests}
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop.ThrowableEOps
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.CatsInterop._
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError, Concurrent}
import cats.Monoid

import scala.reflect.runtime.universe.{TypeTag, WeakTypeTag}

final class FetchTallyImpl[F[+_, +_] : Concurrent](chunkSize: Int = 5000) extends FetchTally[F] {
  override def fetchTally1[B, T_TALLIER_1, A_1 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1)(implicit t1: Tallier[T_TALLIER_1, B, A_1]): F[FetchTally.Error, A_1] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T_TALLIER_1, B, A_1](tallier1))).flatMap { bundle =>
      BifunctorMonadError.fromEither {
        for {
          tally1 <- bundle.getTallySafe[T_TALLIER_1, A_1](tallier1)(t1)
        } yield tally1
      }.leftMap(FetchTally.Error)
    }

  override def fetchTally2[B, T_TALLIER_1, T_TALLIER_2, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2]): F[FetchTally.Error, (A_1, A_2)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T_TALLIER_1, B, A_1](tallier1), TallyRequest[T_TALLIER_2, B, A_2](tallier2))).flatMap { bundle =>
      BifunctorMonadError.fromEither {
        for {
          tally1 <- bundle.getTallySafe[T_TALLIER_1, A_1](tallier1)(t1)
          tally2 <- bundle.getTallySafe[T_TALLIER_2, A_2](tallier2)(t2)
        } yield (tally1, tally2)
      }.leftMap(FetchTally.Error)
    }

  override def fetchTally3[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3]): F[FetchTally.Error, (A_1, A_2, A_3)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T_TALLIER_1, B, A_1](tallier1), TallyRequest[T_TALLIER_2, B, A_2](tallier2), TallyRequest[T_TALLIER_3, B, A_3](tallier3))).flatMap { bundle =>
      BifunctorMonadError.fromEither {
        for {
          tally1 <- bundle.getTallySafe[T_TALLIER_1, A_1](tallier1)(t1)
          tally2 <- bundle.getTallySafe[T_TALLIER_2, A_2](tallier2)(t2)
          tally3 <- bundle.getTallySafe[T_TALLIER_3, A_3](tallier3)(t3)
        } yield (tally1, tally2, tally3)
      }.leftMap(FetchTally.Error)
    }

  override def fetchTally4[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4]): F[FetchTally.Error, (A_1, A_2, A_3, A_4)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T_TALLIER_1, B, A_1](tallier1), TallyRequest[T_TALLIER_2, B, A_2](tallier2), TallyRequest[T_TALLIER_3, B, A_3](tallier3), TallyRequest[T_TALLIER_4, B, A_4](tallier4))).flatMap { bundle =>
      BifunctorMonadError.fromEither {
        for {
          tally1 <- bundle.getTallySafe[T_TALLIER_1, A_1](tallier1)(t1)
          tally2 <- bundle.getTallySafe[T_TALLIER_2, A_2](tallier2)(t2)
          tally3 <- bundle.getTallySafe[T_TALLIER_3, A_3](tallier3)(t3)
          tally4 <- bundle.getTallySafe[T_TALLIER_4, A_4](tallier4)(t4)
        } yield (tally1, tally2, tally3, tally4)
      }.leftMap(FetchTally.Error)
    }

  override def fetchTally5[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T_TALLIER_1, B, A_1](tallier1), TallyRequest[T_TALLIER_2, B, A_2](tallier2), TallyRequest[T_TALLIER_3, B, A_3](tallier3), TallyRequest[T_TALLIER_4, B, A_4](tallier4), TallyRequest[T_TALLIER_5, B, A_5](tallier5))).flatMap { bundle =>
      BifunctorMonadError.fromEither {
        for {
          tally1 <- bundle.getTallySafe[T_TALLIER_1, A_1](tallier1)(t1)
          tally2 <- bundle.getTallySafe[T_TALLIER_2, A_2](tallier2)(t2)
          tally3 <- bundle.getTallySafe[T_TALLIER_3, A_3](tallier3)(t3)
          tally4 <- bundle.getTallySafe[T_TALLIER_4, A_4](tallier4)(t4)
          tally5 <- bundle.getTallySafe[T_TALLIER_5, A_5](tallier5)(t5)
        } yield (tally1, tally2, tally3, tally4, tally5)
      }.leftMap(FetchTally.Error)
    }

  override def fetchTally6[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, T_TALLIER_6, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag, A_6 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5, tallier6: T_TALLIER_6)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5], t6: Tallier[T_TALLIER_6, B, A_6]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5, A_6)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T_TALLIER_1, B, A_1](tallier1), TallyRequest[T_TALLIER_2, B, A_2](tallier2), TallyRequest[T_TALLIER_3, B, A_3](tallier3), TallyRequest[T_TALLIER_4, B, A_4](tallier4), TallyRequest[T_TALLIER_5, B, A_5](tallier5), TallyRequest[T_TALLIER_6, B, A_6](tallier6))).flatMap { bundle =>
      BifunctorMonadError.fromEither {
        for {
          tally1 <- bundle.getTallySafe[T_TALLIER_1, A_1](tallier1)(t1)
          tally2 <- bundle.getTallySafe[T_TALLIER_2, A_2](tallier2)(t2)
          tally3 <- bundle.getTallySafe[T_TALLIER_3, A_3](tallier3)(t3)
          tally4 <- bundle.getTallySafe[T_TALLIER_4, A_4](tallier4)(t4)
          tally5 <- bundle.getTallySafe[T_TALLIER_5, A_5](tallier5)(t5)
          tally6 <- bundle.getTallySafe[T_TALLIER_6, A_6](tallier6)(t6)
        } yield (tally1, tally2, tally3, tally4, tally5, tally6)
      }.leftMap(FetchTally.Error)
    }

  override def fetchTally7[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, T_TALLIER_6, T_TALLIER_7, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag, A_6 : Monoid : TypeTag, A_7 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5, tallier6: T_TALLIER_6, tallier7: T_TALLIER_7)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5], t6: Tallier[T_TALLIER_6, B, A_6], t7: Tallier[T_TALLIER_7, B, A_7]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5, A_6, A_7)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T_TALLIER_1, B, A_1](tallier1), TallyRequest[T_TALLIER_2, B, A_2](tallier2), TallyRequest[T_TALLIER_3, B, A_3](tallier3), TallyRequest[T_TALLIER_4, B, A_4](tallier4), TallyRequest[T_TALLIER_5, B, A_5](tallier5), TallyRequest[T_TALLIER_6, B, A_6](tallier6), TallyRequest[T_TALLIER_7, B, A_7](tallier7))).flatMap { bundle =>
      BifunctorMonadError.fromEither {
        for {
          tally1 <- bundle.getTallySafe[T_TALLIER_1, A_1](tallier1)(t1)
          tally2 <- bundle.getTallySafe[T_TALLIER_2, A_2](tallier2)(t2)
          tally3 <- bundle.getTallySafe[T_TALLIER_3, A_3](tallier3)(t3)
          tally4 <- bundle.getTallySafe[T_TALLIER_4, A_4](tallier4)(t4)
          tally5 <- bundle.getTallySafe[T_TALLIER_5, A_5](tallier5)(t5)
          tally6 <- bundle.getTallySafe[T_TALLIER_6, A_6](tallier6)(t6)
          tally7 <- bundle.getTallySafe[T_TALLIER_7, A_7](tallier7)(t7)
        } yield (tally1, tally2, tally3, tally4, tally5, tally6, tally7)
      }.leftMap(FetchTally.Error)
    }

  override def fetchTally8[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, T_TALLIER_6, T_TALLIER_7, T_TALLIER_8, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag, A_6 : Monoid : TypeTag, A_7 : Monoid : TypeTag, A_8 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5, tallier6: T_TALLIER_6, tallier7: T_TALLIER_7, tallier8: T_TALLIER_8)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5], t6: Tallier[T_TALLIER_6, B, A_6], t7: Tallier[T_TALLIER_7, B, A_7], t8: Tallier[T_TALLIER_8, B, A_8]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5, A_6, A_7, A_8)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T_TALLIER_1, B, A_1](tallier1), TallyRequest[T_TALLIER_2, B, A_2](tallier2), TallyRequest[T_TALLIER_3, B, A_3](tallier3), TallyRequest[T_TALLIER_4, B, A_4](tallier4), TallyRequest[T_TALLIER_5, B, A_5](tallier5), TallyRequest[T_TALLIER_6, B, A_6](tallier6), TallyRequest[T_TALLIER_7, B, A_7](tallier7), TallyRequest[T_TALLIER_8, B, A_8](tallier8))).flatMap { bundle =>
      BifunctorMonadError.fromEither {
        for {
          tally1 <- bundle.getTallySafe[T_TALLIER_1, A_1](tallier1)(t1)
          tally2 <- bundle.getTallySafe[T_TALLIER_2, A_2](tallier2)(t2)
          tally3 <- bundle.getTallySafe[T_TALLIER_3, A_3](tallier3)(t3)
          tally4 <- bundle.getTallySafe[T_TALLIER_4, A_4](tallier4)(t4)
          tally5 <- bundle.getTallySafe[T_TALLIER_5, A_5](tallier5)(t5)
          tally6 <- bundle.getTallySafe[T_TALLIER_6, A_6](tallier6)(t6)
          tally7 <- bundle.getTallySafe[T_TALLIER_7, A_7](tallier7)(t7)
          tally8 <- bundle.getTallySafe[T_TALLIER_8, A_8](tallier8)(t8)
        } yield (tally1, tally2, tally3, tally4, tally5, tally6, tally7, tally8)
      }.leftMap(FetchTally.Error)
    }

  override def fetchTally9[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, T_TALLIER_6, T_TALLIER_7, T_TALLIER_8, T_TALLIER_9, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag, A_6 : Monoid : TypeTag, A_7 : Monoid : TypeTag, A_8 : Monoid : TypeTag, A_9 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5, tallier6: T_TALLIER_6, tallier7: T_TALLIER_7, tallier8: T_TALLIER_8, tallier9: T_TALLIER_9)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5], t6: Tallier[T_TALLIER_6, B, A_6], t7: Tallier[T_TALLIER_7, B, A_7], t8: Tallier[T_TALLIER_8, B, A_8], t9: Tallier[T_TALLIER_9, B, A_9]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5, A_6, A_7, A_8, A_9)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T_TALLIER_1, B, A_1](tallier1), TallyRequest[T_TALLIER_2, B, A_2](tallier2), TallyRequest[T_TALLIER_3, B, A_3](tallier3), TallyRequest[T_TALLIER_4, B, A_4](tallier4), TallyRequest[T_TALLIER_5, B, A_5](tallier5), TallyRequest[T_TALLIER_6, B, A_6](tallier6), TallyRequest[T_TALLIER_7, B, A_7](tallier7), TallyRequest[T_TALLIER_8, B, A_8](tallier8), TallyRequest[T_TALLIER_9, B, A_9](tallier9))).flatMap { bundle =>
      BifunctorMonadError.fromEither {
        for {
          tally1 <- bundle.getTallySafe[T_TALLIER_1, A_1](tallier1)(t1)
          tally2 <- bundle.getTallySafe[T_TALLIER_2, A_2](tallier2)(t2)
          tally3 <- bundle.getTallySafe[T_TALLIER_3, A_3](tallier3)(t3)
          tally4 <- bundle.getTallySafe[T_TALLIER_4, A_4](tallier4)(t4)
          tally5 <- bundle.getTallySafe[T_TALLIER_5, A_5](tallier5)(t5)
          tally6 <- bundle.getTallySafe[T_TALLIER_6, A_6](tallier6)(t6)
          tally7 <- bundle.getTallySafe[T_TALLIER_7, A_7](tallier7)(t7)
          tally8 <- bundle.getTallySafe[T_TALLIER_8, A_8](tallier8)(t8)
          tally9 <- bundle.getTallySafe[T_TALLIER_9, A_9](tallier9)(t9)
        } yield (tally1, tally2, tally3, tally4, tally5, tally6, tally7, tally8, tally9)
      }.leftMap(FetchTally.Error)
    }

  override def fetchTally10[B, T_TALLIER_1, T_TALLIER_2, T_TALLIER_3, T_TALLIER_4, T_TALLIER_5, T_TALLIER_6, T_TALLIER_7, T_TALLIER_8, T_TALLIER_9, T_TALLIER_10, A_1 : Monoid : TypeTag, A_2 : Monoid : TypeTag, A_3 : Monoid : TypeTag, A_4 : Monoid : TypeTag, A_5 : Monoid : TypeTag, A_6 : Monoid : TypeTag, A_7 : Monoid : TypeTag, A_8 : Monoid : TypeTag, A_9 : Monoid : TypeTag, A_10 : Monoid : TypeTag](ballots: fs2.Stream[F[Throwable, +?], B])(tallier1: T_TALLIER_1, tallier2: T_TALLIER_2, tallier3: T_TALLIER_3, tallier4: T_TALLIER_4, tallier5: T_TALLIER_5, tallier6: T_TALLIER_6, tallier7: T_TALLIER_7, tallier8: T_TALLIER_8, tallier9: T_TALLIER_9, tallier10: T_TALLIER_10)(implicit t1: Tallier[T_TALLIER_1, B, A_1], t2: Tallier[T_TALLIER_2, B, A_2], t3: Tallier[T_TALLIER_3, B, A_3], t4: Tallier[T_TALLIER_4, B, A_4], t5: Tallier[T_TALLIER_5, B, A_5], t6: Tallier[T_TALLIER_6, B, A_6], t7: Tallier[T_TALLIER_7, B, A_7], t8: Tallier[T_TALLIER_8, B, A_8], t9: Tallier[T_TALLIER_9, B, A_9], t10: Tallier[T_TALLIER_10, B, A_10]): F[FetchTally.Error, (A_1, A_2, A_3, A_4, A_5, A_6, A_7, A_8, A_9, A_10)] =
    fetchTalliesUnsafe(ballots, TallyRequests(TallyRequest[T_TALLIER_1, B, A_1](tallier1), TallyRequest[T_TALLIER_2, B, A_2](tallier2), TallyRequest[T_TALLIER_3, B, A_3](tallier3), TallyRequest[T_TALLIER_4, B, A_4](tallier4), TallyRequest[T_TALLIER_5, B, A_5](tallier5), TallyRequest[T_TALLIER_6, B, A_6](tallier6), TallyRequest[T_TALLIER_7, B, A_7](tallier7), TallyRequest[T_TALLIER_8, B, A_8](tallier8), TallyRequest[T_TALLIER_9, B, A_9](tallier9), TallyRequest[T_TALLIER_10, B, A_10](tallier10))).flatMap { bundle =>
      BifunctorMonadError.fromEither {
        for {
          tally1 <- bundle.getTallySafe[T_TALLIER_1, A_1](tallier1)(t1)
          tally2 <- bundle.getTallySafe[T_TALLIER_2, A_2](tallier2)(t2)
          tally3 <- bundle.getTallySafe[T_TALLIER_3, A_3](tallier3)(t3)
          tally4 <- bundle.getTallySafe[T_TALLIER_4, A_4](tallier4)(t4)
          tally5 <- bundle.getTallySafe[T_TALLIER_5, A_5](tallier5)(t5)
          tally6 <- bundle.getTallySafe[T_TALLIER_6, A_6](tallier6)(t6)
          tally7 <- bundle.getTallySafe[T_TALLIER_7, A_7](tallier7)(t7)
          tally8 <- bundle.getTallySafe[T_TALLIER_8, A_8](tallier8)(t8)
          tally9 <- bundle.getTallySafe[T_TALLIER_9, A_9](tallier9)(t9)
          tally10 <- bundle.getTallySafe[T_TALLIER_10, A_10](tallier10)(t10)
        } yield (tally1, tally2, tally3, tally4, tally5, tally6, tally7, tally8, tally9, tally10)
      }.leftMap(FetchTally.Error)
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
    tallyRequests.requests.map { case TallyRequest(tallier, tallierInstance, valueTypeTag, valueMonoid) =>
      val tallyValue = tallierInstance.tallyAll(tallier)(ballots)

      tallier -> TallyBundle.Value(tallyValue, valueTypeTag, valueMonoid)
    }.toMap
  }

}

object FetchTallyImpl {

  import au.id.tmm.ausvotes.core.tallying.impl.FetchTallyImpl.TallyBundle.UnknownTallier

  private final case class TallyRequest[T_TALLIER, B, A](tallier: T_TALLIER, tallierInstance: Tallier[T_TALLIER, B, A], valueTypeTag: TypeTag[A], valueMonoid: Monoid[A])

  private object TallyRequest {
    def apply[T_TALLIER, B, A : Monoid : TypeTag](tallier: T_TALLIER)(implicit tallierInstance: Tallier[T_TALLIER, B, A]): TallyRequest[T_TALLIER, B, A] = TallyRequest(tallier, tallierInstance, implicitly[TypeTag[A]], implicitly[Monoid[A]])
  }

  private final case class TallyRequests[B](requests: List[TallyRequest[_, B, _]])

  private object TallyRequests {
    def apply[B](requests: TallyRequest[_, B, _]*): TallyRequests[B] = TallyRequests(requests.toList)
  }

  private final case class TallyBundle[B](underlying: Map[UnknownTallier, TallyBundle.Value[_]]) {
    def getTallySafe[T_TALLIER, A : WeakTypeTag](tallier: T_TALLIER)(tallierInstance: Tallier[T_TALLIER, B, A]): Either[TallyBundle.GetTallyError, A] =
      for {
        value <- underlying.get(tallier)
          .toRight(TallyBundle.GetTallyError.NoTallyForKey)

        expectedTypeTag = implicitly[WeakTypeTag[A]]
        actualTypeTag = value.valueTag

        _ <- if (actualTypeTag.tpe.<:<(expectedTypeTag.tpe)) Right(Unit) else Left(TallyBundle.GetTallyError.TypeError(expectedTypeTag, actualTypeTag))
      } yield value.value.asInstanceOf[A]
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

    final case class Value[A](value: A, valueTag: TypeTag[A], monoidForValueType: Monoid[A])

    object Value {
      implicit def isAMonoid[A : Monoid : TypeTag]: Monoid[Value[A]] = new Monoid[Value[A]] {
        override def empty: Value[A] = Value(Monoid[A].empty, implicitly[TypeTag[A]], Monoid[A])

        override def combine(left: Value[A], right: Value[A]): Value[A] =
          Value(Monoid.combine(left.value, right.value), implicitly[TypeTag[A]], Monoid[A])
      }
    }

    sealed abstract class GetTallyError extends ExceptionCaseClass

    object GetTallyError {
      final case class TypeError(expectedTypeTag: WeakTypeTag[_], actualTypeTag: TypeTag[_]) extends GetTallyError
      final case object NoTallyForKey extends GetTallyError
    }
  }

}

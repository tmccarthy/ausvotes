package au.id.tmm.ausvotes.shared.io.typeclasses

import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.Concurrent.Fibre
import scalaz.zio

trait Concurrent[F[+_, +_]] extends SyncEffects[F] {

  def start[E, A](fea: F[E, A]): F[Nothing, Fibre[F, E, A]]

  def racePair[E, E1 >: E, A, B](left: F[E, A], right: F[E1, B]): F[E1, Either[(A, Fibre[F, E1, B]), (Fibre[F, E, A], B)]]

  def par[E, E1 >: E, A, B](left: F[E, A], right: F[E1, B]): F[E1, (A, B)]

  def async[E, A](k: (F[E, A] => Unit) => Unit): F[E, A]

  def asyncF[E, A](k: (F[E, A] => Unit) => F[Nothing, Unit]): F[E, A]

  def cancelable[E, A](k: (Either[E, A] => Unit) => F[Nothing, Unit]): F[E, A]

  def race[E, A, B](fa: F[E, A], fb: F[E, B]): F[E, Either[A, B]]

}

object Concurrent {

  def apply[F[+_, +_] : Concurrent]: Concurrent[F] = implicitly[Concurrent[F]]

  trait Fibre[F[+_, +_], E, A] {

    def cancel: F[Nothing, zio.ExitResult[E, A]]

    def join: F[E, A]

  }

  def par10[F[+_, +_] : Concurrent, E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10]
  (
    fetchTally1: F[E, T1],
    fetchTally2: F[E, T2],
    fetchTally3: F[E, T3],
    fetchTally4: F[E, T4],
    fetchTally5: F[E, T5],
    fetchTally6: F[E, T6],
    fetchTally7: F[E, T7],
    fetchTally8: F[E, T8],
    fetchTally9: F[E, T9],
    fetchTally10: F[E, T10],
  ): F[E, (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      concurrentInstance.par(fetchTally2,
        concurrentInstance.par(fetchTally3,
          concurrentInstance.par(fetchTally4,
            concurrentInstance.par(fetchTally5,
              concurrentInstance.par(fetchTally6,
                concurrentInstance.par(fetchTally7,
                  concurrentInstance.par(fetchTally8,
                    concurrentInstance.par(fetchTally9,
                      fetchTally10))))))))).map {
      case
        (tally1,
        (tally2,
        (tally3,
        (tally4,
        (tally5,
        (tally6,
        (tally7,
        (tally8,
        (tally9,
        tally10,
          ))))))))) => (
        tally1,
        tally2,
        tally3,
        tally4,
        tally5,
        tally6,
        tally7,
        tally8,
        tally9,
        tally10,
      )
    }
  }

  def par8[F[+_, +_] : Concurrent, E, T1, T2, T3, T4, T5, T6, T7, T8]
  (
    fetchTally1: F[E, T1],
    fetchTally2: F[E, T2],
    fetchTally3: F[E, T3],
    fetchTally4: F[E, T4],
    fetchTally5: F[E, T5],
    fetchTally6: F[E, T6],
    fetchTally7: F[E, T7],
    fetchTally8: F[E, T8],
  ): F[E, (T1, T2, T3, T4, T5, T6, T7, T8)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      concurrentInstance.par(fetchTally2,
        concurrentInstance.par(fetchTally3,
          concurrentInstance.par(fetchTally4,
            concurrentInstance.par(fetchTally5,
              concurrentInstance.par(fetchTally6,
                concurrentInstance.par(fetchTally7,
                  fetchTally8))))))).map {
      case
        (tally1,
        (tally2,
        (tally3,
        (tally4,
        (tally5,
        (tally6,
        (tally7,
        tally8
          ))))))) => (
        tally1,
        tally2,
        tally3,
        tally4,
        tally5,
        tally6,
        tally7,
        tally8,
      )
    }
  }

  def par6[F[+_, +_] : Concurrent, E, T1, T2, T3, T4, T5, T6]
  (
    fetchTally1: F[E, T1],
    fetchTally2: F[E, T2],
    fetchTally3: F[E, T3],
    fetchTally4: F[E, T4],
    fetchTally5: F[E, T5],
    fetchTally6: F[E, T6],
  ): F[E, (T1, T2, T3, T4, T5, T6)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      concurrentInstance.par(fetchTally2,
        concurrentInstance.par(fetchTally3,
          concurrentInstance.par(fetchTally4,
            concurrentInstance.par(fetchTally5,
              fetchTally6))))).map {
      case
        (tally1,
        (tally2,
        (tally3,
        (tally4,
        (tally5,
        tally6))))) => (
        tally1,
        tally2,
        tally3,
        tally4,
        tally5,
        tally6,
      )
    }
  }

  def par4[F[+_, +_] : Concurrent, E, T1, T2, T3, T4]
  (
    fetchTally1: F[E, T1],
    fetchTally2: F[E, T2],
    fetchTally3: F[E, T3],
    fetchTally4: F[E, T4],
  ): F[E, (T1, T2, T3, T4)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      concurrentInstance.par(fetchTally2,
        concurrentInstance.par(fetchTally3,
          fetchTally4))).map {
      case
        (tally1,
        (tally2,
        (tally3,
        tally4))) => (
        tally1,
        tally2,
        tally3,
        tally4,
      )
    }
  }

  def par3[F[+_, +_] : Concurrent, E, T1, T2, T3]
  (
    fetchTally1: F[E, T1],
    fetchTally2: F[E, T2],
    fetchTally3: F[E, T3],
  ): F[E, (T1, T2, T3)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      concurrentInstance.par(fetchTally2,
        fetchTally3)).map {
      case
        (tally1,
        (tally2,
        tally3)) => (
        tally1,
        tally2,
        tally3,
      )
    }
  }

  def par2[F[+_, +_] : Concurrent, E, T1, T2]
  (
    fetchTally1: F[E, T1],
    fetchTally2: F[E, T2],
  ): F[E, (T1, T2)] = {
    val concurrentInstance = Concurrent[F]

    concurrentInstance.par(fetchTally1,
      fetchTally2).map {
      case
        (tally1,
        tally2) => (
        tally1,
        tally2,
      )
    }
  }

}

package au.id.tmm.ausvotes.shared.io.typeclasses

import au.id.tmm.ausvotes.shared.io.typeclasses.Concurrent.Fibre
import scalaz.zio

trait Concurrent[F[+_, +_]] extends SyncEffects[F] {

  def start[E, A](fea: F[E, A]): F[Nothing, Fibre[F, E, A]]

  def racePair[E, E1 >: E, A, B](left: F[E, A], right: F[E1, B]): F[E1, Either[(A, Fibre[F, E1, B]), (Fibre[F, E, A], B)]]

  def par[E, E1 >: E, A, B](left: F[E, A], right: F[E1, B]): F[E1, (A, B)]

  def async[E, A](k: (F[E, A] => Unit) => Unit): F[E, A]

  def asyncF[E, A](k: (F[E, A] => Unit) => F[Nothing, Unit]): F[E, A]

}

object Concurrent {

  def apply[F[+_, +_] : Concurrent]: Concurrent[F] = implicitly[Concurrent[F]]

  trait Fibre[F[+_, +_], E, A] {

    def cancel: F[Nothing, zio.ExitResult[E, A]]

    def join: F[E, A]

  }

}

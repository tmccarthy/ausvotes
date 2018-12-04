package au.id.tmm.ausvotes.shared.io.instances

import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError

import scala.annotation.tailrec

object EitherInstances {

  implicit val eitherIsABifunctorMonadError: BifunctorMonadError[Either] = new BifunctorMonadError[Either] {

    override def handleErrorWith[E, A, E1](fea: Either[E, A])(f: E => Either[E1, A]): Either[E1, A] = fea match {
      case Right(a) => Right(a)
      case Left(e) => f(e)
    }

    override def pure[A](a: A): Either[Nothing, A] = Right(a)

    override def leftPure[E](e: E): Either[E, Nothing] = Left(e)

    override def map[E, A, B](fea: Either[E, A])(fab: A => B): Either[E, B] = fea.map(fab)

    override def leftMap[A, B, C](fab: Either[A, B])(f: A => C): Either[C, B] = fab.left.map(f)

    override def bimap[A, B, C, D](fab: Either[A, B])(f: A => C, g: B => D): Either[C, D] = fab.left.map(f).right.map(g)

    override def flatMap[E1, E2 >: E1, A, B](fe1a: Either[E1, A])(fafe2b: A => Either[E2, B]): Either[E2, B] = fe1a.flatMap(fafe2b)

    @tailrec
    override def tailRecM[E, A, A1](a: A)(f: A => Either[E, Either[A, A1]]): Either[E, A1] = f(a) match {
      case Right(Right(a)) => pure(a)
      case Right(Left(e)) => tailRecM(e)(f)
      case Left(e) => Left(e)
    }

  }

}

package au.id.tmm.ausvotes.shared.io.test

import au.id.tmm.ausvotes.shared.io.test.TestIO.Output
import au.id.tmm.ausvotes.shared.io.typeclasses.{Parallel, SyncEffects}
import cats.effect.ExitCase

import scala.util.control.NonFatal

final case class TestIO[D, +E, +A](run: D => Output[D, E, A]) {
  def map[B](f: A => B): TestIO[D, E, B] = {
    val newRun = run andThen {
      case Output(newData, result) => Output(newData, result.map(f))
    }

    TestIO(newRun)
  }

  def leftMap[E2](f: E => E2): TestIO[D, E2, A] = {
    val newRun = run andThen {
      case Output(newData, result) => Output(newData, result.left.map(f))
    }

    TestIO(newRun)
  }

  def flatMap[E2 >: E, B](f: A => TestIO[D, E2, B]): TestIO[D, E2, B] = this.map(f).flatten

  def resultGiven(testData: D): Either[E, A] = run(testData).result

  def testDataGiven(initialTestData: D): D = run(initialTestData).testData

}

object TestIO {
  final case class Output[D, +E, +A](testData: D, result: Either[E, A])

  def pure[A, D](a: A): TestIO[D, Nothing, A] = TestIO(data => Output(data, Right(a)))
  def leftPure[E, D](e: E): TestIO[D, E, Nothing] = TestIO(data => Output(data, Left(e)))

  implicit class NewTestIOFlattenOps[E1, E2 >: E1, A, D](testIO: TestIO[D, E1, TestIO[D, E2, A]]) {
    def flatten: TestIO[D, E2, A] = {
      val oldRun = testIO.run
      val newRun: D => Output[D, E2, A] = oldRun andThen {
        case Output(data, Right(newTestIO)) => newTestIO.run(data)
        case Output(data, Left(error)) => Output(data, Left(error))
      }

      TestIO(newRun)
    }
  }

  implicit def testIOIsABME[D]: SyncEffects[TestIO[D, +?, +?]] = new SyncEffects[TestIO[D, +?, +?]] {
    override def pure[A](a: A): TestIO[D, Nothing, A] = TestIO.pure(a)
    override def leftPure[E](e: E): TestIO[D, E, Nothing] = TestIO.leftPure(e)
    override def flatten[E1, E2 >: E1, A](io: TestIO[D, E1, TestIO[D, E2, A]]): TestIO[D, E2, A] = io.flatten
    override def flatMap[E1, E2 >: E1, A, B](io: TestIO[D, E1, A])(fafe2b: A => TestIO[D, E2, B]): TestIO[D, E2, B] = io.flatMap(fafe2b)
    override def map[E, A, B](io: TestIO[D, E, A])(fab: A => B): TestIO[D, E, B] = io.map(fab)
    override def bimap[A, B, C, X](fab: TestIO[D, A, B])(f: A => C, g: B => X): TestIO[D, C, X] = fab.map(g).leftMap(f)

    override def attempt[E, A](io: TestIO[D, E, A]): TestIO[D, Nothing, Either[E, A]] = {
      val newRun = io.run andThen {
        case Output(data, result: Either[E, A]) => Output(data, Right(result))
      }

      TestIO(newRun)
    }

    override def absolve[E, A](io: TestIO[D, E, Either[E, A]]): TestIO[D, E, A] = io.flatMap {
      case Right(success) => TestIO.pure(success)
      case Left(error) => TestIO.leftPure(error)
    }

    override def handleErrorWith[E, A, E1](fea: TestIO[D, E, A])(f: E => TestIO[D, E1, A]): TestIO[D, E1, A] = {
      val newRun: D => Output[D, E1, A] = fea.run andThen {
        case Output(data, result) => {
          result match {
            case Right(value) => Output[D, E1, A](data, Right(value))
            case Left(failure) => f(failure).run(data)
          }
        }
      }

      TestIO(newRun)
    }

    /**
      * Keeps calling `f` until a `scala.util.Right[B]` is returned.
      */
    override def tailRecM[E, A, B](a: A)(f: A => TestIO[D, E, Either[A, B]]): TestIO[D, E, B] = f(a).flatMap {
      case Right(value) => TestIO.pure(value)
      case Left(value) => tailRecM(value)(f)
    }

    override def sync[A](effect: => A): TestIO[D, Nothing, A] = TestIO { data =>
      TestIO.Output(data, Right(effect))
    }

    override def syncException[A](effect: => A): TestIO[D, Exception, A] = TestIO { data =>
      val result = try Right(effect) catch {
        case e: Exception => Left(e)
      }

      TestIO.Output(data, result)
    }

    override def syncCatch[E, A](effect: => A)(f: PartialFunction[Throwable, E]): TestIO[D, E, A] = TestIO { data =>
      val result: Either[E, A] = try Right(effect) catch f andThen (e => Left(e))

      TestIO.Output(data, result)
    }

    override def syncThrowable[A](effect: => A): TestIO[D, Throwable, A] = TestIO { data =>
      val result = try Right(effect) catch {
        case e: Throwable => Left(e)
      }

      TestIO.Output(data, result)
    }


    override def bracket[E, A, B](
                                   acquire: TestIO[D, E, A],
                                 )(
                                   release: A => TestIO[D, Nothing, _],
                                 )(
                                   use: A => TestIO[D, E, B],
                                 ): TestIO[D, E, B] = TestIO { data =>
      val TestIO.Output(dataAfterAcquisition, acquired) = acquire.run(data)

      acquired match {
        case Right(aquired) => use(aquired).run(dataAfterAcquisition) match {
          case TestIO.Output(dataAfterUse, Right(resultAfterUse)) =>
            release(aquired).map(_ => resultAfterUse).run(dataAfterUse)

          case TestIO.Output(dataAfterUse, Left(error)) =>
            release(aquired).flatMap(_ => TestIO.leftPure(error)).run(dataAfterUse)
        }
        case Left(acquisitionFailure) => TestIO.Output(dataAfterAcquisition, Left(acquisitionFailure))
      }
    }

    override def bracketCase[A, B](
                                    acquire: TestIO[D, Throwable, A],
                                  )(
                                    use: A => TestIO[D, Throwable, B],
                                  )(
                                    release: (A, ExitCase[Throwable]) => TestIO[D, Throwable, Unit],
                                  ): TestIO[D, Throwable, B] = TestIO { data =>
      val TestIO.Output(dataAfterAcquisition, result) = acquire.run(data)

      val safelyAquired: Either[Throwable, A] = result.left.map {
        case NonFatal(t) => t
        case t => throw t
      }

      safelyAquired match {
        case Right(aquired) => use(aquired).run(dataAfterAcquisition) match {
          case TestIO.Output(dataAfterUse, Right(resultAfterUse)) =>
            release(aquired, ExitCase.Completed).map(_ => resultAfterUse).run(dataAfterUse)

          case TestIO.Output(dataAfterUse, Left(error)) =>
            release(aquired, ExitCase.Error(error)).flatMap(_ => TestIO.leftPure(error)).run(dataAfterUse)
        }
        case Left(acquisitionFailure) => TestIO.Output(dataAfterAcquisition, Left(acquisitionFailure))
      }
    }
  }

  implicit def testIOIsParallel[D]: Parallel[TestIO[D, +?, +?]] = new Parallel[TestIO[D, +?, +?]] {
    override def par[E1, E2 >: E1, A, B](left: TestIO[D, E1, A], right: TestIO[D, E2, B]): TestIO[D, E2, (A, B)] =
      for {
        leftResult <- left
        rightResult <- right
      } yield (leftResult, rightResult)

    override def parAll[E, A](as: Iterable[TestIO[D, E, A]]): TestIO[D, E, List[A]] = parTraverse(as)(identity)

    override def parTraverse[E, A, B](as: Iterable[A])(f: A => TestIO[D, E, B]): TestIO[D, E, List[B]] =
      as.foldRight[TestIO[D, E, List[B]]](TestIO.pure(Nil)) { (a, io) =>
        par(f(a), io).map { case (b, bs) => b :: bs }
      }
  }
}

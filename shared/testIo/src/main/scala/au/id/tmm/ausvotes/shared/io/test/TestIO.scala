package au.id.tmm.ausvotes.shared.io.test

import au.id.tmm.ausvotes.shared.io.test.TestIO.Output
import au.id.tmm.ausvotes.shared.io.typeclasses.{Monad, Parallel}

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

  implicit def testIOIsAMonad[D]: Monad[TestIO[D, +?, +?]] = new Monad[TestIO[D, +?, +?]] {
    override def pure[A](a: A): TestIO[D, Nothing, A] = TestIO.pure(a)
    override def leftPure[E](e: E): TestIO[D, E, Nothing] = TestIO.leftPure(e)
    override def flatten[E1, E2 >: E1, A](io: TestIO[D, E1, TestIO[D, E2, A]]): TestIO[D, E2, A] = io.flatten
    override def flatMap[E1, E2 >: E1, A, B](io: TestIO[D, E1, A])(fafe2b: A => TestIO[D, E2, B]): TestIO[D, E2, B] = io.flatMap(fafe2b)
    override def map[E, A, B](io: TestIO[D, E, A])(fab: A => B): TestIO[D, E, B] = io.map(fab)
    override def leftMap[E1, E2, A](io: TestIO[D, E1, A])(fe1e2: E1 => E2): TestIO[D, E2, A] = io.leftMap(fe1e2)

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

    override def catchLeft[E, A, E1 >: E, A1 >: A](fea: TestIO[D, E, A], pf: PartialFunction[E, TestIO[D, E1, A1]]): TestIO[D, E1, A1] = {
      val newRun: D => Output[D, E1, A1] = fea.run andThen {
        case Output(data, result) => {
          result match {
            case Left(failure) => pf.applyOrElse[E, TestIO[D, E1, A1]](failure, TestIO.leftPure).run(data)
            case Right(_) => Output(data, result)
          }
        }
      }

      TestIO(newRun)
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

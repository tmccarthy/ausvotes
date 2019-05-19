package au.id.tmm.ausvotes.shared.io.test

import au.id.tmm.ausvotes.shared.io.test.TestIO.Output
import au.id.tmm.bfect
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.{ExitCase, Failure}

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

  implicit def testIOIsABME[D]: Sync[TestIO[D, +?, +?]] = new Sync[TestIO[D, +?, +?]] {

    override def rightPure[A](a: A): TestIO[D, Nothing, A] = TestIO.pure(a)

    override def leftPure[E](e: E): TestIO[D, E, Nothing] = TestIO.leftPure(e)

    override def flatMap[E1, E2 >: E1, A, B](fe1a: TestIO[D, E1, A])(fafe2b: A => TestIO[D, E2, B]): TestIO[D, E2, B] = fe1a.flatMap(fafe2b)

    override def biMap[L1, R1, L2, R2](f: TestIO[D, L1, R1])(leftF: L1 => L2, rightF: R1 => R2): TestIO[D, L2, R2] = f.map(rightF).leftMap(leftF)

    override def suspend[E, A](effect: => TestIO[D, E, A]): TestIO[D, E, A] = TestIO[D, E, A](data => effect.run(data))

    override def bracketCase[R, E, A](acquire: TestIO[D, E, R])(release: (R, bfect.ExitCase[E, A]) => TestIO[D, Nothing, _])(use: R => TestIO[D, E, A]): TestIO[D, E, A] = TestIO { data =>
      val TestIO.Output(dataAfterAcquisition, result) = acquire.run(data)

      result match {
        case Right(aquired) => use(aquired).run(dataAfterAcquisition) match {
          case TestIO.Output(dataAfterUse, Right(resultAfterUse)) =>
            release(aquired, ExitCase.Succeeded(resultAfterUse)).map(_ => resultAfterUse).run(dataAfterUse)

          case TestIO.Output(dataAfterUse, Left(error)) =>
            release(aquired, ExitCase.Failed(Failure.Checked(error))).flatMap(_ => TestIO.leftPure(error)).run(dataAfterUse)
        }
        case Left(acquisitionFailure) => TestIO.Output(dataAfterAcquisition, Left(acquisitionFailure))
      }
    }

    override def handleErrorWith[E1, A, E2](fea: TestIO[D, E1, A])(f: E1 => TestIO[D, E2, A]): TestIO[D, E2, A] = {
      val newRun: D => Output[D, E2, A] = fea.run andThen {
        case Output(data, result) => {
          result match {
            case Right(value) => Output[D, E2, A](data, Right(value))
            case Left(failure) => f(failure).run(data)
          }
        }
      }

      TestIO(newRun)
    }

    override def tailRecM[E, A, A1](a: A)(f: A => TestIO[D, E, Either[A, A1]]): TestIO[D, E, A1] = f(a).flatMap {
      case Right(value) => TestIO.pure(value)
      case Left(value) => tailRecM(value)(f)
    }
  }
}

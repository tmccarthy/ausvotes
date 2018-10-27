package au.id.tmm.ausvotes.shared.io.test

import java.time.{Instant, LocalDate, ZoneId, ZonedDateTime}

import au.id.tmm.ausvotes.shared.io.actions
import au.id.tmm.ausvotes.shared.io.actions.{Log, Now}
import au.id.tmm.ausvotes.shared.io.test.datatraits.{CurrentTime, EnvVars, Logging, Resources}
import au.id.tmm.ausvotes.shared.io.typeclasses.{Attempt, Monad, Parallel}

final case class TestIO[+E, +A, D](run: D => (D, Either[E, A])) {
  def map[B](f: A => B): TestIO[E, B, D] = {
    val newRun = run andThen {
      case (newData, result) => (newData, result.map(f))
    }

    TestIO(newRun)
  }

  def leftMap[E2](f: E => E2): TestIO[E2, A, D] = {
    val newRun = run andThen {
      case (newData, result) => (newData, result.left.map(f))
    }

    TestIO(newRun)
  }

  def flatMap[E2 >: E, B](f: A => TestIO[E2, B, D]): TestIO[E2, B, D] = this.map(f).flatten
}

object TestIO {

  def pure[A, D](a: A): TestIO[Nothing, A, D] = TestIO(data => (data, Right(a)))
  def leftPure[E, D](e: E): TestIO[E, Nothing, D] = TestIO(data => (data, Left(e)))

  implicit class TestIOFlattenOps[E1, E2 >: E1, A, D](testIO: TestIO[E1, TestIO[E2, A, D], D]) {
    def flatten: TestIO[E2, A, D] = {
      val oldRun = testIO.run
      val newRun = oldRun andThen {
        case (data, Right(newTestIO)) => newTestIO.run(data)
        case (data, Left(error)) => (data, Left(error))
      }

      TestIO(newRun)
    }
  }

  implicit def testIOIsAMonad[D]: Monad[TestIO[+?, +?, D]] = new Monad[TestIO[+?, +?, D]] {
    override def pure[A](a: A): TestIO[Nothing, A, D] = TestIO.pure(a)
    override def leftPure[E](e: E): TestIO[E, Nothing, D] = TestIO.leftPure(e)
    override def flatten[E1, E2 >: E1, A](io: TestIO[E1, TestIO[E2, A, D], D]): TestIO[E2, A, D] = io.flatten
    override def flatMap[E1, E2 >: E1, A, B](io: TestIO[E1, A, D])(fafe2b: A => TestIO[E2, B, D]): TestIO[E2, B, D] = io.flatMap(fafe2b)
    override def map[E, A, B](io: TestIO[E, A, D])(fab: A => B): TestIO[E, B, D] = io.map(fab)
    override def leftMap[E1, E2, A](io: TestIO[E1, A, D])(fe1e2: E1 => E2): TestIO[E2, A, D] = io.leftMap(fe1e2)
  }

  implicit def testIOAllowsLogging[D <: Logging[D]]: Log[TestIO[+?, +?, D]] = new Log[TestIO[+?, +?, D]] {
    override def logError(loggedEvent: Log.LoggedEvent): TestIO[Nothing, Unit, D] = log(Log.Level.Error, loggedEvent)
    override def logWarn(loggedEvent: Log.LoggedEvent): TestIO[Nothing, Unit, D] = log(Log.Level.Warn, loggedEvent)
    override def logInfo(loggedEvent: Log.LoggedEvent): TestIO[Nothing, Unit, D] = log(Log.Level.Info, loggedEvent)
    override def logDebug(loggedEvent: Log.LoggedEvent): TestIO[Nothing, Unit, D] = log(Log.Level.Debug, loggedEvent)
    override def logTrace(loggedEvent: Log.LoggedEvent): TestIO[Nothing, Unit, D] = log(Log.Level.Trace, loggedEvent)

    override def log(level: Log.Level, event: Log.LoggedEvent): TestIO[Nothing, Unit, D] =
      TestIO(data => (data.log(level, event), Right(Unit)))
  }

  implicit def testIOProducesTheCurrentTime[D <: CurrentTime[D]]: Now[TestIO[+?, +?, D]] = new Now[TestIO[+?, +?, D]] {
    override def systemNanoTime: TestIO[Nothing, Long, D] = testIOWithTime(i => i.getEpochSecond * 1000000000 + i.getNano)
    override def currentTimeMillis: TestIO[Nothing, Long, D] = testIOWithTime(_.toEpochMilli)
    override def nowInstant: TestIO[Nothing, Instant, D] = testIOWithTime(identity)
    override def nowLocalDate: TestIO[Nothing, LocalDate, D] = testIOWithTime(LocalDate.from(_))
    override def nowZonedDateTime: TestIO[Nothing, ZonedDateTime, D] = testIOWithTime(ZonedDateTime.ofInstant(_, ZoneId.systemDefault()))

    private def testIOWithTime[A](fromInstant: Instant => A): TestIO[Nothing, A, D] =
      TestIO(data => data.increment match {
        case (instant, newData) => (newData, Right(fromInstant(instant)))
      })
  }

  implicit def testIOAllowsAttempts[D]: Attempt[TestIO[+?, +?, D]] = new Attempt[TestIO[+?, +?, D]] {
    override def attempt[E, A](io: TestIO[E, A, D]): TestIO[Nothing, Either[E, A], D] = {
      val newRun = io.run andThen {
        case (data, result: Either[E, A]) => (data, Right(result))
      }

      TestIO(newRun)
    }
  }

  implicit def testIOHasEnvVars[D <: EnvVars]: actions.EnvVars[TestIO[+?, +?, D]] = new actions.EnvVars[TestIO[+?, +?, D]] {
    override def envVars: TestIO[Nothing, Map[String, String], D] = TestIO(data => (data, Right(data.envVars)))
  }

  implicit def testIOHasResources[D <: Resources]: actions.Resources[TestIO[+?, +?, D]] = new actions.Resources[TestIO[+?, +?, D]] {
    override def resource(name: String): TestIO[Nothing, Option[String], D] = TestIO(data => (data, Right(data.resources.get(name))))
  }

  implicit def ioCanBeParallel[D]: Parallel[TestIO[+?, +?, D]] = new Parallel[TestIO[+?, +?, D]] {
    override def par[E1, E2 >: E1, A, B](left: TestIO[E1, A, D], right: TestIO[E2, B, D]): TestIO[E2, (A, B), D] =
      for {
        leftResult <- left
        rightResult <- right
      } yield (leftResult, rightResult)

    override def parAll[E, A](as: Iterable[TestIO[E, A, D]]): TestIO[E, List[A], D] = parTraverse(as)(identity)

    override def parTraverse[E, A, B](as: Iterable[A])(f: A => TestIO[E, B, D]): TestIO[E, List[B], D] =
      as.foldRight[TestIO[E, List[B], D]](TestIO.pure(Nil)) { (a, io) =>
        par(f(a), io).map { case (b, bs) => b :: bs }
      }
  }

}

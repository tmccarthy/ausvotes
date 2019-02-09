package au.id.tmm.ausvotes.shared.io.instances

import java.time.{Instant, LocalDate, ZonedDateTime}

import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.actions._
import au.id.tmm.ausvotes.shared.io.typeclasses.{Parallel, SyncEffects}
import cats.effect.ExitCase
import org.apache.commons.io.IOUtils
import org.slf4j
import org.slf4j.LoggerFactory
import scalaz.zio.ExitResult.Cause
import scalaz.zio.{ExitResult, IO}

import scala.util.Try

object ZIOInstances {

  implicit val zioIsABME: SyncEffects[IO] = new SyncEffects[IO] {

    override def handleErrorWith[E, A, E1](fea: IO[E, A])(f: E => IO[E1, A]): IO[E1, A] = fea.catchAll(f)

    override def pure[A](a: A): IO[Nothing, A] = IO.point(a)

    override def leftPure[E](e: E): IO[E, Nothing] = IO.fail(e)

    override def map[E, A, B](fea: IO[E, A])(fab: A => B): IO[E, B] = fea.map(fab)

    override def flatMap[E1, E2 >: E1, A, B](fe1a: IO[E1, A])(fafe2b: A => IO[E2, B]): IO[E2, B] = fe1a.flatMap(fafe2b)

    /**
      * Keeps calling `f` until a `scala.util.Right[B]` is returned.
      */
    override def tailRecM[E, A, A1](a: A)(f: A => IO[E, Either[A, A1]]): IO[E, A1] = f(a).flatMap {
      case Right(a1) => pure(a1)
      case Left(a) => tailRecM(a)(f)
    }

    override def recoverWith[E, A, E1 >: E](fea: IO[E, A])(pf: PartialFunction[E, IO[E1, A]]): IO[E1, A] = fea.catchSome(pf)

    override def attempt[E, A](fea: IO[E, A]): IO[Nothing, Either[E, A]] = fea.attempt

    override def absolve[E, A](feEitherEA: IO[E, Either[E, A]]): IO[E, A] = IO.absolve(feEitherEA)

    override def fromTry[A](t: Try[A]): IO[Throwable, A] = IO.fromTry(t)

    override def unit: IO[Nothing, Unit] = IO.unit

    override def fromEither[E, A](either: Either[E, A]): IO[E, A] = IO.fromEither(either)

    override def bimap[A, B, C, D](fab: IO[A, B])(f: A => C, g: B => D): IO[C, D] = fab.bimap(f, g)

    override def flatten[E1, E2 >: E1, A](fefa: IO[E1, IO[E2, A]]): IO[E2, A] = fefa.flatMap(identity)

    override def sync[A](effect: => A): IO[Nothing, A] = IO.sync(effect)

    override def syncException[A](effect: => A): IO[Exception, A] = IO.syncException(effect)

    override def syncCatch[E, A](effect: => A)(f: PartialFunction[Throwable, E]): IO[E, A] = IO.syncCatch(effect)(f)

    override def syncThrowable[A](effect: => A): IO[Throwable, A] = IO.syncThrowable(effect)

    override def bracket[E, A, B](acquire: IO[E, A])(release: A => IO[Nothing, _])(use: A => IO[E, B]): IO[E, B] = IO.bracket(acquire)(release)(use)

    override def bracketCase[A, B](acquire: IO[Throwable, A])(use: A => IO[Throwable, B])(release: (A, ExitCase[Throwable]) => IO[Throwable, Unit]): IO[Throwable, B] = {

      @scala.annotation.tailrec
      def convertZioFailureToCatsExitCase(exitResultCause: ExitResult.Cause[Throwable]): ExitCase[Throwable] =
        exitResultCause match {
          case Cause.Checked(e) => ExitCase.Error(e)
          case Cause.Unchecked(e) => ExitCase.Error(e)
          case Cause.Interruption => ExitCase.Canceled
          case Cause.Then(left, right) => convertZioFailureToCatsExitCase(left)
          case Cause.Both(left, right) => convertZioFailureToCatsExitCase(left)
        }

      def convertZioExitResultToCatsExitCase(exitResult: ExitResult[Throwable, _]): ExitCase[Throwable] =
        exitResult match {
          case ExitResult.Succeeded(value) => ExitCase.complete
          case ExitResult.Failed(cause) => convertZioFailureToCatsExitCase(cause)
        }

      IO.bracket0[Throwable, A, B](acquire)((a, zioExitResult) => {
        release(a, convertZioExitResultToCatsExitCase(zioExitResult)).leftMap(t => throw t)
      })(use)
    }
  }

  implicit val ioAccessesEnvVars: EnvVars[IO] = new EnvVars[IO] {
    override def envVars: IO[Nothing, Map[String, String]] = IO.sync(sys.env)
  }

  implicit val ioAccessesResources: Resources[IO] = new Resources[IO] {
    override def resource(name: String): IO[Nothing, Option[String]] = IO.sync {
      Option(IOUtils.toString(getClass.getResource(name), "UTF-8"))
    }
  }

  implicit val ioCanLog: Log[IO] = new Log[IO] {
    private val logger: slf4j.Logger = LoggerFactory.getLogger("au.id.tmm.ausvotes")

    override def logError(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.sync(logger.error(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logWarn(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.sync(logger.warn(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logInfo(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.sync(logger.info(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logDebug(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.sync(logger.debug(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logTrace(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.sync(logger.trace(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

  }

  implicit val ioCanDetermineNow: Now[IO] = new Now[IO] {
    override def systemNanoTime: IO[Nothing, Long] = IO.sync(System.nanoTime())

    override def currentTimeMillis: IO[Nothing, Long] = IO.sync(System.currentTimeMillis())

    override def nowInstant: IO[Nothing, Instant] = IO.sync(Instant.now())

    override def nowLocalDate: IO[Nothing, LocalDate] = IO.sync(LocalDate.now())

    override def nowZonedDateTime: IO[Nothing, ZonedDateTime] = IO.sync(ZonedDateTime.now())
  }

  implicit val ioCanBeParallel: Parallel[IO] = new Parallel[IO] {
    override def par[E1, E2 >: E1, A, B](left: IO[E1, A], right: IO[E2, B]): IO[E2, (A, B)] = left par right

    override def parAll[E, A](as: Iterable[IO[E, A]]): IO[E, List[A]] = IO.parAll(as)

    override def parTraverse[E, A, B](as: Iterable[A])(f: A => IO[E, B]): IO[E, List[B]] = IO.parTraverse[E, A, B](as)(f)
  }

  implicit val zioHasAConsole: Console[IO] = new Console[IO] {
    override def print(string: String): IO[Nothing, Unit] = IO.sync(scala.Console.print(string))

    override def println(string: String): IO[Nothing, Unit] = IO.sync(scala.Console.println(string))
  }

}

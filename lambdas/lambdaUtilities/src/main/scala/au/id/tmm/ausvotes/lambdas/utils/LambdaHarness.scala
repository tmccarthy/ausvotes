package au.id.tmm.ausvotes.lambdas.utils

import java.io.{IOException, InputStream, OutputStream}
import java.nio.charset.Charset

import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness._
import au.id.tmm.ausvotes.shared.io.Closeables
import cats.syntax.show.toShow
import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger, RequestStreamHandler}
import io.circe.parser._
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import scalaz.zio.{ExitResult, IO, RTS}

import scala.annotation.tailrec

abstract class LambdaHarness[T_REQUEST : Decoder, T_RESPONSE : Encoder, T_ERROR](protected val rts: RTS = new RTS {})
  extends RequestStreamHandler {

  final override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    rts.unsafeRunSync(harness(input, output, context)) match {
      case ExitResult.Succeeded(_)  => Unit
      case ExitResult.Failed(cause) => handleFailure(cause)
    }
  }

  @tailrec
  private def handleFailure(failureCause: ExitResult.Cause[LambdaHarness.ResponseWriteError]): Unit =
    failureCause match {
      case ExitResult.Cause.Checked(e) => throw e.exception
      case ExitResult.Cause.Unchecked(t) => throw t
      case ExitResult.Cause.Interruption => throw new InterruptedException()
      case ExitResult.Cause.Both(left, right) => handleFailure(left)
      case ExitResult.Cause.Then(left, right) => handleFailure(left)
    }

  private def harness(input: InputStream, output: OutputStream, context: Context): IO[ResponseWriteError, Unit] = {
    val computeResponseLogic: IO[HarnessInputError, T_RESPONSE] = for {
      requestAsString <- Closeables.InputStreams.readAsString(IO.point(input), charset)
        .leftMap(RequestReadError)

      requestJson <- IO.fromEither(parse(requestAsString))
        .leftMap(f => RequestDecodeError(f.show, requestAsString))

      request <- IO.fromEither(requestJson.as[T_REQUEST])
        .leftMap(f => RequestDecodeError(f.show, requestAsString))

      responseOrError <- logic(request, context).attempt

      deliverableResponse <- responseOrError.fold(handleError(_, context.getLogger), IO.point(_))
    } yield deliverableResponse

    computeResponseLogic.attempt.flatMap {
      case Left(e @ RequestReadError(exception)) => IO.sync(context.getLogger.log(ExceptionUtils.getStackTrace(exception)))
        .flatMap(_ => writeResponseTo(transformHarnessError(e), output))
      case Left(e: RequestDecodeError) => IO.sync(context.getLogger.log(e.toString))
        .flatMap(_ => writeResponseTo(transformHarnessError(e), output))
      case Right(response) => writeResponseTo(response, output)
    }
  }

  protected def logic(request: T_REQUEST, context: Context): IO[T_ERROR, T_RESPONSE]

  private def handleError(error: T_ERROR, lambdaLogger: LambdaLogger): IO[Nothing, T_RESPONSE] = {
    val writeLogMessage: IO[Nothing, Unit] = errorLogTransformer.messageFor(error)
      .map(logMessage => IO.sync(lambdaLogger.log(logMessage)))
      .getOrElse(IO.unit)

    writeLogMessage
      .map(_ => errorResponseTransformer.responseFor(error))
  }

  protected def errorResponseTransformer: ErrorResponseTransformer[T_RESPONSE, T_ERROR]

  protected def errorLogTransformer: ErrorLogTransformer[T_ERROR]

  protected def transformHarnessError(harnessInputError: HarnessInputError): T_RESPONSE

  private def writeResponseTo(response: T_RESPONSE, output: OutputStream): IO[ResponseWriteError, Unit] = {
    IO.bracket(IO.sync(output))(os => IO.sync(os.close())) { output =>
      val jsonString = response.asJson.noSpaces

      IO.syncCatch(IOUtils.write(jsonString, output)) {
        case e: IOException => ResponseWriteError(e)
      }
    }
  }

}

object LambdaHarness {
  val charset: Charset = Charset.forName("UTF-8")

  sealed trait HarnessInputError

  final case class RequestReadError(exception: IOException) extends HarnessInputError
  final case class RequestDecodeError(message: String, request: String) extends HarnessInputError

  final case class ResponseWriteError(exception: IOException)

  trait ErrorResponseTransformer[T_RESPONSE, E] {
    def responseFor(error: E): T_RESPONSE
  }

  trait ErrorLogTransformer[E] {
    def messageFor(error: E): Option[String]
  }
}

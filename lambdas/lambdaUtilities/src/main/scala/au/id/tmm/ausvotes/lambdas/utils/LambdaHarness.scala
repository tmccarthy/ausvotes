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
import scalaz.zio.{DefaultRuntime, Exit, IO}

import scala.annotation.tailrec

abstract class LambdaHarness[T_REQUEST : Decoder, T_RESPONSE : Encoder, T_ERROR](protected val rts: DefaultRuntime = new DefaultRuntime {})
  extends RequestStreamHandler {

  final override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    rts.unsafeRunSync(harness(input, output, context)) match {
      case Exit.Success(_)  => Unit
      case Exit.Failure(cause) => handleFailure(cause)
    }
  }

  @tailrec
  private def handleFailure(failureCause: Exit.Cause[LambdaHarness.ResponseWriteError]): Unit =
    failureCause match {
      case Exit.Cause.Fail(e) => throw e.exception
      case Exit.Cause.Die(t) => throw t
      case Exit.Cause.Interrupt => throw new InterruptedException()
      case Exit.Cause.Both(left, right) => handleFailure(left)
      case Exit.Cause.Then(left, right) => handleFailure(left)
    }

  private def harness(input: InputStream, output: OutputStream, context: Context): IO[ResponseWriteError, Unit] = {
    val computeResponseLogic: IO[HarnessInputError, T_RESPONSE] = for {
      requestAsString <- Closeables.InputStreams.readAsString(IO.succeed(input), charset)
        .mapError(RequestReadError)

      requestJson <- IO.fromEither(parse(requestAsString))
        .mapError(f => RequestDecodeError(f.show, requestAsString))

      request <- IO.fromEither(requestJson.as[T_REQUEST])
        .mapError(f => RequestDecodeError(f.show, requestAsString))

      responseOrError <- logic(request, context).either

      deliverableResponse <- responseOrError.fold(handleError(_, context.getLogger), IO.succeed(_))
    } yield deliverableResponse

    computeResponseLogic.either.flatMap {
      case Left(e @ RequestReadError(exception)) => IO.effectTotal(context.getLogger.log(ExceptionUtils.getStackTrace(exception)))
        .flatMap(_ => writeResponseTo(transformHarnessError(e), output))
      case Left(e: RequestDecodeError) => IO.effectTotal(context.getLogger.log(e.toString))
        .flatMap(_ => writeResponseTo(transformHarnessError(e), output))
      case Right(response) => writeResponseTo(response, output)
    }
  }

  protected def logic(request: T_REQUEST, context: Context): IO[T_ERROR, T_RESPONSE]

  private def handleError(error: T_ERROR, lambdaLogger: LambdaLogger): IO[Nothing, T_RESPONSE] = {
    val writeLogMessage: IO[Nothing, Unit] = errorLogTransformer.messageFor(error)
      .map(logMessage => IO.effectTotal(lambdaLogger.log(logMessage)))
      .getOrElse(IO.unit)

    writeLogMessage
      .map(_ => errorResponseTransformer.responseFor(error))
  }

  protected def errorResponseTransformer: ErrorResponseTransformer[T_RESPONSE, T_ERROR]

  protected def errorLogTransformer: ErrorLogTransformer[T_ERROR]

  protected def transformHarnessError(harnessInputError: HarnessInputError): T_RESPONSE

  private def writeResponseTo(response: T_RESPONSE, output: OutputStream): IO[ResponseWriteError, Unit] = {
    IO.bracket(IO.effectTotal(output))(os => IO.effectTotal(os.close())) { output =>
      val jsonString = response.asJson.noSpaces

      IO.effect(IOUtils.write(jsonString, output)).refineOrDie {
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

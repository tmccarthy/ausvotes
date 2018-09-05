package au.id.tmm.ausvotes.lambdas.utils

import java.io.{IOException, InputStream, OutputStream}
import java.nio.charset.Charset

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness._
import au.id.tmm.ausvotes.shared.io.Closeables
import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger, RequestStreamHandler}
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import scalaz.zio.{ExitResult, IO, RTS}

abstract class LambdaHarness[T_REQUEST : DecodeJson, T_RESPONSE : EncodeJson, T_ERROR] extends RequestStreamHandler
  with RTS {

  final override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    unsafeRunSync(harness(input, output, context)) match {
      case ExitResult.Completed(_)  => Unit
      case ExitResult.Terminated(t) => throw t.head
      case ExitResult.Failed(e, _)  => throw e.exception
    }
  }

  private def harness(input: InputStream, output: OutputStream, context: Context): IO[ResponseWriteError, Unit] = {
    val computeResponseLogic: IO[HarnessInputError, T_RESPONSE] = for {
      requestAsString <- Closeables.InputStreams.readAsString(IO.point(input), charset)
          .leftMap(RequestReadError)

      request <- IO.fromEither(Parse.decodeEither[T_REQUEST](requestAsString))
          .leftMap(RequestDecodeError(_, requestAsString))

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
      val jsonString = response.asJson.toString()

      IO.syncCatch(IOUtils.write(jsonString, output)) {
        case e: IOException => ResponseWriteError(e)
      }
    }
  }

}

object LambdaHarness {
  val charset: Charset = Charset.forName("UTF-8")

  private[utils] sealed trait HarnessInputError

  private[utils] final case class RequestReadError(exception: IOException) extends HarnessInputError
  private[utils] final case class RequestDecodeError(message: String, request: String) extends HarnessInputError

  private[utils] final case class ResponseWriteError(exception: IOException)

  trait ErrorResponseTransformer[T_RESPONSE, E] {
    def responseFor(error: E): T_RESPONSE
  }

  trait ErrorLogTransformer[E] {
    def messageFor(error: E): Option[String]
  }
}
package au.id.tmm.ausvotes.lambdas.utils

import java.io.{IOException, InputStream, OutputStream}
import java.nio.charset.Charset

import argonaut.Argonaut._
import argonaut.Parse
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness._
import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import scalaz.zio.{ExitResult, IO, RTS}

abstract class LambdaHarness[T_ERROR] extends RequestStreamHandler with RTS {

  final override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    unsafeRunSync(harness(input, output, context)) match {
      case ExitResult.Completed(_)  => Unit
      case ExitResult.Terminated(t) => throw t.head
      case ExitResult.Failed(e, _)  => throw e.exception
    }
  }

  private def harness(input: InputStream, output: OutputStream, context: Context): IO[ResponseWriteError, Unit] = {
    val computeResponseLogic: IO[HarnessInputError, LambdaResponse] = for {
      request <- readRequestFrom(input)

      responseOrError <- logic(request, context).attempt

      deliverableResponse = responseOrError.left.map(transformError).fold(identity, identity)
    } yield deliverableResponse

    computeResponseLogic.attempt.flatMap {
      case Left(e @ RequestReadError(exception)) => IO.sync(context.getLogger.log(ExceptionUtils.getStackTrace(exception)))
        .flatMap(_ => writeResponseTo(transformHarnessError(e), output))
      case Left(e: RequestDecodeError) => writeResponseTo(transformHarnessError(e), output)
      case Right(response) => writeResponseTo(response, output)
    }
  }

  private def readRequestFrom(input: InputStream): IO[HarnessInputError, LambdaRequest] = {
    IO.bracket(IO.sync(input))(is => IO.sync(is.close())) { input =>
      for {
        requestAsString <- IO.syncCatch(IOUtils.toString(input, charset)){
          case e: IOException => RequestReadError(e)
        }
        request <- IO.fromEither(Parse.decodeEither[LambdaRequest](requestAsString))
          .leftMap(RequestDecodeError)
      } yield request
    }
  }

  protected def logic(request: LambdaRequest, context: Context): IO[T_ERROR, LambdaResponse]

  protected def transformError(error: T_ERROR): LambdaResponse

  private def transformHarnessError(harnessInputError: HarnessInputError): LambdaResponse = harnessInputError match {
    case RequestReadError(_) => LambdaResponse(500, Map.empty, jString(""))
    case RequestDecodeError(message) => LambdaResponse(400, Map.empty, jString(message))
  }

  private def writeResponseTo(response: LambdaResponse, output: OutputStream): IO[ResponseWriteError, Unit] = {
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

  private sealed trait HarnessInputError

  private final case class RequestReadError(exception: IOException) extends HarnessInputError
  private final case class RequestDecodeError(message: String) extends HarnessInputError

  private final case class ResponseWriteError(exception: IOException)
}
package au.id.tmm.ausvotes.shared.aws

import java.nio.charset.StandardCharsets

import au.id.tmm.ausvotes.shared.aws.data.LambdaFunctionName
import au.id.tmm.http_constants.HttpResponseCode
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.lambda.model.{InvokeRequest, InvokeResult}
import com.amazonaws.services.lambda.{AWSLambdaAsync, AWSLambdaAsyncClientBuilder}
import scalaz.zio.IO

object LambdaOps {

  private def lambdaClient: IO[Exception, AWSLambdaAsync] = IO.sync(AWSLambdaAsyncClientBuilder.defaultClient())

  def invokeLambda(functionName: LambdaFunctionName, payload: Option[String]): IO[Exception, String] = {
    val request = new InvokeRequest()
      .withFunctionName(functionName.asString)
      .withPayload(payload.orNull)

    for {
      client <- lambdaClient
      resultPayload <- IO.async[Exception, String](callback => client.invokeAsync(request, lambdaAsyncHandler(callback)))
    } yield resultPayload
  }

  private def lambdaAsyncHandler(ioCallback: IO[Exception, String] => Unit): AsyncHandler[InvokeRequest, InvokeResult] =
    new AsyncHandler[InvokeRequest, InvokeResult] {
      override def onError(exception: Exception): Unit = ioCallback(IO.fail(exception))

      override def onSuccess(request: InvokeRequest, result: InvokeResult): Unit = {

        val payload = StandardCharsets.UTF_8.decode(result.getPayload).toString

        result.getFunctionError match {
          case handled @ ("Handled" | "Unhandled") => ioCallback.apply(IO.fail(
            LambdaInvocationException(
              statusCode = HttpResponseCode.fromCode(result.getStatusCode)
                .getOrElse(HttpResponseCode.InternalServerError),
              handled = handled == "Handled",
              payload = payload,
            )
          ))
          case _ => ioCallback.apply(IO.point(payload))
        }
      }
    }

  final case class LambdaInvocationException(statusCode: HttpResponseCode, handled: Boolean, payload: String)
    extends Exception(s"${classOf[LambdaInvocationException].getSimpleName}($statusCode, handled = $handled, payload = <$payload>)")

}

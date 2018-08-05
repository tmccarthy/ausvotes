package au.id.tmm.ausvotes.lambdas.recount

import java.io.{InputStream, OutputStream}

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.lambdas.recount.Lambda.Response
import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import org.apache.commons.io.IOUtils

final class Lambda extends RequestStreamHandler {
  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    for {
      _ <- resource.managed(input)
      _ <- resource.managed(output)
    } {
      val response = Response(200, Map(), "Hello world")

      IOUtils.write(response.asJson.toString(), output, "UTF-8")
    }
  }
}

object Lambda {
  final case class Request()

  final case class Response(statusCode: Int, headers: Map[String, String], body: String) {
    def isBase64Encoded: Boolean = false
  }

  object Response {
    implicit val encodeResponse: EncodeJson[Response] = response => jObjectFields(
      "statusCode" -> response.statusCode.asJson,
      "headers" -> response.headers.asJson,
      "body" -> response.body.asJson,
      "isBase64Encoded" -> response.isBase64Encoded.asJson,
    )
  }
}
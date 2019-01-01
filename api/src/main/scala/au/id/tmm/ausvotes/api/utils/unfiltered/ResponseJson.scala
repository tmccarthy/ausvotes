package au.id.tmm.ausvotes.api.utils.unfiltered

import io.circe.Encoder
import io.circe.syntax.EncoderOps
import unfiltered.response.{HttpResponse, JsonContent, Responder, ResponseString}

final case class ResponseJson[A : Encoder](content: A) extends Responder[Any] {

  override def respond(response: HttpResponse[Any]): Unit = {
    val jsonString = content.asJson.noSpaces

    ResponseString(jsonString)(response)

    JsonContent(response)
  }

}

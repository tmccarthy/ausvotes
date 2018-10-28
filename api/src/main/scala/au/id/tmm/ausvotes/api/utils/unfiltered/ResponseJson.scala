package au.id.tmm.ausvotes.api.utils.unfiltered

import argonaut.Argonaut._
import argonaut.EncodeJson
import unfiltered.response.{HttpResponse, JsonContent, Responder, ResponseString}

final case class ResponseJson[A : EncodeJson](content: A) extends Responder[Any] {

  override def respond(response: HttpResponse[Any]): Unit = {
    val jsonString = content.asJson.toString

    ResponseString(jsonString)(response)

    JsonContent(response)
  }

}

package au.id.tmm.ausvotes.api.jsonformats

import au.id.tmm.ausvotes.api.errorhandling.ErrorResponse
import org.json4s.JsonAST.{JObject, JString}
import org.json4s.{CustomSerializer, Serializer}

object ErrorResponseSerializers {

  val errorResponseSerializer: Serializer[ErrorResponse] = new CustomSerializer[ErrorResponse](implicit formats => ({
    PartialFunction.empty
  }, {
    case error: ErrorResponse =>
      JObject(
        "errorId" -> JString(error.errorId),
        "errorDescription" -> JString(error.errorDescription),
        "details" -> JObject(
          error.details.render.mapValues(JString).toList,
        ),
      )
  }))

  val ALL = Set(errorResponseSerializer)
}

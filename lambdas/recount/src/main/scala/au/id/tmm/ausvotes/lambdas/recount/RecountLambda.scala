package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import au.id.tmm.ausvotes.lambdas.utils.{LambdaHarness, Request, Response}
import com.amazonaws.services.lambda.runtime.Context
import org.apache.http.HttpStatus
import scalaz.zio.IO

final class RecountLambda extends LambdaHarness[RecountLambda.Error] {

  override def logic(request: Request, context: Context): IO[Error, Response] = {
    val response = jObjectFields(
      "election" -> request.pathParameters("election").asJson,
      "state" -> request.pathParameters("state").asJson,
      "vacancies" -> request.queryStringParameters("vacancies").asJson,
      "ineligibleCandidates" -> request.queryStringParameters("ineligibleCandidates").asJson,
    )

    IO.point(Response(HttpStatus.SC_OK, Map.empty, response))
  }

  override def transformError(error: RecountLambda.Error): Response = ???
}

object RecountLambda {
  sealed trait Error
}
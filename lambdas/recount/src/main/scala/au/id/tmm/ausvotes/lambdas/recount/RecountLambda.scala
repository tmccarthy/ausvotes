package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import au.id.tmm.ausvotes.lambdas.recount.Errors.RecountLambdaError
import au.id.tmm.ausvotes.lambdas.utils.{LambdaHarness, LambdaRequest, LambdaResponse}
import com.amazonaws.services.lambda.runtime.Context
import org.apache.http.HttpStatus
import scalaz.zio.IO

final class RecountLambda extends LambdaHarness[RecountLambdaError] {

  override def logic(request: LambdaRequest, context: Context): IO[RecountLambdaError, LambdaResponse] = {
    val response = jObjectFields(
      "election" -> request.pathParameters("election").asJson,
      "state" -> request.pathParameters("state").asJson,
      "vacancies" -> request.queryStringParameters("vacancies").asJson,
      "ineligibleCandidates" -> request.queryStringParameters("ineligibleCandidates").asJson,
    )

    IO.point(LambdaResponse(HttpStatus.SC_OK, Map.empty, response))
  }

  override def transformError(error: RecountLambdaError): LambdaResponse = error match {
    case Errors.RecountRequestError.MissingElection =>
      badRequestResponse("Election was not specified")

    case Errors.RecountRequestError.InvalidElectionId(badElectionId) =>
      badRequestResponse(s"""Unrecognised election id "$badElectionId"""")

    case Errors.RecountRequestError.MissingState =>
      badRequestResponse("State was not specified")

    case Errors.RecountRequestError.InvalidStateId(badStateId) =>
      badRequestResponse(s"""Unrecognised state id "$badStateId"""")

    case Errors.RecountRequestError.NoElectionForState(election, state) =>
      badRequestResponse(s"""The election "${election.id}" did not have an election for state "${state.abbreviation}"""")

    case Errors.RecountRequestError.InvalidNumVacancies(badNumVacancies) =>
      badRequestResponse(s"""Invalid number of vacancies "$badNumVacancies"""")
  }

  private def badRequestResponse(message: String): LambdaResponse = LambdaResponse(
    statusCode = 400,
    headers = Map.empty,
    body = jObjectFields(
      "message" -> jString(message),
    )
  )
}

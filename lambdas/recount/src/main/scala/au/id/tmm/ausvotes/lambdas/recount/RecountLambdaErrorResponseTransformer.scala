package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut.{jObjectFields, jString}
import au.id.tmm.ausvotes.lambdas.utils.{ApiGatewayLambdaHarness, ApiGatewayLambdaResponse}

object RecountLambdaErrorResponseTransformer extends ApiGatewayLambdaHarness.ErrorResponseTransformer[RecountLambdaError] {

  private[recount] def badRequestResponse(message: String): ApiGatewayLambdaResponse = ApiGatewayLambdaResponse(
    statusCode = 400,
    headers = Map.empty,
    body = jObjectFields(
      "message" -> jString(message),
    )
  )

  override def responseFor(error: RecountLambdaError): ApiGatewayLambdaResponse = error match {
    case RecountLambdaError.RecountRequestError.MissingElection =>
      badRequestResponse("Election was not specified")

    case RecountLambdaError.RecountRequestError.InvalidElectionId(badElectionId) =>
      badRequestResponse(s"""Unrecognised election id "$badElectionId"""")

    case RecountLambdaError.RecountRequestError.MissingState =>
      badRequestResponse("State was not specified")

    case RecountLambdaError.RecountRequestError.InvalidStateId(badStateId) =>
      badRequestResponse(s"""Unrecognised state id "$badStateId"""")

    case RecountLambdaError.RecountRequestError.NoElectionForState(election, state) =>
      badRequestResponse(s"""The election "${election.id}" did not have an election for state "${state.abbreviation}"""")

    case RecountLambdaError.RecountRequestError.InvalidNumVacancies(badNumVacancies) =>
      badRequestResponse(s"""Invalid number of vacancies "$badNumVacancies"""")

    case RecountLambdaError.RecountRequestError.InvalidCandidateIds(invalidCandidateAecIds) =>
      badRequestResponse(s"""Invalid candidate ids ${invalidCandidateAecIds.mkString("[\"", "\", \"", "\"]")}""")

    case RecountLambdaError.ConfigurationError.RecountDataBucketUndefined =>
      badRequestResponse("Recount data bucket was undefined")

    case RecountLambdaError.EntityFetchError.GroupFetchError(_) =>
      badRequestResponse("An error occurred while fetching the groups")

    case RecountLambdaError.EntityFetchError.GroupDecodeError(message) =>
      badRequestResponse(s"""An error occurred while decoding the groups: "$message"""")

    case RecountLambdaError.EntityFetchError.CandidateFetchError(_) =>
      badRequestResponse("An error occurred while fetching the candidates")

    case RecountLambdaError.EntityFetchError.CandidateDecodeError(message) =>
      badRequestResponse(s"""An error occurred while decoding the candidates: "$message"""")

    case RecountLambdaError.EntityFetchError.PreferenceTreeFetchError(_) =>
      badRequestResponse("An error occurred while fetching or decoding the preference tree")

    case RecountLambdaError.RecountComputationError(_) =>
      badRequestResponse("An error occurred while performing the recount computation")
  }
}

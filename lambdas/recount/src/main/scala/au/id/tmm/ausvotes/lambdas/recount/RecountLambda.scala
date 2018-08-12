package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.lambdas.utils.{LambdaHarness, LambdaRequest, LambdaResponse}
import com.amazonaws.services.lambda.runtime.Context
import scalaz.zio.IO

final class RecountLambda extends LambdaHarness[RecountLambdaError] {

  override def logic(lambdaRequest: LambdaRequest, context: Context): IO[RecountLambdaError, LambdaResponse] = {
    implicit val partyCodec: PartyCodec = PartyCodec()
    implicit val groupCodec: GroupCodec = GroupCodec()

    for {
      recountDataBucketName <- Configuration.recountDataBucketName

      recountRequest <- IO.fromEither(RecountRequest.fromRequest(lambdaRequest))

      election = recountRequest.election
      state = recountRequest.state

      groups <- EntityFetching.fetchGroups(recountDataBucketName, election, state)

      candidateCodec = CandidateCodec(groups)
      candidates <- EntityFetching.fetchCandidates(recountDataBucketName, election, state)(candidateCodec)

      ineligibleCandidates <- IO.fromEither {
        CandidateActualisation.actualiseIneligibleCandidates(recountRequest.ineligibleCandidateAecIds, candidates)
      }

      preferenceTree <- EntityFetching.fetchPreferenceTree(recountDataBucketName, election, state, candidates)

      recountResult <- IO.fromEither {
        PerformRecount.performRecount(
          election,
          state,
          candidates,
          preferenceTree,
          ineligibleCandidates,
          recountRequest.vacancies,
        )
      }
    } yield LambdaResponse(200, Map.empty, recountResult.asJson(PerformRecount.Result.encodeRecountResult(candidateCodec)))
  }

  override def transformError(error: RecountLambdaError): LambdaResponse = error match {
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

  private def badRequestResponse(message: String): LambdaResponse = LambdaResponse(
    statusCode = 400,
    headers = Map.empty,
    body = jObjectFields(
      "message" -> jString(message),
    )
  )
}

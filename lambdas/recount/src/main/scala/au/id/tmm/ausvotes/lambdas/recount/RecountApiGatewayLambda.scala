package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.{ApiGatewayLambdaHarness, ApiGatewayLambdaRequest, ApiGatewayLambdaResponse}
import com.amazonaws.services.lambda.runtime.Context
import scalaz.zio.IO

final class RecountApiGatewayLambda extends ApiGatewayLambdaHarness[RecountLambdaError] {

  override def logic(lambdaRequest: ApiGatewayLambdaRequest, context: Context): IO[RecountLambdaError, ApiGatewayLambdaResponse] = {
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
    } yield ApiGatewayLambdaResponse(200, Map.empty, recountResult.asJson(PerformRecount.Result.encodeRecountResult(candidateCodec)))
  }

  override protected def errorResponseTransformer: RecountLambdaErrorResponseTransformer.type = RecountLambdaErrorResponseTransformer

  override protected def errorLogTransformer: RecountLambdaErrorLogTransformer.type = RecountLambdaErrorLogTransformer

}

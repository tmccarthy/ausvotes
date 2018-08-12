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

  override protected def errorResponseTransformer: RecountLambdaErrorResponseTransformer.type = RecountLambdaErrorResponseTransformer

  override protected def errorLogTransformer: RecountLambdaErrorLogTransformer.type = RecountLambdaErrorLogTransformer

}

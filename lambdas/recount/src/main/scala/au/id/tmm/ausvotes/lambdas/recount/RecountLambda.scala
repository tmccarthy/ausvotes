package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.lambdas.utils.snsintegration.{SnsLambdaHarness, SnsLambdaRequest}
import au.id.tmm.ausvotes.shared.aws.S3Ops
import au.id.tmm.ausvotes.shared.io.Slf4jLogging._
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import au.id.tmm.utilities.logging.Logger
import com.amazonaws.services.lambda.runtime.Context
import scalaz.zio.IO

final class RecountLambda extends SnsLambdaHarness[RecountRequest, RecountLambdaError] {

  implicit val logger: Logger = Logger()

  override def logic(lambdaRequest: SnsLambdaRequest[RecountRequest], context: Context): IO[RecountLambdaError, Unit] = {
    implicit val partyCodec: PartyCodec = PartyCodec()
    implicit val groupCodec: GroupCodec = GroupCodec()

    for {
      recountDataBucketName <- Configuration.recountDataBucketName

      recountRequest = lambdaRequest.snsBody.message

      _ <- logInfo("RECEIVE_RECOUNT_REQUEST", "recount_request" -> recountRequest)

      election = recountRequest.election
      state = recountRequest.state

      groups <- EntityFetching.fetchGroups(recountDataBucketName, election, state)
        .timedLog("FETCH_GROUPS",
          "recount_data_bucket_name" -> recountDataBucketName,
          "election" -> election,
          "state" -> state,
        )

      candidateCodec = CandidateCodec(groups)
      candidates <- EntityFetching.fetchCandidates(recountDataBucketName, election, state)(candidateCodec)
        .timedLog("FETCH_CANDIDATES",
          "recount_data_bucket_name" -> recountDataBucketName,
          "election" -> election,
          "state" -> state,
        )

      ineligibleCandidates <- IO.fromEither {
        CandidateActualisation.actualiseIneligibleCandidates(recountRequest.ineligibleCandidateAecIds, candidates)
      }

      preferenceTree <- EntityFetching.fetchPreferenceTree(recountDataBucketName, election, state, candidates)
        .timedLog("FETCH_PREFERENCE_TREE",
          "recount_data_bucket_name" -> recountDataBucketName,
          "election" -> election,
          "state" -> state,
        )

      recountResult <- IO.sync {
        PerformRecount.performRecount(
          election,
          state,
          candidates,
          preferenceTree,
          ineligibleCandidates,
          recountRequest.vacancies,
        )
      }
        .timedLog("PERFORM_RECOUNT",
          "election" -> election,
          "state" -> state,
          "num_ineligible_candidates" -> ineligibleCandidates.size,
          "num_vacancies" -> recountRequest.vacancies,
        )
        .flatMap(IO.fromEither)

      recountResultKey = RecountLocations.locationOfRecountFor(recountRequest)

      _ <- S3Ops.putString(
        bucketName = recountDataBucketName,
        objectKey = recountResultKey,
        content = recountResult.asJson(PerformRecount.Result.encodeRecountResult(candidateCodec)).toString,
      )
        .leftMap(RecountLambdaError.WriteRecountError)
        .timedLog("PUT_RECOUNT_RESULT",
          "recount_data_bucket_name" -> recountDataBucketName,
          "recount_result_key" -> recountResultKey,
        )
    } yield Unit
  }

  override protected def errorLogTransformer: RecountLambdaErrorLogTransformer.type = RecountLambdaErrorLogTransformer

}

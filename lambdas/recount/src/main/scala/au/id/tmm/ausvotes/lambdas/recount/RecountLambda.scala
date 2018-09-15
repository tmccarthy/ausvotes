package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import argonaut.CodecJson
import au.id.tmm.ausvotes.core.model.codecs.{CandidateCodec, GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.lambdas.utils.snsintegration.{SnsLambdaHarness, SnsLambdaRequest}
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.{ReadsS3, WritesToS3}
import au.id.tmm.ausvotes.shared.io.Logging
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.actions.Log._
import au.id.tmm.ausvotes.shared.io.actions.{EnvVars, Log, Now}
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.io.typeclasses._
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import com.amazonaws.services.lambda.runtime.Context
import scalaz.zio.IO

final class RecountLambda extends SnsLambdaHarness[RecountRequest, RecountLambdaError] {

  override def logic(lambdaRequest: SnsLambdaRequest[RecountRequest], context: Context): IO[RecountLambdaError, Unit] = {
    import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
    import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
    recountLogic[IO](lambdaRequest, context)
  }

  private def recountLogic[F[+_, +_] : ReadsS3 : WritesToS3 : EnvVars : SyncEffects : Attempt : Log : Now : Monad](lambdaRequest: SnsLambdaRequest[RecountRequest], context: Context): F[RecountLambdaError, Unit] = {

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
      candidates <- {
        implicit val c: CodecJson[Candidate] = candidateCodec

        EntityFetching.fetchCandidates(recountDataBucketName, election, state)
          .timedLog("FETCH_CANDIDATES",
            "recount_data_bucket_name" -> recountDataBucketName,
            "election" -> election,
            "state" -> state,
          )
      }

      ineligibleCandidates <- Monad.fromEither {
        CandidateActualisation.actualiseIneligibleCandidates(recountRequest.ineligibleCandidateAecIds, candidates)
      }

      preferenceTree <- EntityFetching.fetchPreferenceTree(recountDataBucketName, election, state, candidates)
        .timedLog("FETCH_PREFERENCE_TREE",
          "recount_data_bucket_name" -> recountDataBucketName,
          "election" -> election,
          "state" -> state,
        )

      //noinspection ConvertibleToMethodValue
      recountResult <- Logging.timedLog(
        "PERFORM_RECOUNT",
        "election" -> election,
        "state" -> state,
        "num_ineligible_candidates" -> ineligibleCandidates.size,
        "num_vacancies" -> recountRequest.vacancies,
      ) {
        PerformRecount.performRecount(
          election,
          state,
          candidates,
          preferenceTree,
          ineligibleCandidates,
          recountRequest.vacancies,
        )
      }

      recountResultKey = RecountLocations.locationOfRecountFor(recountRequest)

      _ <- WritesToS3.putJson(recountDataBucketName, recountResultKey)(
        content = recountResult.asJson(PerformRecount.Result.encodeRecountResult(candidateCodec)).toString,
      )
        .timedLog("PUT_RECOUNT_RESULT",
          "recount_data_bucket_name" -> recountDataBucketName,
          "recount_result_key" -> recountResultKey,
        )
        .leftMap(RecountLambdaError.WriteRecountError)
    } yield Unit
  }

  override protected def errorLogTransformer: RecountLambdaErrorLogTransformer.type = RecountLambdaErrorLogTransformer

}

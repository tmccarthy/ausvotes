package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness.{ErrorResponseTransformer, RequestDecodeError, RequestReadError}
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.WritesToS3
import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.actions.Log._
import au.id.tmm.ausvotes.shared.io.actions.{Log, Now}
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree
import au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching.{GroupsAndCandidatesCache, PreferenceTreeCache}
import au.id.tmm.ausvotes.shared.recountresources.recount.RunRecount
import au.id.tmm.ausvotes.shared.recountresources.{CountSummary, RecountLocations, RecountRequest, RecountResponse}
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync._
import au.id.tmm.bfect.extraeffects.EnvVars
import au.id.tmm.utilities.collection.Flyweight
import com.amazonaws.services.lambda.runtime.Context
import io.circe.syntax.EncoderOps
import scalaz.zio.{DefaultRuntime, IO}

final class RecountLambda extends LambdaHarness[RecountRequest, RecountResponse, RecountLambdaError](RecountLambda.rts) {

  override def logic(lambdaRequest: RecountRequest, context: Context): IO[RecountLambdaError, RecountResponse] = {
    import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
    import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
    import au.id.tmm.bfect.ziointerop._

    for {
      recountDataBucketName <- Configuration.recountDataBucketName

      result <- {
        implicit val preferenceTreeCache: FetchPreferenceTree[IO] =
          RecountLambda.preferenceTreeCache(recountDataBucketName)

        recountLogic(lambdaRequest, context)
      }

    } yield result
  }

  private def recountLogic[F[+_, +_] : FetchPreferenceTree : WritesToS3 : Log : Now : EnvVars : Sync]
  (
    recountRequest: RecountRequest,
    context: Context,
  ): F[RecountLambdaError, RecountResponse] = {

    for {
      recountDataBucketName <- Configuration.recountDataBucketName
        .leftMap(e => e: RecountLambdaError)

      _ <- logInfo("RECEIVE_RECOUNT_REQUEST", "recount_request" -> recountRequest)

      countResult <- RunRecount.runRecountRequest(recountRequest)
          .leftMap(RecountLambdaError.RecountComputationError)

      countSummary <- Sync.fromEither(CountSummary.from(recountRequest, countResult))
          .leftMap(RecountLambdaError.RecountSummaryError)

      recountResultKey = RecountLocations.locationOfRecountFor(recountRequest)

      _ <- WritesToS3.putJson(recountDataBucketName, recountResultKey)(
        content = countSummary.asJson.toString,
      )
        .timedLog("PUT_RECOUNT_RESULT",
          "recount_data_bucket_name" -> recountDataBucketName,
          "recount_result_key" -> recountResultKey,
        )
        .leftMap(RecountLambdaError.WriteRecountError)
    } yield RecountResponse.Success(countSummary)
  }

  override protected def errorLogTransformer: RecountLambdaErrorLogTransformer.type = RecountLambdaErrorLogTransformer

  protected def errorResponseTransformer: ErrorResponseTransformer[RecountResponse, RecountLambdaError] = {
    case RecountLambdaError.RecountComputationError(RunRecount.Error.InvalidCandidateIds(invalidCandidateAecIds)) =>
      RecountResponse.Failure.InvalidCandidateIds(invalidCandidateAecIds)
    case _ => RecountResponse.Failure.InternalError
  }

  protected def transformHarnessError(harnessInputError: LambdaHarness.HarnessInputError): RecountResponse =
    harnessInputError match {
      case RequestReadError(_) => RecountResponse.Failure.InternalError
      case RequestDecodeError(message, request) => RecountResponse.Failure.RequestDecodeError(message, request)
    }

}

object RecountLambda {
  private lazy val rts: DefaultRuntime = new DefaultRuntime {}

  private val preferenceTreeCache: Flyweight[S3BucketName, PreferenceTreeCache] = Flyweight { s3BucketName =>
    rts.unsafeRun {
      for {
        groupsAndCandidatesCache <- GroupsAndCandidatesCache(s3BucketName)
        preferenceTreeCache <- PreferenceTreeCache(groupsAndCandidatesCache)
      } yield preferenceTreeCache
    }
  }
}

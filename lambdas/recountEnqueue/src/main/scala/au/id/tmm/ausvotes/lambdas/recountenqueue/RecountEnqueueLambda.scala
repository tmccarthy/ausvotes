package au.id.tmm.ausvotes.lambdas.recountenqueue

import java.net.URL

import argonaut.Argonaut._
import argonaut.EncodeJson
import au.id.tmm.ausvotes.lambdas.recountenqueue.RecountEnqueueLambda.Error.BadRequestError
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import au.id.tmm.ausvotes.lambdas.utils.UrlCodec._
import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.{ApiGatewayLambdaHarness, ApiGatewayLambdaRequest, ApiGatewayLambdaResponse}
import au.id.tmm.ausvotes.shared.aws.S3Urls
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.aws.actions.SnsActions.PutsSnsMessages
import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.actions.EnvVars
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import com.amazonaws.services.lambda.runtime.Context
import scalaz.zio.IO

class RecountEnqueueLambda extends ApiGatewayLambdaHarness[RecountEnqueueLambda.Error] {

  override protected def logic(request: ApiGatewayLambdaRequest, context: Context): IO[RecountEnqueueLambda.Error, ApiGatewayLambdaResponse] = {
    import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
    import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
    RecountEnqueueLambda.recountEnqueueLogic[IO](request)
  }

  override protected def errorResponseTransformer: LambdaHarness.ErrorResponseTransformer[ApiGatewayLambdaResponse, RecountEnqueueLambda.Error] =
    RecountEnqueueLambdaErrorResponseTransformer

  override protected def errorLogTransformer: LambdaHarness.ErrorLogTransformer[RecountEnqueueLambda.Error] =
    RecountEnqueueLambdaErrorLogTransformer
}

object RecountEnqueueLambda {

  def recountEnqueueLogic[F[+_, +_] : EnvVars : ReadsS3 : PutsSnsMessages : Monad](request: ApiGatewayLambdaRequest): F[RecountEnqueueLambda.Error, ApiGatewayLambdaResponse] = {
    for {
      recountQueueArn <- readRecountQueueArn
      recountDataBucket <- readRecountDataBucket
      region <- readRegion

      recountRequest <- Monad.fromEither(buildRecountRequest(request))

      recountComputationKey = RecountLocations.locationOfRecountFor(recountRequest)

      recountAlreadyComputed <- ReadsS3.checkObjectExists(recountDataBucket, recountComputationKey)
        .leftMap(RecountEnqueueLambda.Error.CheckRecountComputedError)

      _ <- if (!recountAlreadyComputed) putSnsMessage(recountQueueArn, recountRequest) else Monad.unit
    } yield {
      val response = RecountEnqueueLambda.Response(
        S3Urls.objectUrl(
          region = region,
          bucketName = recountDataBucket,
          objectKey = recountComputationKey,
        )
      )

      ApiGatewayLambdaResponse(202, Map.empty, response.asJson)
    }
  }

  private def readRecountQueueArn[F[+_, +_] : EnvVars : Monad]: F[RecountEnqueueLambda.Error.RecountQueueArnMissing.type, String] =
    EnvVars.envVarOr("RECOUNT_REQUEST_QUEUE", RecountEnqueueLambda.Error.RecountQueueArnMissing)

  private def readRecountDataBucket[F[+_, +_] : EnvVars : Monad]: F[RecountEnqueueLambda.Error.RecountDataBucketMissing.type, S3BucketName] =
    EnvVars.envVarOr("RECOUNT_DATA_BUCKET", RecountEnqueueLambda.Error.RecountDataBucketMissing).map(S3BucketName)

  private def readRegion[F[+_, +_] : EnvVars : Monad]: F[RecountEnqueueLambda.Error.RegionMissing.type, String] =
    EnvVars.envVarOr("AWS_DEFAULT_REGION", RecountEnqueueLambda.Error.RegionMissing)

  private def buildRecountRequest(request: ApiGatewayLambdaRequest): Either[BadRequestError, RecountRequest] =
    RecountRequest.build(
      rawElection = request.pathParameters.get("election"),
      rawState = request.pathParameters.get("state"),
      rawNumVacancies = request.queryStringParameters.get("vacancies"),
      rawIneligibleCandidates = request.queryStringParameters.get("ineligibleCandidates"),
    ).left.map(RecountEnqueueLambda.Error.BadRequestError)

  private def putSnsMessage[F[+_, +_] : PutsSnsMessages : Monad](recountQueueArn: String, recountRequest: RecountRequest): F[RecountEnqueueLambda.Error.MessagePublishError, Unit] = {
    val messageAsString = recountRequest.asJson.toString()

    PutsSnsMessages.putSnsMessage(recountQueueArn, messageAsString)
      .leftMap(RecountEnqueueLambda.Error.MessagePublishError)
  }

  sealed trait Error

  object Error {
    final case class BadRequestError(cause: RecountRequest.Error) extends Error
    final case class MessagePublishError(cause: Exception) extends Error
    final case class CheckRecountComputedError(cause: Exception) extends Error
    case object RecountQueueArnMissing extends Error
    case object RecountDataBucketMissing extends Error
    case object RegionMissing extends Error
  }

  final case class Response(recountLocation: URL)

  object Response {
    implicit val encode: EncodeJson[Response] = casecodec1(apply, unapply)("recountLocation")
  }
}

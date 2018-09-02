package au.id.tmm.ausvotes.lambdas.recountenqueue

import java.net.URL

import argonaut.Argonaut._
import argonaut.EncodeJson
import au.id.tmm.ausvotes.lambdas.recountenqueue.RecountEnqueueLambda.Error.BadRequestError
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import au.id.tmm.ausvotes.lambdas.utils.UrlCodec._
import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.{ApiGatewayLambdaHarness, ApiGatewayLambdaRequest, ApiGatewayLambdaResponse}
import au.id.tmm.ausvotes.shared.aws.{S3BucketName, S3Ops}
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import com.amazonaws.services.lambda.runtime.Context
import scalaz.zio.IO

class RecountEnqueueLambda extends ApiGatewayLambdaHarness[RecountEnqueueLambda.Error] {
  override protected def logic(request: ApiGatewayLambdaRequest, context: Context): IO[RecountEnqueueLambda.Error, ApiGatewayLambdaResponse] = {
    for {
      recountQueueArn <- readRecountQueueArn
      recountDataBucket <- readRecountDataBucket
      region <- readRegion

      recountRequest <- IO.fromEither(buildRecountRequest(request))

      recountComputationKey = RecountLocations.locationOfRecountFor(recountRequest)

      recountAlreadyComputed <- S3Ops.checkObjectExists(recountDataBucket, recountComputationKey)
          .leftMap(RecountEnqueueLambda.Error.CheckRecountComputedError)

      _ <- if (!recountAlreadyComputed) putSnsMessage(recountQueueArn, recountRequest) else IO.unit
    } yield {
      val response = RecountEnqueueLambda.Response(
        S3Ops.objectUrl(
          region = region,
          bucketName = recountDataBucket,
          objectKey = recountComputationKey,
        )
      )

      ApiGatewayLambdaResponse(202, Map.empty, response.asJson)
    }
  }

  private val readRecountQueueArn: IO[RecountEnqueueLambda.Error.RecountQueueArnMissing.type, String] =
    readEnvVar("RECOUNT_REQUEST_QUEUE", RecountEnqueueLambda.Error.RecountQueueArnMissing)

  private val readRecountDataBucket: IO[RecountEnqueueLambda.Error.RecountDataBucketMissing.type, S3BucketName] =
    readEnvVar("RECOUNT_DATA_BUCKET", RecountEnqueueLambda.Error.RecountDataBucketMissing).map(S3BucketName)

  private val readRegion: IO[RecountEnqueueLambda.Error.RegionMissing.type, String] =
    readEnvVar("AWS_DEFAULT_REGION", RecountEnqueueLambda.Error.RegionMissing)

  private def readEnvVar[E <: RecountEnqueueLambda.Error](name: String, errorIfMissing: => E): IO[E, String] =
    IO.sync(sys.env.get(name)).flatMap {
      case Some(recountQueueArn) => IO.point(recountQueueArn)
      case None => IO.fail(errorIfMissing)
    }

  private def buildRecountRequest(request: ApiGatewayLambdaRequest): Either[BadRequestError, RecountRequest] =
    RecountRequest.build(
      rawElection = request.pathParameters.get("election"),
      rawState = request.pathParameters.get("state"),
      rawNumVacancies = request.queryStringParameters.get("vacancies"),
      rawIneligibleCandidates = request.queryStringParameters.get("ineligibleCandidates"),
    ).left.map(RecountEnqueueLambda.Error.BadRequestError)

  private def putSnsMessage(recountQueueArn: String, recountRequest: RecountRequest): IO[RecountEnqueueLambda.Error.MessagePublishError, Unit] = {
    val messageAsString = recountRequest.asJson.toString()

    SnsOps.putMessage(recountQueueArn, messageAsString)
      .leftMap(RecountEnqueueLambda.Error.MessagePublishError)
  }

  override protected def errorResponseTransformer: LambdaHarness.ErrorResponseTransformer[ApiGatewayLambdaResponse, RecountEnqueueLambda.Error] =
    RecountEnqueueLambdaErrorResponseTransformer

  override protected def errorLogTransformer: LambdaHarness.ErrorLogTransformer[RecountEnqueueLambda.Error] =
    RecountEnqueueLambdaErrorLogTransformer
}

object RecountEnqueueLambda {
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

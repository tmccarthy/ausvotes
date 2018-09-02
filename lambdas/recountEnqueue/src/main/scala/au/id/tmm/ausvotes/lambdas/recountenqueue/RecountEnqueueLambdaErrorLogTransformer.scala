package au.id.tmm.ausvotes.lambdas.recountenqueue

import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import org.apache.commons.lang3.exception.ExceptionUtils

object RecountEnqueueLambdaErrorLogTransformer extends LambdaHarness.ErrorLogTransformer[RecountEnqueueLambda.Error] {
  override def messageFor(error: RecountEnqueueLambda.Error): Option[String] = error match {
    case RecountEnqueueLambda.Error.MessagePublishError(cause) => Some(s"MessagePublishError\n${ExceptionUtils.getStackTrace(cause)}")
    case RecountEnqueueLambda.Error.BadRequestError(cause) => Some(s"Bad request: $cause")
    case RecountEnqueueLambda.Error.CheckRecountComputedError(cause) => Some(s"CheckRecountComputedError\n${ExceptionUtils.getStackTrace(cause)}")
    case RecountEnqueueLambda.Error.RecountQueueArnMissing => Some("Recount queue ARN missing from env vars")
    case RecountEnqueueLambda.Error.RecountDataBucketMissing => Some("Recount data bucket missing from env vars")
    case RecountEnqueueLambda.Error.RegionMissing => Some("Region missing from env vars")
  }
}

package au.id.tmm.ausvotes.lambdas.recountenqueue

import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import org.apache.commons.lang3.exception.ExceptionUtils

object RecountEnqueueLambdaErrorLogTransformer extends LambdaHarness.ErrorLogTransformer[RecountEnqueueLambda.Error] {
  override def messageFor(error: RecountEnqueueLambda.Error): Option[String] = error match {
    case RecountEnqueueLambda.Error.MessagePublishError(exception) => Some(s"MessagePublishError\n${ExceptionUtils.getStackTrace(exception)}")
    case RecountEnqueueLambda.Error.RecountQueueArnMissing => Some("Recount Queue ARN missing from env vars")
  }
}

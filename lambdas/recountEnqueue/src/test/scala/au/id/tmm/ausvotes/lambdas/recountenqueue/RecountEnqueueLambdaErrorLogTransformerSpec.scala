package au.id.tmm.ausvotes.lambdas.recountenqueue

import au.id.tmm.ausvotes.shared.recountresources.RecountRequest
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.apache.commons.lang3.exception.ExceptionUtils

class RecountEnqueueLambdaErrorLogTransformerSpec extends ImprovedFlatSpec {

  private def testErrorLog(error: RecountEnqueueLambda.Error, expectedMessage: String, expectedStacktrace: Option[Exception] = None): Unit = {
    it should s"translate $error appropriately" in {
      val fullExpectedMessage = expectedStacktrace
        .map(expectedMessage + "\n" + ExceptionUtils.getStackTrace(_))
        .getOrElse(expectedMessage)

      assert(RecountEnqueueLambdaErrorLogTransformer.messageFor(error) === Some(fullExpectedMessage))
    }
  }

  behaviour of "the recount enqueue lambda error log transformer"

  private val testException = new Exception

  testErrorLog(
    RecountEnqueueLambda.Error.MessagePublishError(testException),
    expectedMessage = s"MessagePublishError",
    expectedStacktrace = Some(testException),
  )

  testErrorLog(
    RecountEnqueueLambda.Error.BadRequestError(RecountRequest.Error.MissingElection),
    expectedMessage = s"Bad request: ${RecountRequest.Error.MissingElection}",
  )

  testErrorLog(
    RecountEnqueueLambda.Error.CheckRecountComputedError(testException),
    expectedMessage = s"CheckRecountComputedError",
    expectedStacktrace = Some(testException),
  )

  testErrorLog(
    RecountEnqueueLambda.Error.RecountQueueArnMissing,
    expectedMessage = "Recount queue ARN missing from env vars",
  )

  testErrorLog(
    RecountEnqueueLambda.Error.RecountDataBucketMissing,
    expectedMessage = "Recount data bucket missing from env vars",
  )

  testErrorLog(
    RecountEnqueueLambda.Error.RegionMissing,
    expectedMessage = "Region missing from env vars",
  )

}

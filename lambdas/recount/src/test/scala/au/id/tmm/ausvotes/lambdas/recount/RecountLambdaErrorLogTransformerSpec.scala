package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.recountresources.recount.RunRecount
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.apache.commons.lang3.exception.ExceptionUtils

class RecountLambdaErrorLogTransformerSpec extends ImprovedFlatSpec {

  "the recount lambda error log transformer" should "return the string representation of a error without an exception" in {
    val error = RecountLambdaError.RecountDataBucketUndefined()

    assert(RecountLambdaErrorLogTransformer.messageFor(error) === Some(error.toString))
  }

  it should "return the string representation and stacktrace of an error with an exception" in {
    val exception = RunRecount.Error.PerformRecountException(new RuntimeException())

    val error = RecountLambdaError.RecountComputationError(exception)

    assert(
      RecountLambdaErrorLogTransformer.messageFor(error) ===
        Some(s"${error.toString}\n${ExceptionUtils.getStackTrace(exception)}")
    )
  }

}

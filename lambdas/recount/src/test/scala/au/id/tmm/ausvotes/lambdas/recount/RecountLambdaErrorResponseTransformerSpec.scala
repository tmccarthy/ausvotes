package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError._
import au.id.tmm.ausvotes.model.CandidateDetails
import au.id.tmm.ausvotes.shared.recountresources.recount.RunRecount
import au.id.tmm.ausvotes.shared.recountresources.recount.RunRecount.Error.InvalidCandidateIds
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountLambdaErrorResponseTransformerSpec extends ImprovedFlatSpec {
  def errorResponseTest(error: RecountLambdaError, expectedMessage: String): Unit = {
    it should s"translate a $error error to a response" in {
      assert(RecountLambdaErrorResponseTransformer.responseFor(error) === RecountLambdaErrorResponseTransformer.badRequestResponse(expectedMessage))
    }
  }

  behaviour of "a recount lambda"

  errorResponseTest(
    error = RecountComputationError(InvalidCandidateIds(Set(CandidateDetails.Id(99999991), CandidateDetails.Id(99999992)))),
    expectedMessage = """Invalid candidate ids [99999991, 99999992]"""
  )

  errorResponseTest(
    error = RecountDataBucketUndefined(),
    expectedMessage = "Recount data bucket was undefined",
  )

  errorResponseTest(
    error = RecountComputationError(RunRecount.Error.PerformRecountException(new RuntimeException())),
    expectedMessage = "An error occurred while performing the recount computation",
  )

  errorResponseTest(
    error = WriteRecountError(new RuntimeException()),
    expectedMessage = "An error occurred while writing the recount result",
  )
}

package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.RecountRequestError._
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError._
import au.id.tmm.ausvotes.shared.recountresources.entities.PreferenceTreeCache.PreferenceTreeCacheException
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountLambdaErrorResponseTransformerSpec extends ImprovedFlatSpec {
  def errorResponseTest(error: RecountLambdaError, expectedMessage: String): Unit = {
    it should s"translate a $error error to a response" in {
      assert(RecountLambdaErrorResponseTransformer.responseFor(error) === RecountLambdaErrorResponseTransformer.badRequestResponse(expectedMessage))
    }
  }

  behaviour of "a recount lambda"

  errorResponseTest(
    error = InvalidCandidateIds(Set("invalid1", "invalid2")),
    expectedMessage = """Invalid candidate ids ["invalid1", "invalid2"]"""
  )

  errorResponseTest(
    error = RecountDataBucketUndefined,
    expectedMessage = "Recount data bucket was undefined",
  )

  errorResponseTest(
    error = EntityFetchError(PreferenceTreeCacheException.PreferenceTreeFetchException(new RuntimeException())),
    expectedMessage = "An error occurred while fetching the entities",
  )

  errorResponseTest(
    error = EntityCachePopulationError(PreferenceTreeCacheException.PreferenceTreeFetchException(new RuntimeException())),
    expectedMessage = "An error occurred while fetching the entities",
  )

  errorResponseTest(
    error = RecountComputationError(new RuntimeException()),
    expectedMessage = "An error occurred while performing the recount computation",
  )

  errorResponseTest(
    error = WriteRecountError(new RuntimeException()),
    expectedMessage = "An error occurred while writing the recount result",
  )
}

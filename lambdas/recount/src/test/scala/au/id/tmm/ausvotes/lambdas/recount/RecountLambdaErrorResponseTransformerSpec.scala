package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.ConfigurationError.RecountDataBucketUndefined
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.EntityFetchError._
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.{RecountComputationError, WriteRecountError}
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.RecountRequestError._
import au.id.tmm.ausvotes.shared.recountresources.RecountRequest.Error._
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountLambdaErrorResponseTransformerSpec extends ImprovedFlatSpec {
  def errorResponseTest(error: RecountLambdaError, expectedMessage: String): Unit = {
    it should s"translate a $error error to a response" in {
      assert(RecountLambdaErrorResponseTransformer.responseFor(error) === RecountLambdaErrorResponseTransformer.badRequestResponse(expectedMessage))
    }
  }

  behaviour of "a recount lambda"

  errorResponseTest(
    error = RecountRequestParseError(MissingElection),
    expectedMessage = "Election was not specified",
  )

  errorResponseTest(
    error = RecountRequestParseError(InvalidElectionId("invalid")),
    expectedMessage = """Unrecognised election id "invalid"""",
  )

  errorResponseTest(
    error = RecountRequestParseError(MissingState),
    expectedMessage = "State was not specified",
  )

  errorResponseTest(
    error = RecountRequestParseError(InvalidStateId("invalid")),
    expectedMessage = """Unrecognised state id "invalid"""",
  )

  errorResponseTest(
    error = RecountRequestParseError(NoElectionForState(SenateElection.`2014 WA`, State.SA)),
    expectedMessage = """The election "2014WA" did not have an election for state "SA"""",
  )

  errorResponseTest(
    error = RecountRequestParseError(InvalidNumVacancies("invalid")),
    expectedMessage = """Invalid number of vacancies "invalid"""",
  )

  errorResponseTest(
    error = InvalidCandidateIds(Set("invalid1", "invalid2")),
    expectedMessage = """Invalid candidate ids ["invalid1", "invalid2"]"""
  )

  errorResponseTest(
    error = RecountDataBucketUndefined,
    expectedMessage = "Recount data bucket was undefined",
  )

  errorResponseTest(
    error = GroupFetchError(new RuntimeException()),
    expectedMessage = "An error occurred while fetching the groups",
  )

  errorResponseTest(
    error = GroupDecodeError("the message"),
    expectedMessage = """An error occurred while decoding the groups: "the message"""",
  )

  errorResponseTest(
    error = CandidateFetchError(new RuntimeException()),
    expectedMessage = "An error occurred while fetching the candidates",
  )

  errorResponseTest(
    error = CandidateDecodeError("the message"),
    expectedMessage = """An error occurred while decoding the candidates: "the message"""",
  )

  errorResponseTest(
    error = PreferenceTreeFetchError(new RuntimeException()),
    expectedMessage = "An error occurred while fetching or decoding the preference tree",
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

package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.ConfigurationError.RecountDataBucketUndefined
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.EntityFetchError._
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.RecountRequestError._
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError._
import au.id.tmm.ausvotes.lambdas.utils.LambdaResponse
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountLambdaSpec extends ImprovedFlatSpec {

  private val sut = new RecountLambda()

  private def badRequestResponse(message: String): LambdaResponse = LambdaResponse(
    statusCode = 400,
    headers = Map.empty,
    body = jObjectFields(
      "message" -> jString(message),
    )
  )

  def errorResponseTest(error: RecountLambdaError, expectedMessage: String): Unit = {
    it should s"translate a ${error.getClass.getName} to a response" in {
      assert(sut.transformError(error) === badRequestResponse(expectedMessage))
    }
  }

  behaviour of "a recount lambda"

  errorResponseTest(
    error = MissingElection,
    expectedMessage = "Election was not specified",
  )

  errorResponseTest(
    error = InvalidElectionId("invalid"),
    expectedMessage = """Unrecognised election id "invalid"""",
  )

  errorResponseTest(
    error = MissingState,
    expectedMessage = "State was not specified",
  )

  errorResponseTest(
    error = InvalidStateId("invalid"),
    expectedMessage = """Unrecognised state id "invalid"""",
  )

  errorResponseTest(
    error = NoElectionForState(SenateElection.`2014 WA`, State.SA),
    expectedMessage = """The election "2014WA" did not have an election for state "SA"""",
  )

  errorResponseTest(
    error = InvalidNumVacancies("invalid"),
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

}

package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.lambdas.recount.Errors.RecountRequestError
import au.id.tmm.ausvotes.lambdas.utils.LambdaRequest
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountRequestSpec extends ImprovedFlatSpec {

  private def actualRecountRequestFrom(lambdaRequest: LambdaRequest): Either[RecountRequestError, RecountRequest] =
    RecountRequest.fromRequest(lambdaRequest)

  "a recount request" can "be parsed from a lambda request" in {
    val request = LambdaRequestFixture.lambdaRequest()

    val expectedRecountRequest = RecountRequest(
      SenateElection.`2016`,
      State.VIC,
      vacancies = 12,
      ineligibleCandidateAecIds = Set("123", "456", "789"),
    )

    assert(actualRecountRequestFrom(request) === Right(expectedRecountRequest))
  }

  it must "have an election" in {
    val request = LambdaRequestFixture.lambdaRequest(pathParameters = Map("state" -> "VIC"))

    assert(actualRecountRequestFrom(request) === Left(RecountRequestError.MissingElection))
  }

  it must "have a valid election" in {
    val request = LambdaRequestFixture.lambdaRequest(pathParameters = Map("election" -> "invalid", "state" -> "VIC"))

    assert(actualRecountRequestFrom(request) === Left(RecountRequestError.InvalidElectionId("invalid")))
  }

  it must "have a state" in {
    val request = LambdaRequestFixture.lambdaRequest(pathParameters = Map("election" -> "2016"))

    assert(actualRecountRequestFrom(request) === Left(RecountRequestError.MissingState))
  }

  it must "have a valid state" in {
    val request = LambdaRequestFixture.lambdaRequest(pathParameters = Map("election" -> "2016", "state" -> "invalid"))

    assert(actualRecountRequestFrom(request) === Left(RecountRequestError.InvalidStateId("invalid")))
  }

  it must "have a state which had an election at the given election" in {
    val request = LambdaRequestFixture.lambdaRequest(pathParameters = Map("election" -> "2014WA", "state" -> "VIC"))

    assert(actualRecountRequestFrom(request) === Left(RecountRequestError.NoElectionForState(SenateElection.`2014 WA`, State.VIC)))
  }

  it should "default to the number of vacancies for that state" in {
    val request = LambdaRequestFixture.lambdaRequest(queryStringParameters = Map())

    assert(actualRecountRequestFrom(request).map(_.vacancies) === Right(12))
  }

  it must "not have zero vacancies" in {
    val request = LambdaRequestFixture.lambdaRequest(queryStringParameters = Map("vacancies" -> "0"))

    assert(actualRecountRequestFrom(request).map(_.vacancies) === Left(RecountRequestError.InvalidNumVacancies("0")))
  }

  it must "not have a negative number of vacancies" in {
    val request = LambdaRequestFixture.lambdaRequest(queryStringParameters = Map("vacancies" -> "-1"))

    assert(actualRecountRequestFrom(request).map(_.vacancies) === Left(RecountRequestError.InvalidNumVacancies("-1")))
  }

  it must "not have an invalid number of vacancies" in {
    val request = LambdaRequestFixture.lambdaRequest(queryStringParameters = Map("vacancies" -> "invalid"))

    assert(actualRecountRequestFrom(request).map(_.vacancies) === Left(RecountRequestError.InvalidNumVacancies("invalid")))
  }

  it should "default to no ineligible candidates" in {
    val request = LambdaRequestFixture.lambdaRequest(queryStringParameters = Map())

    assert(actualRecountRequestFrom(request).map(_.ineligibleCandidateAecIds) === Right(Set.empty))
  }

  it should "suppress empty ineligible candidate ids" in {
    val request = LambdaRequestFixture.lambdaRequest(queryStringParameters = Map("ineligibleCandidates" -> "123,,456"))

    assert(actualRecountRequestFrom(request).map(_.ineligibleCandidateAecIds) === Right(Set("123", "456")))
  }

}

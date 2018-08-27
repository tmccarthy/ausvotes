package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.lambdas.recount.RecountLambda.SnsMessage
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.RecountRequestError
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountRequestSpec extends ImprovedFlatSpec {

  private def actualRecountRequestFrom(lambdaRequest: RecountLambda.SnsMessage): Either[RecountRequestError, RecountRequest] =
    RecountRequest.fromRequest(lambdaRequest)

  "a recount request" can "be parsed from a lambda request" in {
    val request = snsMessageFixture()

    val expectedRecountRequest = RecountRequest(
      SenateElection.`2016`,
      State.VIC,
      vacancies = 12,
      ineligibleCandidateAecIds = Set("123", "456", "789"),
    )

    assert(actualRecountRequestFrom(request) === Right(expectedRecountRequest))
  }

  it must "have an election" in {
    val request = snsMessageFixture(election = None)

    assert(actualRecountRequestFrom(request) === Left(RecountRequestError.MissingElection))
  }

  it must "have a valid election" in {
    val request = snsMessageFixture(election = Some("invalid"))

    assert(actualRecountRequestFrom(request) === Left(RecountRequestError.InvalidElectionId("invalid")))
  }

  it must "have a state" in {
    val request = snsMessageFixture(state = None)

    assert(actualRecountRequestFrom(request) === Left(RecountRequestError.MissingState))
  }

  it must "have a valid state" in {
    val request = snsMessageFixture(state = Some("invalid"))

    assert(actualRecountRequestFrom(request) === Left(RecountRequestError.InvalidStateId("invalid")))
  }

  it must "have a state which had an election at the given election" in {
    val request = snsMessageFixture(election = Some("2014WA"), state = Some("VIC"))

    assert(actualRecountRequestFrom(request) === Left(RecountRequestError.NoElectionForState(SenateElection.`2014 WA`, State.VIC)))
  }

  it should "default to the number of vacancies for that state" in {
    val request = snsMessageFixture(vacancies = None)

    assert(actualRecountRequestFrom(request).map(_.vacancies) === Right(12))
  }

  it must "not have zero vacancies" in {
    val request = snsMessageFixture(vacancies = Some("0"))

    assert(actualRecountRequestFrom(request).map(_.vacancies) === Left(RecountRequestError.InvalidNumVacancies("0")))
  }

  it must "not have a negative number of vacancies" in {
    val request = snsMessageFixture(vacancies = Some("-1"))

    assert(actualRecountRequestFrom(request).map(_.vacancies) === Left(RecountRequestError.InvalidNumVacancies("-1")))
  }

  it must "not have an invalid number of vacancies" in {
    val request = snsMessageFixture(vacancies = Some("invalid"))

    assert(actualRecountRequestFrom(request).map(_.vacancies) === Left(RecountRequestError.InvalidNumVacancies("invalid")))
  }

  it should "default to no ineligible candidates" in {
    val request = snsMessageFixture(ineligibleCandidateAecIds = None)

    assert(actualRecountRequestFrom(request).map(_.ineligibleCandidateAecIds) === Right(Set.empty))
  }

  it should "suppress empty ineligible candidate ids" in {
    val request = snsMessageFixture(ineligibleCandidateAecIds = Some("123,,456"))

    assert(actualRecountRequestFrom(request).map(_.ineligibleCandidateAecIds) === Right(Set("123", "456")))
  }

  private def snsMessageFixture(
                                 election: Option[String] = Some("2016"),
                                 state: Option[String] = Some("VIC"),
                                 vacancies: Option[String] = Some("12"),
                                 ineligibleCandidateAecIds: Option[String] = Some("123,456,789")
                               ): SnsMessage = SnsMessage(election, state, vacancies, ineligibleCandidateAecIds)

}

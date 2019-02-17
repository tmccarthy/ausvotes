package au.id.tmm.ausvotes.api.model.recount

import au.id.tmm.ausvotes.model.CandidateDetails
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountApiRequestSpec extends ImprovedFlatSpec {

  private def actualRecountRequestGiven(
                                         election: String = "2016",
                                         state: String = "VIC",
                                         vacancies: Option[String] = Some("12"),
                                         ineligibleCandidateAecIds: Option[String] = Some("123,456,789"),
                                         roundingFlag: Option[String] = Some("true")
                                       ): Either[RecountApiRequest.ConstructionException, RecountApiRequest] =
    RecountApiRequest.buildFrom(election, state, vacancies, ineligibleCandidateAecIds, roundingFlag)

  "a recount request" can "be parsed from a lambda request" in {
    val request = actualRecountRequestGiven()

    val expectedRecountRequest = RecountApiRequest(
      SenateElection.`2016`.electionForState(State.VIC).get,
      numVacancies = Some(12),
      ineligibleCandidates = Some(Set(CandidateDetails.Id(123), CandidateDetails.Id(456), CandidateDetails.Id(789))),
      doRounding = Some(true),
    )

    assert(request === Right(expectedRecountRequest))
  }

  it must "have a valid election" in {
    val request = actualRecountRequestGiven(election = "invalid")

    assert(request === Left(RecountApiRequest.ConstructionException.InvalidElectionId("invalid")))
  }

  it must "have a valid state" in {
    val request = actualRecountRequestGiven(state = "invalid")

    assert(request === Left(RecountApiRequest.ConstructionException.InvalidStateId("invalid")))
  }

  it must "have a state which had an election at the given election" in {
    val request = actualRecountRequestGiven(election = "2014WA", state = "VIC")

    assert(request === Left(RecountApiRequest.ConstructionException.NoElectionForState(SenateElection.`2014 WA`, State.VIC)))
  }

  it must "not have zero vacancies" in {
    val request = actualRecountRequestGiven(vacancies = Some("0"))

    assert(request === Left(RecountApiRequest.ConstructionException.InvalidNumVacancies("0")))
  }

  it must "not have a negative number of vacancies" in {
    val request = actualRecountRequestGiven(vacancies = Some("-1"))

    assert(request === Left(RecountApiRequest.ConstructionException.InvalidNumVacancies("-1")))
  }

  it must "not have an invalid number of vacancies" in {
    val request = actualRecountRequestGiven(vacancies = Some("invalid"))

    assert(request === Left(RecountApiRequest.ConstructionException.InvalidNumVacancies("invalid")))
  }

  it must "not have an invalid rounding flag" in {
    val request = actualRecountRequestGiven(roundingFlag = Some("invalid"))

    assert(request === Left(RecountApiRequest.ConstructionException.InvalidRoundingFlag("invalid")))
  }

  it should "suppress empty ineligible candidate ids" in {
    val request = actualRecountRequestGiven(ineligibleCandidateAecIds = Some("123,,456"))

    assert(request.map(_.ineligibleCandidates) === Right(Some(Set(CandidateDetails.Id(123), CandidateDetails.Id(456)))))
  }

  it should "reject invalid ineligible candidate ids" in {
    val request = actualRecountRequestGiven(ineligibleCandidateAecIds = Some("invalid,,invalid2"))

    assert(request.map(_.ineligibleCandidates) === Left(RecountApiRequest.ConstructionException.InvalidCandidateIds(Set("invalid", "invalid2"))))
  }

}

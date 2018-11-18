package au.id.tmm.ausvotes.api.model.recount

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountApiRequestSpec extends ImprovedFlatSpec {

  private def actualRecountRequestGiven(
                                         election: String = "2016",
                                         state: String = "VIC",
                                         vacancies: Option[String] = Some("12"),
                                         ineligibleCandidateAecIds: Option[String] = Some("123,456,789"),
                                       ): Either[RecountApiRequest.ConstructionException, RecountApiRequest] =
    RecountApiRequest.buildFrom(election, state, vacancies, ineligibleCandidateAecIds)

  "a recount request" can "be parsed from a lambda request" in {
    val request = actualRecountRequestGiven()

    val expectedRecountRequest = RecountApiRequest(
      SenateElection.`2016`,
      State.VIC,
      numVacancies = Some(12),
      ineligibleCandidates = Some(Set(AecCandidateId("123"), AecCandidateId("456"), AecCandidateId("789"))),
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

  it should "suppress empty ineligible candidate ids" in {
    val request = actualRecountRequestGiven(ineligibleCandidateAecIds = Some("123,,456"))

    assert(request.map(_.ineligibleCandidates) === Right(Some(Set(AecCandidateId("123"), AecCandidateId("456")))))
  }

  "a InvalidElectionId error" should "have a human readable representation" in {
    val badElectionId = "invalid"

    assert(
      RecountApiRequest.ConstructionException.humanReadableMessageFor(RecountApiRequest.ConstructionException.InvalidElectionId(badElectionId)) ===
        s"""Unrecognised election id "$badElectionId""""
    )
  }

  "a InvalidStateId(badStateId) error" should "have a human readable representation" in {
    val badStateId = "invalid"

    assert(
      RecountApiRequest.ConstructionException.humanReadableMessageFor(RecountApiRequest.ConstructionException.InvalidStateId(badStateId)) ===
        s"""Unrecognised state id "$badStateId""""
    )
  }

  "a NoElectionForState(election, state) error" should "have a human readable representation" in {
    val election = SenateElection.`2014 WA`
    val state = State.SA

    assert(
      RecountApiRequest.ConstructionException.humanReadableMessageFor(RecountApiRequest.ConstructionException.NoElectionForState(election, state)) ===
        s"""The election "${election.id}" did not have an election for state "${state.abbreviation}""""
    )
  }

  "a InvalidNumVacancies(badNumVacancies) error" should "have a human readable representation" in {
    val badNumVacancies = "-1"

    assert(
      RecountApiRequest.ConstructionException.humanReadableMessageFor(RecountApiRequest.ConstructionException.InvalidNumVacancies(badNumVacancies)) ===
        s"""Invalid number of vacancies "$badNumVacancies""""
    )
  }

}

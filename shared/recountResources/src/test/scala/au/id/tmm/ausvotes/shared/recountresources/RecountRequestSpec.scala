package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountRequestSpec extends ImprovedFlatSpec {

  private def actualRecountRequestGiven(
                                         election: Option[String] = Some("2016"),
                                         state: Option[String] = Some("VIC"),
                                         vacancies: Option[String] = Some("12"),
                                         ineligibleCandidateAecIds: Option[String] = Some("123,456,789"),
                                       ): Either[RecountRequest.Error, RecountRequest] =
    RecountRequest.build(election, state, vacancies, ineligibleCandidateAecIds)

  "a recount request" can "be parsed from a lambda request" in {
    val request = actualRecountRequestGiven()

    val expectedRecountRequest = RecountRequest(
      SenateElection.`2016`,
      State.VIC,
      vacancies = 12,
      ineligibleCandidateAecIds = Set(AecCandidateId("123"), AecCandidateId("456"), AecCandidateId("789")),
    )

    assert(request === Right(expectedRecountRequest))
  }

  it must "have an election" in {
    val request = actualRecountRequestGiven(election = None)

    assert(request === Left(RecountRequest.Error.MissingElection))
  }

  it must "have a valid election" in {
    val request = actualRecountRequestGiven(election = Some("invalid"))

    assert(request === Left(RecountRequest.Error.InvalidElectionId("invalid")))
  }

  it must "have a state" in {
    val request = actualRecountRequestGiven(state = None)

    assert(request === Left(RecountRequest.Error.MissingState))
  }

  it must "have a valid state" in {
    val request = actualRecountRequestGiven(state = Some("invalid"))

    assert(request === Left(RecountRequest.Error.InvalidStateId("invalid")))
  }

  it must "have a state which had an election at the given election" in {
    val request = actualRecountRequestGiven(election = Some("2014WA"), state = Some("VIC"))

    assert(request === Left(RecountRequest.Error.NoElectionForState(SenateElection.`2014 WA`, State.VIC)))
  }

  it should "default to the number of vacancies for that state" in {
    val request = actualRecountRequestGiven(vacancies = None)

    assert(request.map(_.vacancies) === Right(12))
  }

  it must "not have zero vacancies" in {
    val request = actualRecountRequestGiven(vacancies = Some("0"))

    assert(request.map(_.vacancies) === Left(RecountRequest.Error.InvalidNumVacancies("0")))
  }

  it must "not have a negative number of vacancies" in {
    val request = actualRecountRequestGiven(vacancies = Some("-1"))

    assert(request.map(_.vacancies) === Left(RecountRequest.Error.InvalidNumVacancies("-1")))
  }

  it must "not have an invalid number of vacancies" in {
    val request = actualRecountRequestGiven(vacancies = Some("invalid"))

    assert(request.map(_.vacancies) === Left(RecountRequest.Error.InvalidNumVacancies("invalid")))
  }

  it should "default to no ineligible candidates" in {
    val request = actualRecountRequestGiven(ineligibleCandidateAecIds = None)

    assert(request.map(_.ineligibleCandidateAecIds) === Right(Set.empty))
  }

  it should "suppress empty ineligible candidate ids" in {
    val request = actualRecountRequestGiven(ineligibleCandidateAecIds = Some("123,,456"))

    assert(request.map(_.ineligibleCandidateAecIds) === Right(Set(AecCandidateId("123"), AecCandidateId("456"))))
  }

  "a MissingElection error" should "have a human readable representation" in {
    assert(
      RecountRequest.Error.humanReadableMessageFor(RecountRequest.Error.MissingElection) ===
      "Election was not specified"
    )
  }

  "a InvalidElectionId error" should "have a human readable representation" in {
    val badElectionId = "invalid"

    assert(
      RecountRequest.Error.humanReadableMessageFor(RecountRequest.Error.InvalidElectionId(badElectionId)) ===
      s"""Unrecognised election id "$badElectionId""""
    )
  }

  "a MissingState error" should "have a human readable representation" in {
    assert(
      RecountRequest.Error.humanReadableMessageFor(RecountRequest.Error.MissingState) ===
      "State was not specified"
    )
  }

  "a InvalidStateId(badStateId) error" should "have a human readable representation" in {
    val badStateId = "invalid"

    assert(
      RecountRequest.Error.humanReadableMessageFor(RecountRequest.Error.InvalidStateId(badStateId)) ===
      s"""Unrecognised state id "$badStateId""""
    )
  }

  "a NoElectionForState(election, state) error" should "have a human readable representation" in {
    val election = SenateElection.`2014 WA`
    val state = State.SA

    assert(
      RecountRequest.Error.humanReadableMessageFor(RecountRequest.Error.NoElectionForState(election, state)) ===
      s"""The election "${election.id}" did not have an election for state "${state.abbreviation}""""
    )
  }

  "a InvalidNumVacancies(badNumVacancies) error" should "have a human readable representation" in {
    val badNumVacancies = "-1"

    assert(
      RecountRequest.Error.humanReadableMessageFor(RecountRequest.Error.InvalidNumVacancies(badNumVacancies)) ===
      s"""Invalid number of vacancies "$badNumVacancies""""
    )
  }

}

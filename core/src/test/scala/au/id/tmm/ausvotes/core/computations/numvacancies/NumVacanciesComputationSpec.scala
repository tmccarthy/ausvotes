package au.id.tmm.ausvotes.core.computations.numvacancies

import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class NumVacanciesComputationSpec extends ImprovedFlatSpec {

  "the numVacancies computation" should "indicate the number of vacancies for a state at a normal election" in {
    assert(
      NumVacanciesComputation.numVacanciesFor(SenateElection.`2013`.electionForState(State.SA).get) === 6
    )
  }

  it should "indicate the number of vacancies for a territory at a normal election" in {
    assert(
      NumVacanciesComputation.numVacanciesFor(SenateElection.`2013`.electionForState(State.NT).get) === 2
    )
  }

  it should "indicate the number of vacancies for a state at a double dissolution election" in {
    assert(
      NumVacanciesComputation.numVacanciesFor(SenateElection.`2016`.electionForState(State.WA).get) === 12
    )
  }

  it should "indicate the number of vacancies for a territory at a double dissolution election" in {
    assert(
      NumVacanciesComputation.numVacanciesFor(SenateElection.`2016`.electionForState(State.ACT).get) === 2
    )
  }

}

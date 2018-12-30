package au.id.tmm.ausvotes.core.computations.numvacancies

import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class NumVacanciesComputationSpec extends ImprovedFlatSpec {

  "the numVacancies computation" should "indicate the number of vacancies for a state at a normal election" in {
    assert(
      NumVacanciesComputation.numVacanciesFor(SenateElectionForState(SenateElection.`2013`, State.SA).right.get) ===
        Right(6)
    )
  }

  it should "indicate the number of vacancies for a territory at a normal election" in {
    assert(
      NumVacanciesComputation.numVacanciesFor(SenateElectionForState(SenateElection.`2013`, State.NT).right.get) ===
        Right(2)
    )
  }

  it should "indicate the number of vacancies for a state at a double dissolution election" in {
    assert(
      NumVacanciesComputation.numVacanciesFor(SenateElectionForState(SenateElection.`2016`, State.WA).right.get) ===
        Right(12)
    )
  }

  it should "indicate the number of vacancies for a territory at a double dissolution election" in {
    assert(
      NumVacanciesComputation.numVacanciesFor(SenateElectionForState(SenateElection.`2016`, State.ACT).right.get) ===
        Right(2)
    )
  }

}

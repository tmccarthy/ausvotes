package au.id.tmm.ausvotes.core.computations.numvacancies

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class NumVacanciesComputationSpec extends ImprovedFlatSpec {

  "the numVacancies computation" should "indicate if no election was held for that state at the election" in {
    assert(
      NumVacanciesComputation.numVacanciesForStateAtElection(SenateElection.`2014 WA`, State.VIC) ===
        Left(NumVacanciesComputation.NoElection)
    )
  }

  it should "indicate the number of vacancies for a state at a normal election" in {
    assert(
      NumVacanciesComputation.numVacanciesForStateAtElection(SenateElection.`2013`, State.SA) ===
        Right(6)
    )
  }

  it should "indicate the number of vacancies for a territory at a normal election" in {
    assert(
      NumVacanciesComputation.numVacanciesForStateAtElection(SenateElection.`2013`, State.NT) ===
        Right(2)
    )
  }

  it should "indicate the number of vacancies for a state at a double dissolution election" in {
    assert(
      NumVacanciesComputation.numVacanciesForStateAtElection(SenateElection.`2016`, State.WA) ===
        Right(12)
    )
  }

  it should "indicate the number of vacancies for a territory at a double dissolution election" in {
    assert(
      NumVacanciesComputation.numVacanciesForStateAtElection(SenateElection.`2016`, State.ACT) ===
        Right(2)
    )
  }

}

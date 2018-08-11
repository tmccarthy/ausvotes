package au.id.tmm.ausvotes.core.computations.numvacancies

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

object NumVacanciesComputation {

  case object NoElection

  def numVacanciesForStateAtElection(election: SenateElection, state: State): Either[NoElection.type, Int] = {
    if (!election.states.contains(state)) {
      Left(NoElection)
    } else if (state.isTerritory) {
      Right(2)
    } else if (election.doubleDissolution) {
      Right(12)
    } else {
      Right(6)
    }
  }

}

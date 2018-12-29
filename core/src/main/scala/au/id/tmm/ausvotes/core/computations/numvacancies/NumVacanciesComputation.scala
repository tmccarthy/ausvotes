package au.id.tmm.ausvotes.core.computations.numvacancies

import au.id.tmm.ausvotes.model.federal.senate.SenateElection.SenateElectionType._
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import au.id.tmm.utilities.geo.australia.State.{StateProper, Territory}

object NumVacanciesComputation {

  case object NoElection

  def numVacanciesFor(election: SenateElectionForState): Int = {
    (election.election.senateElectionType, election.state) match {
      case (FullSenate, _: StateProper) => 12
      case (HalfSenate, _: StateProper) => 6
      case (_,          _: Territory  ) => 2
    }
  }

}

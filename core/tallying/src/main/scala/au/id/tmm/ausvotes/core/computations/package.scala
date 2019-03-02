package au.id.tmm.ausvotes.core

import au.id.tmm.ausvotes.model.federal.FederalBallotJurisdiction
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotId, SenateElectionForState}

package object computations {
  type SenateBallotWithFacts = StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]
}

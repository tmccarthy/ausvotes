package au.id.tmm.ausvotes.core

import au.id.tmm.ausvotes.model.federal.FederalBallotJurisdiction
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotId, SenateElectionForState}
import au.id.tmm.ausvotes.model.nsw

package object computations {
  type SenateBallotWithFacts = StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]
  type NswLegCoBallotWithFacts = StvBallotWithFacts[nsw.legco.NswLegCoElection, nsw.legco.BallotJurisdiction, nsw.legco.BallotId]
}

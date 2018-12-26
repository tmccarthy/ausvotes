package au.id.tmm.ausvotes.model.federal

import au.id.tmm.ausvotes.model.Candidate
import au.id.tmm.ausvotes.model.stv.{Ballot, StvCandidate}

package object senate {

  type SenateCandidateDetails = Candidate[SenateElectionForState]
  type SenateCandidate = StvCandidate[SenateElectionForState, SenateCandidateDetails]
  type SenateBallot = Ballot[SenateElectionForState, SenateCandidate, FederalBallotJurisdiction, SenateBallotId]

}

package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.stv.{Ballot, StvCandidate}

package object federal {

  type Division = Electorate[FederalElection]
  type FederalVcp = VoteCollectionPoint[FederalElection, FederalVoteCollectionPointJurisdiction]

  type SenateCandidateDetails = Candidate[SenateElectionForState]
  type SenateCandidate = StvCandidate[SenateElectionForState, SenateCandidateDetails]
  type SenateBallot = Ballot[SenateElectionForState, SenateCandidate, FederalBallotJurisdiction, SenateBallotId]

}

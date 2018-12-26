package au.id.tmm.ausvotes.model.federal

import au.id.tmm.ausvotes.model.stv.{Ballot, CountData, Group, StvCandidate}
import au.id.tmm.ausvotes.model.{Candidate, HowToVoteCard}

package object senate {

  type SenateCandidateDetails = Candidate[SenateElectionForState]
  type SenateCandidate = StvCandidate[SenateElectionForState, SenateCandidateDetails]
  type SenateBallot = Ballot[SenateElectionForState, SenateCandidate, FederalBallotJurisdiction, SenateBallotId]

  type SenateGroup = Group[SenateElectionForState]
  type SenateHtv = HowToVoteCard[SenateElectionForState, SenateGroup]

  type SenateCountData = CountData[SenateElectionForState, SenateCandidate]

}

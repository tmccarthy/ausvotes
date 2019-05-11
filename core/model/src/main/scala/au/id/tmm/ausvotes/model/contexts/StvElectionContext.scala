package au.id.tmm.ausvotes.model.contexts

import au.id.tmm.ausvotes.model
import au.id.tmm.ausvotes.model.stv
import au.id.tmm.countstv.normalisation.Preference

trait StvElectionContext[E, ElectorateJurisdiction, VcpJurisdiction, BallotJurisdiction, BallotId] {

  type CandidateDetails = model.CandidateDetails[E]
  type Candidate = stv.StvCandidate[E]
  type CandidatePosition = stv.CandidatePosition[E]

  type Ballot = stv.Ballot[E, BallotJurisdiction, BallotId]
  type NormalisedBallot = stv.NormalisedBallot[E]

  type BallotGroup = stv.BallotGroup[E]
  type Group = stv.Group[E]
  type Ungrouped = stv.Ungrouped[E]

  type Htv = model.HowToVoteCard[E, Group]

  type CountData = stv.CountData[E]

  type AtlPreferences = Map[Group, Preference]
  type BtlPreferences = Map[Candidate, Preference]

}

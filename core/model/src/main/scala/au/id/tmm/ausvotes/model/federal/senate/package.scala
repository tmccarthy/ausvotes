package au.id.tmm.ausvotes.model.federal

import au.id.tmm.ausvotes.model._
import au.id.tmm.ausvotes.model.stv.BallotGroup.Code
import au.id.tmm.ausvotes.model.stv.Group.InvalidGroupCode
import au.id.tmm.ausvotes.model.stv._
import au.id.tmm.countstv.model.CompletedCount

package object senate {

  type SenateCandidateDetails = Candidate[SenateElectionForState]
  def SenateCandidateDetails(
                              election: SenateElectionForState,
                              name: Name,
                              party: Option[Party],
                              id: Candidate.Id,
                            ): SenateCandidateDetails = Candidate(election, name, party, id)

  type SenateCandidate = StvCandidate[SenateElectionForState, SenateCandidateDetails]
  def SenateCandidate(
                       election: SenateElectionForState,
                       candidate: SenateCandidateDetails,
                       position: CandidatePosition[SenateElectionForState],
                     ): SenateCandidate = StvCandidate(election, candidate, position)

  type SenateCandidatePosition = CandidatePosition[SenateElectionForState]
  def SenateCandidatePosition(
                               ballotGroup: SenateBallotGroup,
                               indexInGroup: Int,
                             ): SenateCandidatePosition = CandidatePosition(ballotGroup, indexInGroup)

  type SenateBallot = Ballot[SenateElectionForState, SenateCandidate, FederalBallotJurisdiction, SenateBallotId]
  def SenateBallot(
                    election: SenateElectionForState,
                    jurisdiction: FederalBallotJurisdiction,
                    id: SenateBallotId,
                    groupPreferences: Map[SenateGroup, Preference],
                    candidatePreferences: Map[SenateCandidate, Preference],
                  ): SenateBallot = Ballot(election, jurisdiction, id, groupPreferences, candidatePreferences)

  type SenateBallotGroup = BallotGroup[SenateElectionForState]

  type SenateGroup = Group[SenateElectionForState]
  def SenateGroup(
                   election: SenateElectionForState,
                   code: Code,
                   party: Option[Party],
                 ): Either[InvalidGroupCode.type, SenateGroup] = Group(election, code, party)

  type SenateUngrouped = Ungrouped[SenateElectionForState]
  def SenateUngrouped(electionForState: SenateElectionForState): SenateUngrouped = Ungrouped(electionForState)

  type SenateHtv = HowToVoteCard[SenateElectionForState, SenateGroup]
  def SenateHtv(
                 election: SenateElectionForState,
                 issuer: SenateGroup,
                 suggestedOrder: Vector[SenateGroup],
               ): SenateHtv = HowToVoteCard(election, issuer, suggestedOrder)

  type SenateCountData = CountData[SenateElectionForState, SenateCandidate]
  def SenateCountData(
                       election: SenateElectionForState,
                       completedCount: CompletedCount[SenateCandidate],
                     ): SenateCountData = CountData(election, completedCount)

  type AtlPreferences = Map[SenateGroup, Preference]
  type BtlPreferences = Map[SenateCandidate, Preference]

}

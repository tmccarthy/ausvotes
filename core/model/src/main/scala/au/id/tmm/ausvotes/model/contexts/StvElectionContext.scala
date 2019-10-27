package au.id.tmm.ausvotes.model.contexts

import au.id.tmm.ausvotes.model
import au.id.tmm.ausvotes.model.stv
import au.id.tmm.countstv.model.CompletedCount
import au.id.tmm.countstv.normalisation.{BallotNormalisation, Preference}
import cats.data.NonEmptyVector

import scala.collection.immutable.ArraySeq

trait StvElectionContext[E, ElectorateJurisdiction, BallotJurisdiction, BallotId] {

  type CandidateDetails = model.CandidateDetails[E]
  def CandidateDetails(
                        election: E,
                        name: model.Name,
                        party: Option[model.Party],
                        id: model.CandidateDetails.Id,
                      ): CandidateDetails = model.CandidateDetails(election, name, party, id)

  type Candidate = stv.StvCandidate[E]
  def Candidate(
                 election: E,
                 candidateDetails: model.CandidateDetails[E],
                 position: CandidatePosition,
               ): Candidate = stv.StvCandidate(election, candidateDetails, position)

  type CandidatePosition = stv.CandidatePosition[E]
  def CandidatePosition(
                         group: BallotGroup,
                         indexInGroup: Int,
                       ): CandidatePosition = stv.CandidatePosition(group, indexInGroup)

  type Ballot = stv.Ballot[E, BallotJurisdiction, BallotId]
  def Ballot(
              election: E,
              jurisdiction: BallotJurisdiction,
              id: BallotId,
              groupPreferences: Map[Group, Preference],
              candidatePreferences: Map[Candidate, Preference],
            ): Ballot = stv.Ballot[E, BallotJurisdiction, BallotId](election, jurisdiction, id, groupPreferences, candidatePreferences)

  type NormalisedBallot = stv.NormalisedBallot[E]
  def NormalisedBallot(
                        atl: BallotNormalisation.Result[Group],
                        atlCandidateOrder: Option[ArraySeq[Candidate]],

                        btl: BallotNormalisation.Result[Candidate],

                        canonicalOrder: Option[ArraySeq[Candidate]],
                      ): NormalisedBallot = stv.NormalisedBallot(atl, atlCandidateOrder, btl, canonicalOrder)

  type BallotGroup = stv.BallotGroup[E]

  type Group = stv.Group[E]
  def Group(
             election: E,
             code: stv.BallotGroup.Code,
             party: Option[model.Party],
           ): Either[stv.Group.InvalidGroupCode.type, Group] = stv.Group(election, code, party)

  type GroupsAndCandidates = stv.GroupsAndCandidates[E]
  def GroupsAndCandidates(
                           groups: Set[Group],
                           candidates: Set[Candidate]
                         ): GroupsAndCandidates = stv.GroupsAndCandidates(groups, candidates)

  type Ungrouped = stv.Ungrouped[E]
  def Ungrouped(election: E): Ungrouped = stv.Ungrouped(election)

  type Htv = model.HowToVoteCard[E, Group]
  def Htv(
           election: E,
           issuer: Group,
           suggestedOrder: NonEmptyVector[Group],
         ): Htv = model.HowToVoteCard(election, issuer, suggestedOrder)

  type CountData = stv.CountData[E]
  def CountData(
                 election: E,
                 completedCount: CompletedCount[Candidate],
               ): CountData = stv.CountData(election, completedCount)

  type AtlPreferences = Map[Group, Preference]

  type BtlPreferences = Map[Candidate, Preference]

}

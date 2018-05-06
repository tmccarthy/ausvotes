package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.core.model.parsing.CandidatePosition
import au.id.tmm.countstv.model.CandidateStatuses
import au.id.tmm.countstv.model.countsteps.CountSteps
import au.id.tmm.countstv.model.values.{NumPapers, NumVotes}
import au.id.tmm.utilities.geo.australia.State

final case class CountData(election: SenateElection,
                           state: State,
                           totalFormalPapers: NumPapers,
                           quota: NumVotes,
                           countSteps: CountSteps[CandidatePosition],
                          ) {
  def ineligibleCandidates: Set[CandidatePosition] = outcomes.ineligibleCandidates

  def outcomes: CandidateStatuses[CandidatePosition] = countSteps.last.candidateStatuses
}

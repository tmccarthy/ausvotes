package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.core.model.parsing.CandidatePosition
import au.id.tmm.countstv.model.{CandidateStatuses, CompletedCount}
import au.id.tmm.utilities.geo.australia.State

final case class CountData(election: SenateElection,
                           state: State,

                           completedCount: CompletedCount[CandidatePosition],
                          ) {
  def ineligibleCandidates: Set[CandidatePosition] = outcomes.ineligibleCandidates

  def outcomes: CandidateStatuses[CandidatePosition] = completedCount.outcomes
}

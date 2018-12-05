package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.countstv.model.{CandidateStatuses, CompletedCount}
import au.id.tmm.utilities.geo.australia.State

final case class CountData(election: SenateElection,
                           state: State,

                           completedCount: CompletedCount[Candidate],
                          ) {
  def ineligibleCandidates: Set[Candidate] = outcomes.ineligibleCandidates

  def outcomes: CandidateStatuses[Candidate] = completedCount.outcomes
}

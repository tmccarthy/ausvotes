package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition}
import au.id.tmm.ausvotes.shared.recountresources.RecountResult
import au.id.tmm.countstv.counting.FullCountComputation
import au.id.tmm.countstv.model.CandidateStatuses
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree
import au.id.tmm.utilities.geo.australia.State

object PerformRecount {

  def performRecount(
                      election: SenateElection,
                      state: State,

                      allCandidates: Set[Candidate],

                      preferenceTree: RootPreferenceTree[CandidatePosition],
                      ineligibleCandidates: Set[Candidate],
                      numVacancies: Int,
                    ): Either[RecountLambdaError.RecountComputationError, RecountResult] = {
    try {
      val allCandidatePositions = allCandidates.map(_.btlPosition)
      val ineligibleCandidatePositions = ineligibleCandidates.map(_.btlPosition)

      val completedCountPossibilities = FullCountComputation.runCount[CandidatePosition](
        allCandidatePositions,
        ineligibleCandidatePositions,
        numVacancies,
        preferenceTree,
      )

      val lookupCandidateByPosition = allCandidates.map { candidate =>
        candidate.btlPosition -> candidate
      }.toMap

      val completedCountPossibilitiesByCandidate = completedCountPossibilities.map { completedCount =>
        val outcomesByCandidatePosition = completedCount.outcomes

        CandidateStatuses(
          outcomesByCandidatePosition.asMap.map { case (candidatePosition, candidateOutcome) =>
            lookupCandidateByPosition(candidatePosition) -> candidateOutcome
          }
        )
      }

      Right(RecountResult(election, state, numVacancies, ineligibleCandidates, completedCountPossibilitiesByCandidate))
    } catch {
      case e: Exception => Left(RecountLambdaError.RecountComputationError(e))
    }
  }

}

package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition}
import au.id.tmm.ausvotes.shared.recountresources.CountResult
import au.id.tmm.countstv.counting.FullCountComputation
import au.id.tmm.countstv.model.CandidateStatuses
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree
import au.id.tmm.countstv.rules.RoundingRules
import au.id.tmm.utilities.geo.australia.State

object PerformRecount {

  def performRecount(
                      election: SenateElection,
                      state: State,

                      allCandidates: Set[Candidate],

                      preferenceTree: RootPreferenceTree[CandidatePosition],
                      ineligibleCandidates: Set[Candidate],
                      numVacancies: Int,
                      doRounding: Boolean,
                    ): Either[RecountLambdaError.RecountComputationError, CountResult] = {
    try {
      val allCandidatePositions = allCandidates.map(_.btlPosition)
      val ineligibleCandidatePositions = ineligibleCandidates.map(_.btlPosition)

      val completedCountPossibilities = FullCountComputation.runCount[CandidatePosition](
        allCandidatePositions,
        ineligibleCandidatePositions,
        numVacancies,
        preferenceTree,
      )(if (doRounding) RoundingRules.AEC else RoundingRules.NO_ROUNDING)

      val lookupCandidateByPosition = allCandidates.map { candidate =>
        candidate.btlPosition -> candidate
      }.toMap

      Right(
        CountResult(
          CountResult.Request(
            election, state, numVacancies, ineligibleCandidates, doRounding,
          ),
          completedCountPossibilities.map { completedCount =>
            val outcomesByCandidatePosition = completedCount.outcomes

            val candidateStatuses = CandidateStatuses(
              outcomesByCandidatePosition.asMap.map { case (candidatePosition, candidateOutcome) =>
                lookupCandidateByPosition(candidatePosition) -> candidateOutcome
              }
            )

            CountResult.Outcome(
              candidateStatuses.electedCandidates,
              completedCount.countSteps.last.candidateVoteCounts.exhausted,
              completedCount.countSteps.last.candidateVoteCounts.roundingError,
              candidateStatuses,
            )
          }
        )
      )
    } catch {
      case e: Exception => Left(RecountLambdaError.RecountComputationError(e))
    }
  }

}

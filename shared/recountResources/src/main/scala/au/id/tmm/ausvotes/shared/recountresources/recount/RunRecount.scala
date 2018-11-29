package au.id.tmm.ausvotes.shared.recountresources.recount

import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition}
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.actions.{Log, Now}
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.io.typeclasses.SyncEffects.syncException
import au.id.tmm.ausvotes.shared.io.typeclasses.{Monad, SyncEffects}
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree.FetchPreferenceTreeException
import au.id.tmm.ausvotes.shared.recountresources.{CountResult, RecountRequest}
import au.id.tmm.countstv.counting.FullCountComputation
import au.id.tmm.countstv.model.{CandidateStatuses, CompletedCount}
import au.id.tmm.countstv.rules.RoundingRules
import au.id.tmm.utilities.probabilities.ProbabilityMeasure

object RunRecount {

  def runRecountRequest[F[+_, +_] : FetchPreferenceTree : SyncEffects : Log : Now : Monad](
                                                                                            recountRequest: RecountRequest,
                                                                                          ): F[RunRecount.Error, CountResult] =
    for {
      entities <- FetchPreferenceTree.fetchGroupsCandidatesAndPreferencesFor(recountRequest.election, recountRequest.state)
        .leftMap(Error.FetchEntitiesException)

      preferenceTree = entities.preferenceTree

      allCandidates = entities.groupsAndCandidates.candidates
      ineligibleCandidates <- Monad.fromEither {
        CandidateActualisation.actualiseCandidates(allCandidates)(recountRequest.ineligibleCandidateAecIds)
          .invalidCandidateIdsOrCandidates
          .left.map(Error.InvalidCandidateIds)
      }

      // TODO produce a preference tree by Candidate rather than CandidatePosition and avoid these conversions
      completedCountPossibilities <- syncException {
        FullCountComputation.runCount[CandidatePosition](
          candidates = allCandidates.map(_.btlPosition),
          ineligibleCandidates = ineligibleCandidates.map(_.btlPosition),
          recountRequest.vacancies,
          preferenceTree,
        )(if (recountRequest.doRounding) RoundingRules.AEC else RoundingRules.NO_ROUNDING)
      }
        .timedLog(
          "PERFORM_RECOUNT",
          "election" -> recountRequest.election,
          "state" -> recountRequest.state,
          "num_ineligible_candidates" -> ineligibleCandidates.size,
          "num_vacancies" -> recountRequest.vacancies,
        )
        .leftMap(Error.PerformRecountException)

      countResult = makeCountResult(recountRequest, allCandidates, ineligibleCandidates, completedCountPossibilities)

    } yield countResult

  private def makeCountResult(
                               recountRequest: RecountRequest,
                               allCandidates: Set[Candidate],
                               ineligibleCandidates: Set[Candidate],
                               completedCountPossibilities: ProbabilityMeasure[CompletedCount[CandidatePosition]],
                             ): CountResult = {
    val lookupCandidateByPosition = allCandidates.map { candidate =>
      candidate.btlPosition -> candidate
    }.toMap

    CountResult(
      CountResult.Request(
        recountRequest.election,
        recountRequest.state,
        recountRequest.vacancies,
        ineligibleCandidates,
        recountRequest.doRounding,
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
  }

  sealed abstract class Error extends ExceptionCaseClass

  object Error {
    final case class FetchEntitiesException(cause: FetchPreferenceTreeException) extends Error with ExceptionCaseClass.WithCause
    final case class InvalidCandidateIds(invalidCandidateIds: Set[AecCandidateId]) extends Error
    final case class PerformRecountException(cause: Exception) extends Error with ExceptionCaseClass.WithCause
  }

}
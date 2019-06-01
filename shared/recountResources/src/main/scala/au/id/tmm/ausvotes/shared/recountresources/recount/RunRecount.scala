package au.id.tmm.ausvotes.shared.recountresources.recount

import au.id.tmm.ausvotes.model.CandidateDetails
import au.id.tmm.ausvotes.model.federal.senate.SenateCandidate
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.RecountRequest
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree.FetchPreferenceTreeException
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME._
import au.id.tmm.bfect.effects.{Now, Sync}
import au.id.tmm.countstv.counting.FullCountComputation
import au.id.tmm.countstv.model.{CompletedCount, CountParams}
import au.id.tmm.countstv.rules.RoundingRules
import au.id.tmm.utilities.probabilities.ProbabilityMeasure

object RunRecount {

  def runRecountRequest[F[+_, +_] : FetchPreferenceTree : Sync : Log : Now : BME]
  (
    recountRequest: RecountRequest,
  ): F[RunRecount.Error, ProbabilityMeasure[CompletedCount[SenateCandidate]]] =
    for {
      entities <- FetchPreferenceTree.fetchGroupsCandidatesAndPreferencesFor(recountRequest.election)
        .leftMap(Error.FetchEntitiesException)

      preferenceTree = entities.preferenceTree

      allCandidates = entities.groupsAndCandidates.candidates
      ineligibleCandidates <- BME.fromEither {
        CandidateActualisation.actualiseCandidates(allCandidates)(recountRequest.ineligibleCandidateAecIds)
          .invalidCandidateIdsOrCandidates
          .left.map(Error.InvalidCandidateIds)
      }

      completedCountPossibilities <- Sync.syncException {
        FullCountComputation.runCount[SenateCandidate](
          CountParams[SenateCandidate](
            allCandidates,
            ineligibleCandidates,
            recountRequest.vacancies,
            roundingRules = if (recountRequest.doRounding) RoundingRules.AEC else RoundingRules.NO_ROUNDING,
          ),
          preferenceTree,
        )
      }
        .timedLog(
          "PERFORM_RECOUNT",
          "election" -> recountRequest.election.election,
          "state" -> recountRequest.election.state,
          "num_ineligible_candidates" -> ineligibleCandidates.size,
          "num_vacancies" -> recountRequest.vacancies,
        )
        .leftMap(Error.PerformRecountException)

    } yield completedCountPossibilities

  sealed abstract class Error extends ExceptionCaseClass

  object Error {
    final case class FetchEntitiesException(cause: FetchPreferenceTreeException) extends Error with ExceptionCaseClass.WithCause
    final case class InvalidCandidateIds(invalidCandidateIds: Set[CandidateDetails.Id]) extends Error
    final case class PerformRecountException(cause: Exception) extends Error with ExceptionCaseClass.WithCause
  }

}

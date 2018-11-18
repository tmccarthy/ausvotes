package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.ausvotes.lambdas.recount.RecountLambdaError.RecountRequestError.InvalidCandidateIds

object CandidateActualisation {

  def actualiseIneligibleCandidates(
                                     ineligibleCandidateAecIds: Set[AecCandidateId],
                                     candidates: Set[Candidate],
                                   ): Either[InvalidCandidateIds, Set[Candidate]] = {
    val aecIdToCandidateLookup = candidates.groupBy(_.aecId).mapValues(_.head)

    val resolvedCandidatePerAecId = ineligibleCandidateAecIds.map(aecId => aecId -> aecIdToCandidateLookup.get(aecId))

    val unresolvedCandidates = resolvedCandidatePerAecId.collect {
      case (badAecId, None) => badAecId
    }

    if (unresolvedCandidates.nonEmpty) {
      Left(InvalidCandidateIds(unresolvedCandidates))
    } else {
      val resolvedCandidates = resolvedCandidatePerAecId.collect {
        case (_, Some(ineligibleCandidate)) => ineligibleCandidate
      }

      Right(resolvedCandidates)
    }
  }

}

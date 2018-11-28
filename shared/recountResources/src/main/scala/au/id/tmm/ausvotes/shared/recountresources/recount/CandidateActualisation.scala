package au.id.tmm.ausvotes.shared.recountresources.recount

import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId

object CandidateActualisation {

  def actualiseCandidates(allCandidates: Set[Candidate])(idsToActualise: Set[AecCandidateId]): CandidateActualisation.Result = {

    val aecIdToCandidateLookup = allCandidates.groupBy(_.aecId).mapValues(_.head)

    idsToActualise.foldLeft(Result(Set.empty, Set.empty)) { case (resultSoFar: Result, candidateId: AecCandidateId) =>
      aecIdToCandidateLookup.get(candidateId) match {
        case Some(candidate) => resultSoFar.copy(candidates = resultSoFar.candidates + candidate)
        case None => resultSoFar.copy(invalidCandidateIds = resultSoFar.invalidCandidateIds + candidateId)
      }
    }

  }

  final case class Result(candidates: Set[Candidate], invalidCandidateIds: Set[AecCandidateId]) {
    def invalidCandidateIdsOrCandidates: Either[Set[AecCandidateId], Set[Candidate]] =
      if (invalidCandidateIds.nonEmpty) Left(invalidCandidateIds) else Right(candidates)
  }

}

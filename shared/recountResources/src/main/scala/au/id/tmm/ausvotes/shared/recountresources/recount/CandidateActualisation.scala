package au.id.tmm.ausvotes.shared.recountresources.recount

import au.id.tmm.ausvotes.model.CandidateDetails
import au.id.tmm.ausvotes.model.federal.senate.SenateCandidate

object CandidateActualisation {

  def actualiseCandidates(allCandidates: Set[SenateCandidate])(idsToActualise: Set[CandidateDetails.Id]): CandidateActualisation.Result = {

    val aecIdToCandidateLookup = allCandidates.groupBy(_.candidateDetails.id).mapValues(_.head)

    idsToActualise.foldLeft(Result(Set.empty, Set.empty)) { case (resultSoFar: Result, candidateId: CandidateDetails.Id) =>
      aecIdToCandidateLookup.get(candidateId) match {
        case Some(candidate) => resultSoFar.copy(candidates = resultSoFar.candidates + candidate)
        case None => resultSoFar.copy(invalidCandidateIds = resultSoFar.invalidCandidateIds + candidateId)
      }
    }

  }

  final case class Result(candidates: Set[SenateCandidate], invalidCandidateIds: Set[CandidateDetails.Id]) {
    def invalidCandidateIdsOrCandidates: Either[Set[CandidateDetails.Id], Set[SenateCandidate]] =
      if (invalidCandidateIds.nonEmpty) Left(invalidCandidateIds) else Right(candidates)
  }

}

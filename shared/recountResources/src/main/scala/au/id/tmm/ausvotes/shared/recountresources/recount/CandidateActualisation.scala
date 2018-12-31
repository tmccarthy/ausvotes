package au.id.tmm.ausvotes.shared.recountresources.recount

import au.id.tmm.ausvotes.model.Candidate
import au.id.tmm.ausvotes.model.federal.senate.SenateCandidate

object CandidateActualisation {

  def actualiseCandidates(allCandidates: Set[SenateCandidate])(idsToActualise: Set[Candidate.Id]): CandidateActualisation.Result = {

    val aecIdToCandidateLookup = allCandidates.groupBy(_.candidate.id).mapValues(_.head)

    idsToActualise.foldLeft(Result(Set.empty, Set.empty)) { case (resultSoFar: Result, candidateId: Candidate.Id) =>
      aecIdToCandidateLookup.get(candidateId) match {
        case Some(candidate) => resultSoFar.copy(candidates = resultSoFar.candidates + candidate)
        case None => resultSoFar.copy(invalidCandidateIds = resultSoFar.invalidCandidateIds + candidateId)
      }
    }

  }

  final case class Result(candidates: Set[SenateCandidate], invalidCandidateIds: Set[Candidate.Id]) {
    def invalidCandidateIdsOrCandidates: Either[Set[Candidate.Id], Set[SenateCandidate]] =
      if (invalidCandidateIds.nonEmpty) Left(invalidCandidateIds) else Right(candidates)
  }

}

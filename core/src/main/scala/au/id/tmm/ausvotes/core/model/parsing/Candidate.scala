package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.utilities.geo.australia.State

final case class Candidate(election: SenateElection,
                           state: State,
                           aecId: AecCandidateId,
                           name: Name,
                           party: Party,
                           btlPosition: CandidatePosition)

object Candidate {
  final case class AecCandidateId(asString: String) extends AnyVal

  object AecCandidateId {
    implicit val ordering: Ordering[AecCandidateId] = Ordering.by(_.asString)
  }
}

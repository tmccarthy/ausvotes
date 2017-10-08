package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

final case class Candidate(election: SenateElection,
                           state: State,
                           aecId: String,
                           name: Name,
                           party: Party,
                           btlPosition: CandidatePosition)
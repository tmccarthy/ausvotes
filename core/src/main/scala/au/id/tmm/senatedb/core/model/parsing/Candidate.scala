package au.id.tmm.senatedb.core.model.parsing

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

final case class Candidate(election: SenateElection,
                           state: State,
                           aecId: String,
                           name: Name,
                           party: Party,
                           btlPosition: CandidatePosition)
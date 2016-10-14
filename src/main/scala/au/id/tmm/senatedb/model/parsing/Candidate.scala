package au.id.tmm.senatedb.model.parsing

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

final case class Candidate(election: SenateElection,
                           state: State,
                           aecId: String,
                           name: Name,
                           party: Option[Party],
                           btlPosition: CandidatePosition)
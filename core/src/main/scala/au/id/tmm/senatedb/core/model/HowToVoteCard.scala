package au.id.tmm.senatedb.core.model

import au.id.tmm.senatedb.core.model.parsing.Group
import au.id.tmm.utilities.geo.australia.State

final case class HowToVoteCard(election: SenateElection,
                               state: State,
                               group: Group,
                               groupOrder: Vector[Group]) {
}

package au.id.tmm.senatedb.model.parsing

import au.id.tmm.senatedb.model._
import au.id.tmm.utilities.geo.australia.State

final case class Ballot(election: SenateElection,
                        state: State,
                        division: Division,
                        voteCollectionPoint: PollingPlace,
                        batch: Int,
                        paper: Int,
                        atlPreferences: Map[Group, Preference],
                        btlPreferences: Map[CandidatePosition, Preference]
                       ) {

}
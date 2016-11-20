package au.id.tmm.senatedb.core.model.parsing

import au.id.tmm.senatedb.core.model._
import au.id.tmm.senatedb.core.model.parsing.Ballot.{AtlPreferences, BtlPreferences}
import au.id.tmm.utilities.geo.australia.State

final case class Ballot(election: SenateElection,
                        state: State,
                        division: Division,
                        voteCollectionPoint: VoteCollectionPoint,
                        batch: Int,
                        paper: Int,
                        atlPreferences: AtlPreferences,
                        btlPreferences: BtlPreferences
                       ) {

}

object Ballot {
  type AtlPreferences = Map[Group, Preference]
  type BtlPreferences = Map[CandidatePosition, Preference]
}
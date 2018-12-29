package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.VoteCollectionPoint.PollingPlace
import au.id.tmm.utilities.geo.australia.State

package object federal {

  type Division = Electorate[FederalElection, State]
  type FederalVcp = VoteCollectionPoint[FederalElection, FederalVoteCollectionPointJurisdiction]
  type FederalPollingPlace = PollingPlace[FederalElection, FederalVoteCollectionPointJurisdiction]

}

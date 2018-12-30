package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.VoteCollectionPoint.PollingPlace
import au.id.tmm.utilities.geo.australia.State

package object federal {

  type Division = Electorate[FederalElection, State]
  def Division(
                election: FederalElection,
                jurisdiction: State,
                name: String,
                id: Electorate.Id,
              ): Division = Electorate(election, jurisdiction, name, id)

  type FederalVcp = VoteCollectionPoint[FederalElection, FederalVcpJurisdiction]

  type FederalPollingPlace = PollingPlace[FederalElection, FederalVcpJurisdiction]
  def FederalPollingPlace(
                           election: FederalElection,
                           jurisdiction: FederalVcpJurisdiction,
                           id: PollingPlace.Id,
                           pollingPlaceType: PollingPlace.PollingPlaceType,
                           name: String,
                           location: PollingPlace.Location,
                         ): FederalPollingPlace =
    PollingPlace(
      election,
      jurisdiction,
      id,
      pollingPlaceType,
      name,
      location,
    )

}

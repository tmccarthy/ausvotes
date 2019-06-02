package au.id.tmm.ausvotes.model

import au.id.tmm.utilities.geo.australia.State

package object federal {

  type Division = Electorate[FederalElection, State]
  def Division(
                election: FederalElection,
                jurisdiction: State,
                name: String,
                id: Electorate.Id,
              ): Division = Electorate(election, jurisdiction, name, id)

  type FederalVcp = FederalVoteCollectionPoint

}

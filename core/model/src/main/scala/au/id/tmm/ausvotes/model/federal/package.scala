package au.id.tmm.ausvotes.model

import au.id.tmm.utilities.geo.australia.State

package object federal {

  type Division = Electorate[FederalElection, State]
  def Division(
                election: FederalElection,
                jurisdiction: State,
                name: String,
              ): Division = Electorate(election, jurisdiction, name)

  type FederalVcp = FederalVoteCollectionPoint

}

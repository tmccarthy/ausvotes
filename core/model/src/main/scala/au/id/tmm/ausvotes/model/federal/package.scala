package au.id.tmm.ausvotes.model

package object federal {

  type Division = Electorate[FederalElection]
  type FederalVcp = VoteCollectionPoint[FederalElection, FederalVoteCollectionPointJurisdiction]

}

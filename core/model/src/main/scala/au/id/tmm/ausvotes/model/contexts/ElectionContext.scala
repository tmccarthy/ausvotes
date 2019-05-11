package au.id.tmm.ausvotes.model.contexts

import au.id.tmm.ausvotes.model

trait ElectionContext[E, ElectorateJurisdiction, VcpJurisdiction] {

  type Electorate = model.Electorate[E, ElectorateJurisdiction]

  type Vcp = model.VoteCollectionPoint[E, VcpJurisdiction]
  type PollingPlace = model.VoteCollectionPoint.PollingPlace[E, VcpJurisdiction]

}

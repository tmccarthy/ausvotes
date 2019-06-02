package au.id.tmm.ausvotes.model.contexts

import au.id.tmm.ausvotes.model

trait ElectionContext[E, ElectorateJurisdiction, VcpJurisdiction] {

  type Electorate = model.Electorate[E, ElectorateJurisdiction]
  def Electorate(
                  election: E,
                  jurisdiction: ElectorateJurisdiction,
                  name: String,
                  id: model.Electorate.Id,
                ): Electorate = model.Electorate(election, jurisdiction, name, id)

  type Vcp = model.VoteCollectionPoint[E, VcpJurisdiction]
  type PollingPlace = model.VoteCollectionPoint.PollingPlace[E, VcpJurisdiction]

}

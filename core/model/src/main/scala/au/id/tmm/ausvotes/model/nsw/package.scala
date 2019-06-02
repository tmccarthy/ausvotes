package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model
import au.id.tmm.ausvotes.model.contexts.ElectionContext

package object nsw extends ElectionContext[NswElection, Unit, nsw.VcpJurisdiction] {
  type District = super.Electorate
  def District(election: NswElection, name: String, id: model.Electorate.Id): District = Electorate(election, (), name, id)
}

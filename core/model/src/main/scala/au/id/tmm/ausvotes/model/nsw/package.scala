package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.contexts.ElectionContext

package object nsw extends ElectionContext[NswElection, Unit, nsw.VcpJurisdiction] {
  type District = super.Electorate
}

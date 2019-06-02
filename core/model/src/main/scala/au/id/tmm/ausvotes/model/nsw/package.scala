package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.contexts.ElectionContext

package object nsw extends ElectionContext[NswElection, Unit] {
  type District = super.Electorate
  def District(election: NswElection, name: String): District = Electorate(election, (), name)
}

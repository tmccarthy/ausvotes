package au.id.tmm.senatedb.model.parsing

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

final case class Division(election: SenateElection,
                          state: State,
                          name: String,
                          aecId: Int) extends Ordered[Division] {
  override def compare(that: Division): Int =
    (this.election, this.state, this.name) compare (that.election, that.state, that.name)
}
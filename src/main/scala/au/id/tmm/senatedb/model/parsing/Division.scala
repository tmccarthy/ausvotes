package au.id.tmm.senatedb.model.parsing

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

final case class Division(election: SenateElection, state: State, name: String) extends Ordered[Division] {
  override def compare(that: Division): Int = Division.ordering.compare(this, that)
}

object Division {
  private val ordering: Ordering[Division] = Ordering.by(d => (d.state.abbreviation, d.name))
}
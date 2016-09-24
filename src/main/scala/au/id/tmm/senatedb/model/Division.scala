package au.id.tmm.senatedb.model

final case class Division(name: String, state: State) extends Ordered[Division] {
  override def compare(that: Division): Int = Division.ordering.compare(this, that)
}

object Division {
  private val ordering: Ordering[Division] = Ordering.by(d => (d.state, d.name))
}
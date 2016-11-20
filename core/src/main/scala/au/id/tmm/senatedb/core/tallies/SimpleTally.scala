package au.id.tmm.senatedb.core.tallies

final case class SimpleTally(count: Double) extends TallyLike {
  override type SelfType = SimpleTally

  override def +(that: SimpleTally): SimpleTally = SimpleTally(this.count + that.count)
}

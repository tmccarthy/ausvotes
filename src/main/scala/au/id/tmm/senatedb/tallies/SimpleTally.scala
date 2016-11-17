package au.id.tmm.senatedb.tallies

final case class SimpleTally(count: Double) extends TallyLike {
  override type SelfType = SimpleTally

  override def +(that: SimpleTally): SimpleTally = SimpleTally(this.count + that.count)
}

package au.id.tmm.senatedb.tallies

final case class SimpleTally(count: Double) extends TallyLike {
  override type SelfType = SimpleTally

  override def +(that: SimpleTally): SimpleTally = SimpleTally(this.count + that.count)

  override def /(that: SimpleTally): SimpleTally = SimpleTally(this.count / that.count)

  override def /(k: Double): SimpleTally = {
    if (k == 0) {
      throw new ArithmeticException()
    }

    SimpleTally(this.count / k)
  }
}

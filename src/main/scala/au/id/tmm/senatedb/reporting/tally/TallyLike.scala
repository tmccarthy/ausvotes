package au.id.tmm.senatedb.reporting.tally

trait TallyLike {
  type SelfType <: TallyLike

  def +(that: SelfType): SelfType

  def /(that: SelfType): SelfType

  def /(k: Double): SelfType
}
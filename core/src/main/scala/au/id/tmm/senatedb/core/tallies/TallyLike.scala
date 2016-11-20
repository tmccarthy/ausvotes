package au.id.tmm.senatedb.core.tallies

trait TallyLike {
  type SelfType <: TallyLike

  def +(that: SelfType): SelfType
}
package au.id.tmm.senatedb.tallies

trait TallyLike {
  type SelfType <: TallyLike

  def +(that: SelfType): SelfType
}
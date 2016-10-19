package au.id.tmm.senatedb.reporting

trait Report[A <: Report[A]] { this: A =>

  def accumulate(other: A): A

}

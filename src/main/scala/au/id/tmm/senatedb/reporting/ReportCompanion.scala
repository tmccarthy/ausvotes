package au.id.tmm.senatedb.reporting

trait ReportCompanion[A <: Report[A]] {
  def empty: A
}

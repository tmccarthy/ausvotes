package au.id.tmm.senatedb.reporting

trait ReportCompanion {
  type T_REPORT <: Report

  def empty: T_REPORT
}

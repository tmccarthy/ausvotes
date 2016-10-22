package au.id.tmm.senatedb.reporting

trait Report {

  type SELF_TYPE <: Report

  def accumulate(other: SELF_TYPE): SELF_TYPE

}

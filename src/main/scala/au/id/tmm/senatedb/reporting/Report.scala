package au.id.tmm.senatedb.reporting

trait Report {

  type SELF_TYPE <: Report

  def accumulate(that: SELF_TYPE): SELF_TYPE

  def +(that: SELF_TYPE): SELF_TYPE = accumulate(that)
}

package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.reporting.totalformal.TotalFormalBallotsReport

final case class ReportHolder(totalFormal: TotalFormalBallotsReport) {
  def accumulate(other: ReportHolder): ReportHolder = ReportHolder(
    this.totalFormal accumulate other.totalFormal
  )
}

object ReportHolder {
  val empty = ReportHolder(TotalFormalBallotsReport.empty)
}

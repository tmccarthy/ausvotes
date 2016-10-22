package au.id.tmm.senatedb.reporting

final case class ReportHolder(totalFormal: TallyReport) {
  def accumulate(other: ReportHolder): ReportHolder = ReportHolder(
    this.totalFormal accumulate other.totalFormal
  )
}

object ReportHolder {
  val empty = ReportHolder(TallyReport.empty)
}

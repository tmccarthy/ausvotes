package au.id.tmm.senatedb.reporting

final case class ReportHolder(totalFormal: TallyReport,
                              oneAtl: TallyReport,
                              donkeyVotes: TallyReport,
                              ballotsUsingTicks: TallyReport,
                              ballotsUsingCrosses: TallyReport,
                              usedHtvReport: UsedHtvReport
                             ) {
  def accumulate(that: ReportHolder): ReportHolder = ReportHolder(
    this.totalFormal accumulate that.totalFormal,
    this.oneAtl accumulate that.oneAtl,
    this.donkeyVotes accumulate that.donkeyVotes,
    this.ballotsUsingTicks accumulate that.ballotsUsingTicks,
    this.ballotsUsingCrosses accumulate that.ballotsUsingCrosses,
    this.usedHtvReport + that.usedHtvReport
  )
}

object ReportHolder {
  val empty = ReportHolder(
    totalFormal = TallyReport.empty,
    oneAtl = TallyReport.empty,
    donkeyVotes = TallyReport.empty,
    ballotsUsingTicks = TallyReport.empty,
    ballotsUsingCrosses = TallyReport.empty,
    usedHtvReport = UsedHtvReport.empty
  )
}

package au.id.tmm.senatedb.reporting.totalformal

import au.id.tmm.senatedb.reporting.{Report, ReportCompanion}

final case class TotalFormalBallotsReport(total: Long) extends Report[TotalFormalBallotsReport] {
  override def accumulate(other: TotalFormalBallotsReport): TotalFormalBallotsReport =
    TotalFormalBallotsReport(this.total + other.total)
}

object TotalFormalBallotsReport extends ReportCompanion[TotalFormalBallotsReport] {
  override val empty = TotalFormalBallotsReport(0)
}
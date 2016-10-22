package au.id.tmm.senatedb.reporting.totalformal

import au.id.tmm.senatedb.model.parsing.{Division, Party, VoteCollectionPoint}
import au.id.tmm.senatedb.reporting.ReportAccumulationUtils.{combineTallies, emptyCountMap}
import au.id.tmm.senatedb.reporting.{Report, ReportCompanion, TallyReport}
import au.id.tmm.utilities.geo.australia.State

final case class TotalFormalBallotsReport(total: Long,
                                          perState: Map[State, Long],
                                          perDivision: Map[Division, Long],
                                          perVoteCollectionPlace: Map[VoteCollectionPoint, Long],
                                          perFirstPreferencedParty: Map[Option[Party], Long])
  extends Report[TotalFormalBallotsReport]
  with TallyReport {

  override def accumulate(that: TotalFormalBallotsReport): TotalFormalBallotsReport =
    TotalFormalBallotsReport(
      this.total + that.total,
      combineTallies(this.perState, that.perState),
      combineTallies(this.perDivision, that.perDivision),
      combineTallies(this.perVoteCollectionPlace, that.perVoteCollectionPlace),
      combineTallies(this.perFirstPreferencedParty, that.perFirstPreferencedParty)
    )
}

object TotalFormalBallotsReport extends ReportCompanion[TotalFormalBallotsReport] {
  override val empty = TotalFormalBallotsReport(
    total = 0,
    perState = emptyCountMap,
    perDivision = emptyCountMap,
    perVoteCollectionPlace = emptyCountMap,
    perFirstPreferencedParty = emptyCountMap
  )
}
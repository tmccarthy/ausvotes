package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.parsing.{Division, Party, VoteCollectionPoint}
import au.id.tmm.senatedb.reporting.ReportAccumulationUtils.{combineTallies, emptyCountMap}
import au.id.tmm.utilities.geo.australia.State

final case class TallyReport(total: Long,
                             perState: Map[State, Long],
                             perDivision: Map[Division, Long],
                             perVoteCollectionPlace: Map[VoteCollectionPoint, Long],
                             perFirstPreferencedParty: Map[Option[Party], Long]
                            ) extends Report {
  override type SELF_TYPE = TallyReport

  override def accumulate(that: TallyReport): TallyReport =
    TallyReport(
      this.total + that.total,
      combineTallies(this.perState, that.perState),
      combineTallies(this.perDivision, that.perDivision),
      combineTallies(this.perVoteCollectionPlace, that.perVoteCollectionPlace),
      combineTallies(this.perFirstPreferencedParty, that.perFirstPreferencedParty)
    )
}

object TallyReport extends ReportCompanion {
  override type T_REPORT = TallyReport

  override val empty: TallyReport = TallyReport(
    total = 0,
    perState = emptyCountMap,
    perDivision = emptyCountMap,
    perVoteCollectionPlace = emptyCountMap,
    perFirstPreferencedParty = emptyCountMap
  )
}
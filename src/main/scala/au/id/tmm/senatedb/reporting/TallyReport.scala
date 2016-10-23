package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.parsing.{Division, Party, VoteCollectionPoint}
import au.id.tmm.senatedb.reporting.ReportAccumulationUtils._
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
      sumTallies(this.perState, that.perState),
      sumTallies(this.perDivision, that.perDivision),
      sumTallies(this.perVoteCollectionPlace, that.perVoteCollectionPlace),
      sumTallies(this.perFirstPreferencedParty, that.perFirstPreferencedParty)
    )

  def dividedBy(denominator: Long): FloatTallyReport = {
    FloatTallyReport(
      total / denominator.toDouble,
      divideTally(perState, denominator),
      divideTally(perDivision, denominator),
      divideTally(perVoteCollectionPlace, denominator),
      divideTally(perFirstPreferencedParty, denominator)
    )
  }

  def /(denominator: Long) = dividedBy(denominator)

  def dividedBy(that: TallyReport): FloatTallyReport = {
    FloatTallyReport(
      this.total.toDouble / that.total.toDouble,
      divideTally(this.perState, that.perState),
      divideTally(this.perDivision, that.perDivision),
      divideTally(this.perVoteCollectionPlace, that.perVoteCollectionPlace),
      divideTally(this.perFirstPreferencedParty, that.perFirstPreferencedParty)
    )
  }

  def /(that: TallyReport) = dividedBy(that)
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
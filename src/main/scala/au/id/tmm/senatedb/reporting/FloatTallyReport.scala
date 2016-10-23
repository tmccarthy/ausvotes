package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.parsing.{Division, Party, VoteCollectionPoint}
import au.id.tmm.senatedb.reporting.ReportAccumulationUtils.{combineFloatTallies, emptyFloatCountMap}
import au.id.tmm.utilities.geo.australia.State

final case class FloatTallyReport(total: Double,
                                  perState: Map[State, Double],
                                  perDivision: Map[Division, Double],
                                  perVoteCollectionPlace: Map[VoteCollectionPoint, Double],
                                  perFirstPreferencedParty: Map[Option[Party], Double]
                                 ) extends Report {
  override type SELF_TYPE = FloatTallyReport

  override def accumulate(that: FloatTallyReport): FloatTallyReport =
    FloatTallyReport(
      this.total + that.total,
      combineFloatTallies(this.perState, that.perState),
      combineFloatTallies(this.perDivision, that.perDivision),
      combineFloatTallies(this.perVoteCollectionPlace, that.perVoteCollectionPlace),
      combineFloatTallies(this.perFirstPreferencedParty, that.perFirstPreferencedParty)
    )
}

object FloatTallyReport extends ReportCompanion {
  override type T_REPORT = FloatTallyReport

  override val empty: FloatTallyReport = FloatTallyReport(
    total = 0,
    perState = emptyFloatCountMap,
    perDivision = emptyFloatCountMap,
    perVoteCollectionPlace = emptyFloatCountMap,
    perFirstPreferencedParty = emptyFloatCountMap
  )
}
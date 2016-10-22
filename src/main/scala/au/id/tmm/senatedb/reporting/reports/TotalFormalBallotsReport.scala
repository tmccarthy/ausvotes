package au.id.tmm.senatedb.reporting.reports

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.parsing.{Division, Party, VoteCollectionPoint}
import au.id.tmm.senatedb.reporting.ReportAccumulationUtils._
import au.id.tmm.senatedb.reporting._
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

object TotalFormalBallotsReportGenerator extends ReportGenerator with TallyReportGenerator {
  override type T_REPORT = TotalFormalBallotsReport

  override protected def shouldCount(ballot: BallotWithFacts): Boolean = true

  override protected def composeReport(total: Long,
                                       perState: Map[State, Long],
                                       perDivision: Map[Division, Long],
                                       perVoteCollectionPoint: Map[VoteCollectionPoint, Long],
                                       perFirstPreferencedParty: Map[Option[Party], Long]
                                      ): TotalFormalBallotsReport =
    TotalFormalBallotsReport(total, perState, perDivision, perVoteCollectionPoint, perFirstPreferencedParty)
}

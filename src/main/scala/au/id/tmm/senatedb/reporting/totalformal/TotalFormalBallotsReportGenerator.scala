package au.id.tmm.senatedb.reporting.totalformal

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.parsing.{Division, Party, VoteCollectionPoint}
import au.id.tmm.senatedb.reporting.{ReportGenerator, TallyReportGenerator}
import au.id.tmm.utilities.geo.australia.State

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

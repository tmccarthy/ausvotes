package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.parsing.Group
import au.id.tmm.senatedb.reporting.ReportAccumulationUtils.{sumTallies, sumTieredTallies}
import au.id.tmm.utilities.geo.australia.State

final case class UsedHtvReport(totalUsingHtv: Long,
                               totalBallots: Long,
                               usedHtvPerState: Map[State, Long],
                               totalBallotsPerState: Map[State, Long],
                               usedHtvPerGroupPerState: Map[State, Map[Group, Long]],
                               totalBallotsPerGroupPerState: Map[State, Map[Group, Long]]
                              ) extends Report {
  override type SELF_TYPE = UsedHtvReport

  override def accumulate(that: UsedHtvReport): UsedHtvReport = {
    UsedHtvReport(
      this.totalUsingHtv + that.totalUsingHtv,
      this.totalBallots + that.totalBallots,
      sumTallies(this.usedHtvPerState, that.usedHtvPerState),
      sumTallies(this.totalBallotsPerState, that.totalBallotsPerState),
      sumTieredTallies(this.usedHtvPerGroupPerState, that.usedHtvPerGroupPerState),
      sumTieredTallies(this.totalBallotsPerGroupPerState, that.totalBallotsPerGroupPerState)
    )
  }
}

object UsedHtvReport {
  val empty = UsedHtvReport(0, 0, Map.empty, Map.empty, Map.empty, Map.empty)
}
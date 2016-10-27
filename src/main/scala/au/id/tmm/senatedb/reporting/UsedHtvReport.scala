package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.parsing.{Group, Party}
import au.id.tmm.senatedb.reporting.ReportAccumulationUtils.{sumTallies, sumTieredTallies}
import au.id.tmm.utilities.geo.australia.State

final case class UsedHtvReport(totalUsingHtv: Long,
                               totalBallots: Long,

                               usedHtvPerState: Map[State, Long],
                               totalBallotsPerState: Map[State, Long],

                               usedHtvPerParty: Map[Party, Long],
                               totalBallotsPerParty: Map[Party, Long],

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
      sumTallies(this.usedHtvPerParty, that.usedHtvPerParty),
      sumTallies(this.totalBallotsPerParty, that.totalBallotsPerParty),
      sumTieredTallies(this.usedHtvPerGroupPerState, that.usedHtvPerGroupPerState),
      sumTieredTallies(this.totalBallotsPerGroupPerState, that.totalBallotsPerGroupPerState)
    )
  }
}

object UsedHtvReport {
  val empty = UsedHtvReport(0, 0, Map.empty, Map.empty, Map.empty, Map.empty, Map.empty, Map.empty)
}
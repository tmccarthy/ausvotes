package au.id.tmm.senatedb.reporting.reports

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.computation.FirstPreference
import au.id.tmm.senatedb.model.parsing.{Group, Party, Ungrouped}
import au.id.tmm.senatedb.reporting.ReportAccumulationUtils.increment
import au.id.tmm.senatedb.reporting.{ReportGenerator, UsedHtvReport}
import au.id.tmm.utilities.geo.australia.State

import scala.collection.mutable

object UsedHtvReportGenerator extends ReportGenerator {
  override type T_REPORT = UsedHtvReport

  override def generateFor(ballotsWithFacts: Vector[BallotWithFacts]): UsedHtvReport = {
    var totalUsingHtv: Long = 0
    var totalBallots: Long = 0

    val perState: mutable.Map[State, Long] = mutable.Map().withDefaultValue(0)
    val totalPerState: mutable.Map[State, Long] = mutable.Map().withDefaultValue(0)

    val perPartyNationally: mutable.Map[Party, Long] = mutable.Map().withDefaultValue(0)
    val totalPerPartyNationally: mutable.Map[Party, Long] = mutable.Map().withDefaultValue(0)

    val perGroupPerState: mutable.Map[State, mutable.Map[Group, Long]] = mutable.Map[State, mutable.Map[Group, Long]]()
      .withDefaultValue(mutable.Map[Group, Long]().withDefaultValue(0))
    val totalPerGroupPerState: mutable.Map[State, mutable.Map[Group, Long]] = mutable.Map[State, mutable.Map[Group, Long]]()
      .withDefaultValue(mutable.Map[Group, Long]().withDefaultValue(0))

    for (ballotWithFacts <- ballotsWithFacts) {
      val stateForBallot = ballotWithFacts.ballot.state
      val FirstPreference(firstPreferencedBallotGroup, firstPreferencedParty) = ballotWithFacts.firstPreference

      firstPreferencedBallotGroup match {
        case Ungrouped => {}
        case firstPreferencedGroup: Group => {
          totalBallots = totalBallots + 1

          increment(totalPerState, stateForBallot)

          firstPreferencedParty.foreach(p => increment(totalPerPartyNationally, p))

          val totalPerGroupInState = totalPerGroupPerState(stateForBallot)
          increment(totalPerGroupInState, firstPreferencedGroup)
          totalPerGroupPerState.put(stateForBallot, totalPerGroupInState)

          ballotWithFacts.matchingHowToVote.foreach(matchingHtvCard => {
            totalUsingHtv = totalUsingHtv + 1
            increment(perState, stateForBallot)

            firstPreferencedParty.foreach(p => increment(perPartyNationally, p))

            val perGroupInState = perGroupPerState(stateForBallot)
            increment(perGroupInState, firstPreferencedGroup)
            perGroupPerState.put(stateForBallot, perGroupInState)
          })
        }
      }
    }

    val perGroupPerStateFinal = perGroupPerState.map {
      case (state, perGroup) => state -> perGroup.toMap
    }.toMap

    val totalPerGroupPerStateFinal = totalPerGroupPerState.map {
      case (state, totalsPerGroup) => state -> totalsPerGroup.toMap
    }.toMap

    UsedHtvReport(
      totalUsingHtv = totalUsingHtv,
      totalBallots = totalBallots,
      usedHtvPerState = perState.toMap,
      totalBallotsPerState = totalPerState.toMap,
      usedHtvPerParty = perPartyNationally.toMap,
      totalBallotsPerParty = totalPerPartyNationally.toMap,
      usedHtvPerGroupPerState = perGroupPerStateFinal,
      totalBallotsPerGroupPerState = totalPerGroupPerStateFinal
    )
  }
}

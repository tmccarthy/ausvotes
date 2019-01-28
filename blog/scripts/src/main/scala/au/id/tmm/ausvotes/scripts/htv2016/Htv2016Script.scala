package au.id.tmm.ausvotes.scripts.htv2016

import java.nio.file.Paths

import au.id.tmm.ausvotes.core.engine.{ParsedDataStore, TallyEngine}
import au.id.tmm.ausvotes.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.ausvotes.core.reporting._
import au.id.tmm.ausvotes.core.reportwriting.table.TallyTable
import au.id.tmm.ausvotes.core.tallies.{BallotCounter, BallotGrouping, TallierBuilder, TallyBundle}
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.model.instances.StateInstances
import au.id.tmm.utilities.geo.australia.State
import com.google.common.base.Stopwatch

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Htv2016Script {

  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val stopwatch = Stopwatch.createStarted()

    try {
      val aecResourceStore = AecResourceStore.at(Paths.get("rawData"))
      val rawDataStore = RawDataStore(aecResourceStore)
      val parsedDataStore = ParsedDataStore(rawDataStore)

      val nationalVotesTallier = TallierBuilder.counting(BallotCounter.FormalBallots).overall()
      val nationalFirstPreferenceVotesTallier = TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)
      val stateVotesTallier = TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.State)
      val groupVotesTallier = TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedGroup)

      val nationalFirstPreferenceTallier = TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)
      val byStateTallier = TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.State)
      val byGroupTallier = TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedGroup)

      val executionFuture = TallyEngine.runFor(
        parsedDataStore,
        SenateElection.`2016`,
        State.ALL_STATES,
        talliers = Set(
          nationalFirstPreferenceVotesTallier,
          nationalVotesTallier,
          stateVotesTallier,
          groupVotesTallier,
          nationalFirstPreferenceTallier,
          byStateTallier,
          byGroupTallier,
        ),
      ).map { tallies: TallyBundle =>
        println(TableBuilders.NationalPerFirstPrefTableBuilder(nationalVotesTallier, nationalFirstPreferenceTallier, "Used HTV card").tableFrom(tallies).copy(rowOrdering = TallyTable.fractionOrdering).asMarkdown)

        println(TableBuilders.PerStateTableBuilder(nationalVotesTallier, byStateTallier, "Used HTV card").tableFrom(tallies).copy(rowOrdering = TallyTable.fractionOrdering).asMarkdown)

        State.ALL_STATES.toList.sorted(StateInstances.orderStatesByPopulation).foreach { state =>
          println(state.toNiceString)
          println(TableBuilders.PerGroupTableBuilder(stateVotesTallier, byGroupTallier, "Used HTV card", state).tableFrom(tallies).copy(rowOrdering = TallyTable.fractionOrdering).asMarkdown)
        }
      }

      Await.result(executionFuture, Duration.Inf)
    } finally {
      println(s"Completed in $stopwatch")
    }

  }

}

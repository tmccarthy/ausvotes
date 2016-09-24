package au.id.tmm.senatedb.reporting.database

import au.id.tmm.senatedb.model.{Division, State}
import au.id.tmm.senatedb.reporting.{DenominatorType, Report, ReportTable}

import scala.concurrent.Future

trait ReportsFormalBallots { this: ReportingUtilities =>

  import api._

  private val perStateQuery = dal.ballots
    .groupBy(_.state)
    .map {
      case (state, rowsPerState) => (state, rowsPerState.length)
    }

  lazy val totalFormalBallotsPerState: Future[Map[State, Int]] = {
    val byStateName = persistence.runQuery(perStateQuery)

    fillInStates(byStateName)
  }

  lazy val statesInOrderOfMostBallots: Future[Vector[State]] = {
    totalFormalBallotsPerState.map(
      _.toStream
        .sortBy {
          case (state, tally) => tally
        }
        .reverse
        .map {
          case (state, _) => state
        }
        .toVector
    )
  }

  private val formalBallotsPerDivisionQuery = dal.ballots
    .groupBy(row => row.electorate)
    .map {
      case (electorate, rowsPerDivision) => (electorate, rowsPerDivision.map(_.ballotId).size)
    }

  lazy val totalFormalBallotsPerDivision: Future[Map[Division, Int]] = {
    val byDivisionName = persistence.runQuery(formalBallotsPerDivisionQuery)

    fillInDivisions(byDivisionName)
  }

  lazy val totalFormalBallots: Future[Int] = totalFormalBallotsPerState.map(_.values.sum)

  def constructNumFormalBallotsReport: Future[Report] = {
    for {
      perStateTable <- constructPerStateTable
      perElectorateTable <- constructPerElectorateTable
    } yield Report("Number of formal ballots", perStateTable, perElectorateTable)
  }

  private def constructPerStateTable: Future[ReportTable] = {
    val sql = sqlOf(perStateQuery)

    val tableFuture = perStateTableFrom(
      talliesColTitle = "Formal ballots",
      talliesPerStateFuture = totalFormalBallotsPerState,
      denominatorType = DenominatorType.ByTotal
    )

    tableFuture.map(_.copy(description = sql))
  }

  private def constructPerElectorateTable: Future[ReportTable] = {
    val sql = sqlOf(formalBallotsPerDivisionQuery)

    val tableFuture = perDivisionTableFrom(
      talliesColTitle = "Formal ballots",
      talliesPerDivisionFuture = totalFormalBallotsPerDivision,
      denominatorType = DenominatorType.ByTotal
    )

    tableFuture.map(_.copy(description = sql))
  }
}
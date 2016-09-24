package au.id.tmm.senatedb.reporting.database

import au.id.tmm.senatedb.data.database.Persistence
import au.id.tmm.senatedb.model.{Division, State}
import au.id.tmm.senatedb.reporting._

import scala.concurrent.{ExecutionContext, Future}

trait ReportingUtilities { this: Reporter with ReportsFormalBallots =>

  implicit def ec: ExecutionContext

  def persistence: Persistence

  val dal = persistence.dal

  val api = dal.driver.api

  import api._

  protected def sqlOf[E, U, C[_]](query: Query[E, U, C]) = query.result.statements.mkString("\n")

  protected val allDivisionsQuery = dal.ballots
    .map(ballot => (ballot.state, ballot.electorate))
    .distinctOn {
      case (_, electorate) => electorate
    }

  protected lazy val allDivisions: Future[Set[Division]] = persistence.runQuery(allDivisionsQuery).map {
    rows => {
      rows.toStream
        .map {
          case (stateName, divisionName) => {
            val state = State.fromShortName(stateName).get
            Division(divisionName, state)
          }
        }
        .toSet
    }
  }

  protected lazy val divisionsInAlphaOrderByState: Future[Vector[Division]] = allDivisions.map {
    _.toVector.sorted
  }

  protected lazy val divisionLookupByName: Future[Map[String, Division]] = allDivisions.map {
    _.map(division => division.name -> division)
      .toMap
  }

  protected lazy val divisionsPerState: Future[Map[State, Set[Division]]] = allDivisions.map {
    _.groupBy(_.state)
  }

  protected lazy val statePerDivision: Future[Map[Division, State]] = allDivisions.map {
    _.map(division => division -> division.state)
      .toMap
  }

  protected def fillInStates(byStateName: Future[Seq[(String, Int)]]): Future[Map[State, Int]] = {
    byStateName
      .map { rows =>
        rows
          .map {
            case (stateName, tally) => State.fromShortName(stateName).get -> tally
          }
          .toMap
    }
  }

  protected def fillInDivisions(byDivisionNameFuture: Future[Seq[(String, Int)]]): Future[Map[Division, Int]] = {
    for {
      byDivisionName <- byDivisionNameFuture
      lookup <- divisionLookupByName
    } yield {
      byDivisionName
        .map {
          case (divisionName, tally) => lookup(divisionName) -> tally
        }
        .toMap
    }
  }

  protected def perStateTableFrom(talliesColTitle: String,
                                  fractionColTitle: String = "%",
                                  totalRowTitle: String = "Total",
                                  talliesPerStateFuture: Future[State => Int],
                                  denominatorType: DenominatorType
                                 ): Future[ReportTable] = {
    for {
      statesInOrder <- statesInOrderOfMostBallots

      talliesPerState <- talliesPerStateFuture
      totalFormalBallots <- totalFormalBallots
      totalFormalBallotsPerState <- totalFormalBallotsPerState
    } yield {
      ReportTableConstruction.constructPerStateTable(
        statesInOrder,
        talliesColTitle,
        fractionColTitle,
        totalRowTitle,
        talliesPerState,
        denominatorType,
        totalFormalBallots,
        totalFormalBallotsPerState)
    }
  }

  protected def perDivisionTableFrom(talliesColTitle: String,
                                     fractionColTitle: String = "%",
                                     totalRowTitle: String = "Total",
                                     talliesPerDivisionFuture: Future[Division => Int],
                                     denominatorType: DenominatorType
                                    ): Future[ReportTable] = {
    for {
      divisionsOrder <- divisionsInAlphaOrderByState

      talliesPerDivision <- talliesPerDivisionFuture
      totalFormalBallots <- totalFormalBallots
      totalFormalBallotsPerState <- totalFormalBallotsPerState
      totalFormalBallotsPerDivision <- totalFormalBallotsPerDivision
    } yield {
      ReportTableConstruction.constructPerDivisionTable(
        divisionsOrder,
        talliesColTitle,
        fractionColTitle,
        totalRowTitle,
        talliesPerDivision,
        denominatorType,
        totalFormalBallots,
        totalFormalBallotsPerState,
        totalFormalBallotsPerDivision)
    }
  }
}
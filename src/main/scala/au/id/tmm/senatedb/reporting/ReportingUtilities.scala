package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.data.database.Persistence
import au.id.tmm.senatedb.model.{Division, State}
import au.id.tmm.senatedb.reporting.ReportTable._

import scala.collection.Map
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
      denominatorsPerState <- denominatorsForState(denominatorType)
      totalRowDenominator <- totalFormalBallots
    } yield {
      val rows = statesInOrder
        .map { state =>
          val tally = talliesPerState(state)
          val denominator = denominatorsPerState(state)

          val fraction = fractionOf(tally, denominator)

          StateRow(state, tally, fraction)
        }

      val totalsRow = {
        val totalRowTally = rows.map(_.tally).sum
        val totalRowFraction = totalRowTally.toDouble / totalRowDenominator.toDouble

        TotalRow(totalRowTitle, totalRowTally, Some(totalRowFraction))
      }

      val columns = Vector(StateNameColumn, TallyColumn(talliesColTitle))

      val table = ReportTable(
        rows :+ totalsRow,
        columns
      )

      ReportTable.addFractionColumnIfDefinedForAllRows(table, fractionColTitle)
    }
  }

  private def fractionOf(numerator: Int, denominator: Option[Int]) = denominator
    .map(numerator.toDouble / _.toDouble)

  private def denominatorsForDivision(denominatorType: DenominatorType): Future[Division => Option[Int]] = {
    denominatorType match {
      case DenominatorType.ByTotal => totalFormalBallots.map(total => _ => Some(total))
      case DenominatorType.ByStateTotal => totalFormalBallotsPerState.map(theMap => division => theMap.get(division.state))
      case DenominatorType.ByDivisionTotal => totalFormalBallotsPerDivision.map(theMap => theMap.get)
      case DenominatorType.None => Future(_ => None)
    }
  }

  private def denominatorsForState(denominatorType: DenominatorType): Future[State => Option[Int]] = {
    denominatorType match {
      case DenominatorType.ByTotal => totalFormalBallots.map(total => _ => Some(total))
      case DenominatorType.ByStateTotal => totalFormalBallotsPerState.map(theMap => state => theMap.get(state))
      case DenominatorType.ByDivisionTotal => throw new IllegalArgumentException("Cant divide a state tally by the division total")
      case DenominatorType.None => Future(_ => None)
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
      denominatorsPerDivision <- denominatorsForDivision(denominatorType)
      totalRowDenominator <- totalFormalBallots
    } yield {
      val rows = divisionsOrder
        .map { division =>
          val tally = talliesPerDivision(division)
          val denominator = denominatorsPerDivision(division)

          val fraction = fractionOf(tally, denominator)

          DivisionRow(division, tally, fraction)
        }

      val totalsRow = {
        val totalRowTally = rows.map(_.tally).sum
        val totalRowFraction = totalRowTally.toDouble / totalRowDenominator.toDouble

        TotalRow(totalRowTitle, totalRowTally, Some(totalRowFraction))
      }

      val columns = Vector(DivisionNameColumn, StateNameColumn, TallyColumn(talliesColTitle))

      val table = ReportTable(
        rows :+ totalsRow,
        columns
      )

      ReportTable.addFractionColumnIfDefinedForAllRows(table, fractionColTitle)
    }
  }
}
package au.id.tmm.senatedb.data.database

import slick.driver.JdbcProfile
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext

private[data] final class DAL(val driver: JdbcProfile) extends DriverComponent
  with GroupsComponent
  with CandidatesComponent
  with BallotComponent
  with AtlPreferencesComponent
  with BtlPreferencesComponent {

  private val allTables = Vector(groups, candidates, ballots, atlPreferences, btlPreferences)

  import driver.api._

  def listTables = MTable.getTables

  def listTableNames(implicit executionContext: ExecutionContext) = listTables.map(_.map(_.name.name))

  def isInitialised(implicit executionContext: ExecutionContext) = listTables.map(_.nonEmpty)

  def initialise() = {
    createAllTables()
  }

  def createAllTables() = allTables
    .map(_.schema)
    .reduce(_ ++ _)
    .create

  def destroy() = {
    dropAllTables()
  }

  def dropAllTables() = allTables
    .map(_.schema)
    .reduce(_ ++ _)
    .drop
}

private[data] object DAL {
  val UNKNOWN: Int = -1
}
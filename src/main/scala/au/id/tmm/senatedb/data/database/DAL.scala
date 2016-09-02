package au.id.tmm.senatedb.data.database

import au.id.tmm.senatedb.data.database.model._
import slick.driver.JdbcProfile
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext

private[data] final class DAL(val driver: JdbcProfile) extends DriverComponent
  with GroupsComponent
  with CandidatesComponent
  with BallotComponent
  with BallotFactsComponent
  with AtlPreferencesComponent
  with BtlPreferencesComponent {

  private val allTables = Vector(groups, candidates, ballots, ballotFacts, atlPreferences, btlPreferences)

  private lazy val allTableNames = allTables.map(_.baseTableRow.tableName)

  import driver.api._

  def listTables = MTable.getTables

  def listTableNames(implicit executionContext: ExecutionContext) = listTables.map(_.map(_.name.name))

  def isInitialised(implicit executionContext: ExecutionContext) =
    listTableNames.map(tableNames => allTableNames.forall(tableNames.contains))

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

  // So we can use .transactionally
  implicit class ExtensionMethods[E <: Effect, R, S <: NoStream](a: DBIOAction[R, S, E])
    extends driver.JdbcActionExtensionMethods[E, R, S](a)
}

private[data] object DAL {
  val UNKNOWN: Int = -1
}
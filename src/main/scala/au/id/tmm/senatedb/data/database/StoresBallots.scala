package au.id.tmm.senatedb.data.database

import java.sql.PreparedStatement

import au.id.tmm.senatedb.data.BallotWithPreferences
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.collection.CloseableIterator
import resource.managed

import scala.concurrent.Future

private[database] trait StoresBallots { this: Persistence =>

  import dal.driver.api._

  def storeBallotData(ballots: CloseableIterator[BallotWithPreferences]): Future[Unit] = Future {

    for {
      session <- managed(database.createSession())
      ballotInsertStatement <- managed(session.prepareInsertStatement(dal.ballots.insertStatement))
      atlPreferencesInsertStatement <- managed(session.prepareInsertStatement(dal.atlPreferences.insertStatement))
      btlPreferencesInsertStatement <- managed(session.prepareInsertStatement(dal.btlPreferences.insertStatement))
    } {
      for (ballots <- ballots.grouped(StoresBallots.INSERT_CHUNK_SIZE)) {
        session.prepareStatement("BEGIN;").execute()
        for (ballotWithPreferences <- ballots) {
          fillStatement[BallotRow](ballotInsertStatement, row => BallotRow.unapply(row).get.productIterator, ballotWithPreferences.ballot)
          ballotInsertStatement.addBatch()

          for (atlPreference <- ballotWithPreferences.atlPreferences) {
            fillStatement[AtlPreferencesRow](atlPreferencesInsertStatement, row => AtlPreferencesRow.unapply(row).get.productIterator, atlPreference)
            atlPreferencesInsertStatement.addBatch()
          }

          for (btlPreference <- ballotWithPreferences.btlPreferences) {
            fillStatement[BtlPreferencesRow](btlPreferencesInsertStatement, row => BtlPreferencesRow.unapply(row).get.productIterator, btlPreference)
            btlPreferencesInsertStatement.addBatch()
          }
        }
        ballotInsertStatement.executeBatch()
        atlPreferencesInsertStatement.executeBatch()
        btlPreferencesInsertStatement.executeBatch()
        session.prepareStatement("COMMIT;").execute()
      }
    }
  }

  private def fillStatement[A](statement: PreparedStatement, toArray: (A => Iterator[Any]), row: A): Unit = {
    val values = toArray(row)

    values
      .zipWithIndex
      .foreach {
        case (value, index) => setOnStatement(statement, index + 1, value)
      }
  }

  @scala.annotation.tailrec
  private def setOnStatement(statement: PreparedStatement, index: Int, value: Any): Unit = {
    value match {
      case None => statement.setObject(index, null)
      case Some(internal) => setOnStatement(statement, index, internal)
      case _ => statement.setObject(index, value)
    }
  }

  def hasBallotsFor(election: SenateElection, state: State): Future[Boolean] = {
    val query = dal.ballots
      .filter(_.electionId === election.aecID)
      .filter(_.state === state.shortName)

    execute(query.exists.result)
  }

  def deleteBallotsAndPreferencesFor(election: SenateElection, state: State): Future[Unit] = {
    val ballotIdsToDelete = dal.ballots
      .filter(_.electionId === election.aecID)
      .filter(_.state === state.shortName)
      .map(_.ballotId)

    val atlPreferencesDeleteStatement = dal.atlPreferences.filter(_.ballotId in ballotIdsToDelete).delete

    val btlPreferencesDeleteStatement = dal.btlPreferences.filter(_.ballotId in ballotIdsToDelete).delete

    val ballotsDeleteStatement = dal.ballots
      .filter(_.electionId === election.aecID)
      .filter(_.state === state.shortName)
      .delete

    val deleteStatement = atlPreferencesDeleteStatement andThen btlPreferencesDeleteStatement andThen ballotsDeleteStatement

    execute(deleteStatement).map(_ => Unit)
  }
}

object StoresBallots {
  private val INSERT_CHUNK_SIZE = 100000
}
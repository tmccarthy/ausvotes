package au.id.tmm.senatedb.data.database

import au.id.tmm.senatedb.data.BallotWithPreferences
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.collection.CloseableIterator

import scala.concurrent.Future

private[database] trait StoresBallots { this: Persistence =>
  def storeBallotData(ballots: CloseableIterator[BallotWithPreferences]): Future[Unit] = {
    val chunkInsertFutures = ballots
      .grouped(StoresBallots.INSERT_CHUNK_SIZE)
      .map(toInsert => {
        val ballotRows = toInsert.map(_.ballot)
        val atlPreferenceRows = toInsert.flatMap(_.atlPreferences)
        val btlPreferenceRows = toInsert.flatMap(_.btlPreferences)

        // TODO do this in a transaction?
        execute(dal.insertBallots(ballotRows)
          andThen dal.insertAtlPreferences(atlPreferenceRows)
          andThen dal.insertBtlPreferences(btlPreferenceRows))
      })

    Future.sequence(chunkInsertFutures).map(_ => Unit)
  }

  import dal.driver.api._

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
  private val INSERT_CHUNK_SIZE = 100
}
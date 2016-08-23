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

  def hasBallotsFor(election: SenateElection, state: State): Future[Boolean] = {
    import dal.driver.api._

    val query = dal.ballots
      .filter(_.electionId === election.aecID)
      .filter(_.state === state.shortName)

    execute(query.exists.result)
  }
}

object StoresBallots {
  private val INSERT_CHUNK_SIZE = 100
}
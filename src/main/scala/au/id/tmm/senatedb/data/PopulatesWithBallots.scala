package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.model.{SenateElection, State}

import scala.concurrent.Future

trait PopulatesWithBallots { this: PersistencePopulator =>
  def loadBallots(election: SenateElection,
                  state: State,
                  allowDownloading: Boolean = true,
                  forceReload: Boolean = false): Future[Unit] = {
    def deleteIfReloading() = if(forceReload) {
      persistence.deleteBallotsAndPreferencesFor(election, state)
    } else {
      Future.successful(Unit)
    }

    def loadIfNeeded(alreadyLoaded: Boolean) = if(alreadyLoaded) {
      Future.successful(Unit)
    } else {
      doLoadFor(election, state, allowDownloading, forceReload)
    }

    for {
      _ <- deleteIfReloading()
      alreadyLoaded <- persistence.hasBallotsFor(election, state)
      _ <- loadIfNeeded(alreadyLoaded)
    } yield ()
  }

  private def doLoadFor(election: SenateElection,
                        state: State,
                        allowDownloading: Boolean,
                        forceReload: Boolean): Future[Unit] = {
    for {
      _ <- loadGroupsAndCandidates(election, allowDownloading, forceReload)
      candidates <- persistence.retrieveCandidatesFor(election)
      ballotsWithPreferences <- Future(rawDataStore.retrieveBallots(election, state, candidates.toSet, allowDownloading).get)
      _ <- persistence.storeBallotData(ballotsWithPreferences)
    } yield ()
  }
}

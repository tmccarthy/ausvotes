package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.model.{SenateElection, State}

import scala.concurrent.Future

trait PopulatesWithCountData { this: PersistencePopulator =>

  def loadCountData(election: SenateElection,
                    state: State,
                    allowDownloading: Boolean = true,
                    forceReload: Boolean = false
                   ): Future[Unit] = loadCountDataForStates(election, Set(state), allowDownloading, forceReload)

  def loadCountDataForStates(election: SenateElection,
                             states: Set[State],
                             allowDownloading: Boolean = true,
                             forceReload: Boolean = false
                            ): Future[Unit] = {
    for {
      _ <- persistence.initialiseIfNeeded()
      _ <- loadGroupsAndCandidates(election, allowDownloading, forceReload)
      _ <- loadOnlyCountDataForStates(election, states, allowDownloading, forceReload)
    } yield ()
  }

  private def loadOnlyCountDataForStates(election: SenateElection,
                                         states: Set[State],
                                         allowDownloading: Boolean,
                                         forceReload: Boolean): Future[Unit] = {
    val loadingFuturesPerState = states
      .toStream
      .map(state => loadOnlyCountDataForState(election, state, allowDownloading, forceReload))

    sequenceWritingFutures(loadingFuturesPerState).map(_ => Unit)
  }

  private def loadOnlyCountDataForState(election: SenateElection,
                                        state: State,
                                        allowDownloading: Boolean,
                                        forceReload: Boolean): Future[Unit] = {
    def deleteIfReloading() = if(forceReload) {
      persistence.deleteCountDataFor(election, state)
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
      alreadyLoaded <- persistence.hasCountDataFor(election, state)
      _ <- loadIfNeeded(alreadyLoaded)
    } yield ()
  }

  private def doLoadFor(election: SenateElection, state: State, allowDownloading: Boolean, forceReload: Boolean): Future[Unit] = {
    for {
      candidates <- persistence.retrieveCandidatesFor(election)
      countData <- Future(rawDataStore.retrieveCountData(election, state, candidates.toSet, allowDownloading).get)
      _ <- persistence.storeCountData(countData)
    } yield ()
  }
}

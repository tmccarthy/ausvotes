package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.model.{SenateElection, State}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait PopulatesWithBallots { this: PersistencePopulator =>

  def loadBallots(election: SenateElection,
                  state: State,
                  allowDownloading: Boolean = true,
                  forceReload: Boolean = false): Future[Unit] = {
    loadBallotsForStates(election, Set(state), allowDownloading, forceReload)
  }


  def loadBallotsForStates(election: SenateElection,
                           states: Set[State],
                           allowDownloading: Boolean = true,
                           forceReload: Boolean = false): Future[Unit] = {
    for {
      _ <- persistence.initialiseIfNeeded()
      _ <- loadGroupsAndCandidates(election, allowDownloading, forceReload)
      _ <- loadOnlyBallotsForStates(election, states, allowDownloading, forceReload)
    } yield ()
  }

  private def loadOnlyBallotsForStates(election: SenateElection,
                                       states: Set[State],
                                       allowDownloading: Boolean,
                                       forceReload: Boolean): Future[Unit] = {
    val loadingFuturesPerState = states
      .toStream
      .map(state => loadOnlyBallotsForState(election, state, allowDownloading, forceReload))

    Future {
      loadingFuturesPerState.foreach(Await.result(_, Duration.Inf))
    }
  }

  private def loadOnlyBallotsForState(election: SenateElection,
                                      state: State,
                                      allowDownloading: Boolean,
                                      forceReload: Boolean): Future[Unit] = {
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
      candidates <- persistence.retrieveCandidatesFor(election)
      ballotsWithPreferences <- Future(rawDataStore.retrieveBallots(election, state, candidates.toSet, allowDownloading).get)
      _ <- persistence.storeBallotData(ballotsWithPreferences)
    } yield ()
  }
}

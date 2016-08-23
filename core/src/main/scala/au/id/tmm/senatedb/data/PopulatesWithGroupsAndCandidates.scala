package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.model.SenateElection

import scala.concurrent.Future

trait PopulatesWithGroupsAndCandidates { this: PersistencePopulator =>
  def loadGroupsAndCandidates(election: SenateElection,
                              allowDownloading: Boolean = true,
                              forceReload: Boolean = false): Future[Unit] = {
    def deleteIfNeeded(): Future[Unit] = if (forceReload) deleteFor(election) else Future.successful(Unit)

    for {
      _ <- deleteIfNeeded()
      alreadyLoaded <- checkIfAlreadyLoadedFor(election)
      _ <- if (alreadyLoaded) Future.successful(Unit) else doLoadFor(election, allowDownloading)
    } yield ()
  }

  private def deleteFor(election: SenateElection): Future[Unit] = {
    for {
      _ <- persistence.deleteGroupsFor(election)
      _ <- persistence.deleteCandidatesFor(election)
    } yield ()
  }

  private def checkIfAlreadyLoadedFor(election: SenateElection): Future[Boolean] = {
    val alreadyHasGroupsFuture = persistence.hasGroupsFor(election)
    val alreadyHasCandidatesFuture = persistence.hasCandidatesFor(election)

    for {
      alreadyHasGroups <- alreadyHasGroupsFuture
      alreadyHasCandidates <- alreadyHasCandidatesFuture
    } yield alreadyHasGroups || alreadyHasCandidates
  }

  private def doLoadFor(election: SenateElection, allowDownloading: Boolean): Future[Unit] = {
    for {
      (groups, candidates) <- Future(rawDataStore.retrieveGroupsAndCandidates(election, allowDownloading).get)
      _ <- persistence.storeGroups(groups)
      _ <- persistence.storeCandidates(candidates)
    } yield ()
  }
}

package au.id.tmm.senatedb.data.database

import scala.concurrent.Future

private[database] trait PersistenceLifecycle { this: Persistence =>
  def initialiseIfNeeded(): Future[Unit] = isInitialised
    .flatMap(alreadyInitialised => if (!alreadyInitialised) initialise() else Future.successful(Unit))

  private def isInitialised: Future[Boolean] = execute(dal.isInitialised)

  def initialise(): Future[Unit] = execute(dal.initialise())

  def destroyIfNeeded(): Future[Unit] = isInitialised
    .flatMap(alreadyInitialised => if (alreadyInitialised) destroy() else Future.successful(Unit))

  def destroy(): Future[Unit] = execute(dal.destroy())
}

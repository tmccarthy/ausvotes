package au.id.tmm.ausvotes.shared.recountresources.entities

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.{IO, Promise, Semaphore}

private[entities] object RecountEntityCacheUtils {
  def getPromiseFor[E, A](
                                             election: SenateElection,
                                             state: State,
                                             cacheMap: CacheMap[E, A],
                                             mutex: Semaphore,
                                           )(
                                             fetch: IO[E, A]
                                           ): IO[Nothing, Promise[E, A]] = {
    cacheMap.get((election, state)).foreach(promise => return IO.point(promise))

    for {
      promise <- Promise.make[E, A]

      fetchAndCompletePromise = fetch.attempt.flatMap {
        case Right(entity) => promise.complete(entity)
        case Left(exception) => mutex.withPermit {
          cacheMap.remove((election, state))

          promise.error(exception)
        }
      }

      _ <- fetchAndCompletePromise.fork
    } yield {
      cacheMap.update((election, state), promise)

      promise
    }
  }
}

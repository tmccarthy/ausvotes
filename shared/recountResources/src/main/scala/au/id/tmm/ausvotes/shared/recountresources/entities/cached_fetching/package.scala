package au.id.tmm.ausvotes.shared.recountresources.entities

import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import scalaz.zio.{IO, Promise, Semaphore}

import scala.collection.mutable

package object cached_fetching {

  private[cached_fetching] type CacheMap[E, A] = mutable.Map[SenateElectionForState, Promise[E, A]]

  private[cached_fetching] def getPromiseFor[E, A](
                                                    election: SenateElectionForState,
                                                    cacheMap: CacheMap[E, A],
                                                    mutex: Semaphore,
                                                  )(
                                                    fetch: IO[E, A]
                                                  ): IO[Nothing, Promise[E, A]] = {
    cacheMap.get(election).foreach(promise => return IO.succeed(promise))

    for {
      promise <- Promise.make[E, A]

      fetchAndCompletePromise = fetch.either.flatMap {
        case Right(entity) => promise.succeed(entity)
        case Left(exception) => mutex.withPermit {
          cacheMap.remove(election)

          promise.fail(exception)
        }
      }

      _ <- fetchAndCompletePromise.fork
    } yield {
      cacheMap.update(election, promise)

      promise
    }
  }
}

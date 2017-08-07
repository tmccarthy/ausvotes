package au.id.tmm.senatedb.api.integrationtest

import java.time.Instant
import java.time.temporal.ChronoUnit

import play.api.cache.SyncCacheApi

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.Duration

/**
  * An implementation of [[SyncCacheApi]] backed by a simple Map. Designed for testing.
  */
class SimpleCacheApi extends SyncCacheApi {

  private val map = new TrieMap[String, (Instant, Any)]()

  override def set(key: String, value: Any, expiration: Duration): Unit = {
    val expiryTime = Instant.now().plus(expiration.toMillis, ChronoUnit.MILLIS)

    map.update(key, (expiryTime, value))
  }

  override def remove(key: String): Unit = {
    map.remove(key)
  }

  override def getOrElseUpdate[A](key: String, expiration: Duration)(orElse: => A)(implicit evidence$1: ClassManifest[A]): A = {
    val existingValue = get(key)

    if (existingValue.isEmpty) {
      set(key, orElse, expiration)
      orElse
    } else {
      existingValue.get
    }
  }

  override def get[T](key: String)(implicit evidence$2: ClassManifest[T]): Option[T] = {
    map.get(key)
      .flatMap { case (expiryTime, value) =>

        if (expiryTime.isAfter(Instant.now())) {
          remove(key)
          None
        } else {
          Some(value)
        }
      }
      .map(_.asInstanceOf[T])
  }
}

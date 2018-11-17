package au.id.tmm.ausvotes.shared.recountresources.entities

import argonaut.{DecodeJson, Parse}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.{GroupCodec, PartyCodec}
import au.id.tmm.ausvotes.core.model.parsing.{Group, Party}
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.ausvotes.shared.recountresources.entities.GroupsCache.GroupsCacheException
import au.id.tmm.ausvotes.shared.recountresources.entities.IRecountEntityCache.getPromiseFor
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.{IO, Promise, Semaphore}

import scala.collection.mutable

final class GroupsCache(
                         private[entities] val baseBucket: S3BucketName,
                         private[entities] val mutex: Semaphore,
                       ) {

  object codecs {
    implicit val decodeParty: DecodeJson[Party] = PartyCodec.decodeParty
    implicit val decodeGroup: DecodeJson[Group] = GroupCodec.decodeGroup
  }

  private val groups: CacheMap[GroupsCacheException, Set[Group]] = mutable.Map()

}

object GroupsCache {

  def apply(baseBucket: S3BucketName): IO[Nothing, GroupsCache] =
    Semaphore(permits = 1).map(new GroupsCache(baseBucket, _))

  def groupsFor(
                 election: SenateElection,
                 state: State,
               )(implicit cache: GroupsCache): IO[Nothing, Promise[GroupsCacheException, Set[Group]]] = cache.mutex.withPermit {
    getPromiseFor(election, state, cache.groups, cache.mutex) {
      import cache.codecs._

      val objectKey = EntityLocations.locationOfGroupsObject(election, state)

      (for {
        jsonString <- ReadsS3.readAsString(cache.baseBucket, objectKey)
          .leftMap(GroupsCacheException.GroupsFetchException)
        groups <- IO.fromEither {
          val decodeResult = Parse.decodeEither[Set[Group]](jsonString)

          decodeResult.left.map(GroupsCacheException.GroupsDecodeException)
        }
      } yield groups).timedLog(
        "COMPLETE_ENTITY_CACHE_PROMISE",
        "entity_name" -> "groups",
        "election" -> election,
        "state" -> state,
      )
    }
  }

  sealed abstract class GroupsCacheException extends ExceptionCaseClass

  object GroupsCacheException {
    final case class GroupsFetchException(cause: Exception) extends GroupsCacheException with ExceptionCaseClass.WithCause
    final case class GroupsDecodeException(message: String) extends GroupsCacheException
  }

}

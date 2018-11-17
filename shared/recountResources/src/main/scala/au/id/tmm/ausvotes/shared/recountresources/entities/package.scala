package au.id.tmm.ausvotes.shared.recountresources

import argonaut.DecodeResult
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.Promise

import scala.collection.mutable

package object entities {
  private[entities] type StateAtElection = (SenateElection, State)
  private[entities] type CacheMap[E, A] = mutable.Map[StateAtElection, Promise[E, A]]

  // TODO make this shared
  implicit class DecodeResultOps[A](decodeResult: DecodeResult[A]) {
    def toMessageOrResult: Either[String, A] = decodeResult.toEither.left.map {
      case (message, cursorHistory) => message + ": " + cursorHistory.toString
    }
  }
}

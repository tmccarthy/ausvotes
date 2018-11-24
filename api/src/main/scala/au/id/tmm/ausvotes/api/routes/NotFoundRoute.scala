package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.CompleteRoutes
import au.id.tmm.ausvotes.api.errors.NotFoundException
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import unfiltered.request.Path

object NotFoundRoute {

  def apply[F[+_, +_] : Monad]: CompleteRoutes[F] = {
    case Path(path) => Monad.leftPure(NotFoundException(path))
  }

}

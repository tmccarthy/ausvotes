package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.Routes
import au.id.tmm.ausvotes.api.errors.NotFoundException
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import unfiltered.request.Path

object NotFoundRoute {

  def apply[F[+_, +_] : Monad]: Routes[F] = {
    case Path(path) => Monad.leftPure(NotFoundException(path))
  }

}

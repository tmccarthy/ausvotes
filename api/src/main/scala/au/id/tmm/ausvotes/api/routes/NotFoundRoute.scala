package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.Routes
import au.id.tmm.ausvotes.api.errors.NotFoundException
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad

object NotFoundRoute {

  def apply[F[+_, +_] : Monad]: Routes[F] = {
    case _ => Monad.leftPure(NotFoundException())
  }

}

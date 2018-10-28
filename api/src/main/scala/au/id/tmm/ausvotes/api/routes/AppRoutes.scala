package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.Routes
import au.id.tmm.ausvotes.shared.io.actions.Resources
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad

object AppRoutes {

  def apply[F[+_, +_] : Monad : Resources]: Routes[F] =
    DiagnosticRoutes[F] orElse
      NotFoundRoute[F]

}

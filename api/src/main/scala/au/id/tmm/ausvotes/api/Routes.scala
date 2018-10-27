package au.id.tmm.ausvotes.api

import au.id.tmm.ausvotes.api.diagnostics.DiagnosticRoutes
import au.id.tmm.ausvotes.shared.io.actions.Resources
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad

object Routes {

  def apply[F[+_, +_] : Monad : Resources]: Routes[F] = DiagnosticRoutes[F]

}

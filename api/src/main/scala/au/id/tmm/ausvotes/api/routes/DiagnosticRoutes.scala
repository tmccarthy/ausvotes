package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.Routes
import au.id.tmm.ausvotes.api.controllers.DiagnosticController
import au.id.tmm.ausvotes.api.utils.unfiltered.ResponseJson
import au.id.tmm.ausvotes.shared.io.actions.Resources
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import unfiltered.request.{GET, Path, Seg}

object DiagnosticRoutes {

  def apply[F[+_, +_] : Monad : Resources]: Routes[F] = {
    case GET(Path(Seg("diagnostics" :: "version" :: Nil))) => DiagnosticController.version[F].map(ResponseJson(_))
  }

}

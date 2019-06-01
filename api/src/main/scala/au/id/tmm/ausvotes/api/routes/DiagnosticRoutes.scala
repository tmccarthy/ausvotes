package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.PartialRoutes
import au.id.tmm.ausvotes.api.controllers.DiagnosticController
import au.id.tmm.ausvotes.api.utils.unfiltered.ResponseJson
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME.Ops
import au.id.tmm.bfect.effects.extra.Resources
import unfiltered.request.{GET, Path, Seg}

object DiagnosticRoutes {

  def apply[F[+_, +_] : BME : Resources]: PartialRoutes[F] = {
    case GET(Path(Seg("diagnostics" :: "version" :: Nil))) => DiagnosticController.version[F].map(ResponseJson(_))
  }

}

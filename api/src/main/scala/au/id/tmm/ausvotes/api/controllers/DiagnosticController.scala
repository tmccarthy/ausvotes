package au.id.tmm.ausvotes.api.controllers

import au.id.tmm.ausvotes.api.model.diagnostics.VersionResponse
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME.Ops
import au.id.tmm.bfect.extraeffects.Resources

object DiagnosticController {

  def version[F[+_, +_] : BME : Resources]: F[Exception, VersionResponse] = Resources[F].resourceAsString("/version.txt").flatMap {
    case Some(version) => BME.pure(VersionResponse(version))
    case None => BME.leftPure(new Exception("Version file missing"))
  }

}

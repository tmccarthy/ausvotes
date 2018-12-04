package au.id.tmm.ausvotes.api.controllers

import au.id.tmm.ausvotes.api.model.diagnostics.VersionResponse
import au.id.tmm.ausvotes.shared.io.actions.Resources
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError => BME}
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops

object DiagnosticController {

  def version[F[+_, +_] : BME : Resources]: F[Exception, VersionResponse] = Resources.asString("/version.txt").flatMap {
    case Some(version) => BME.pure(VersionResponse(version))
    case None => BME.leftPure(new Exception("Version file missing"))
  }

}

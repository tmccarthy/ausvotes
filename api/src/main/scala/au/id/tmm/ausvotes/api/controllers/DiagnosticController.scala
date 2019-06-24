package au.id.tmm.ausvotes.api.controllers

import au.id.tmm.ausvotes.api.model.diagnostics.VersionResponse
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME.Ops
import au.id.tmm.bfect.effects.extra.Resources

object DiagnosticController {

  def version[F[+_, +_] : Resources : BME]: F[Exception, VersionResponse] =
    Resources[F].resourceAsString("/version.txt")
      .attempt
      .flatMap {
        case Right(version) => BME.pure(VersionResponse(version)): F[Exception, VersionResponse]
        case Left(Resources.ResourceStreamError.ResourceNotFound) => BME.leftPure(new Exception("Version file missing")): F[Exception, VersionResponse]
        case Left(Resources.ResourceStreamError.UseError(e)) => BME.leftPure(e): F[Exception, VersionResponse]
      }

}

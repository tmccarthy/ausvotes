package au.id.tmm.ausvotes.api.controllers

import au.id.tmm.ausvotes.api.model.diagnostics.VersionResponse
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops
import au.id.tmm.bfect.effects.extra.Resources

object DiagnosticController {

  def version[F[+_, +_] : Resources]: F[Exception, VersionResponse] = Resources[F].resourceAsString("/version.txt").attempt.flatMap {
    case Right(version) => Sync[F].pure(VersionResponse(version)): F[Exception, VersionResponse]
    case Left(Resources.ResourceStreamError.ResourceNotFound) => Sync[F].leftPure(new Exception("Version file missing")): F[Exception, VersionResponse]
    case Left(Resources.ResourceStreamError.UseError(e)) => Sync[F].leftPure(e): F[Exception, VersionResponse]
  }

}

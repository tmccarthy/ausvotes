package au.id.tmm.ausvotes.api.controllers

import au.id.tmm.ausvotes.api.model.diagnostics.VersionResponse
import au.id.tmm.ausvotes.shared.io.actions.Resources
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps

object DiagnosticController {

  def version[F[+_, +_] : Monad : Resources]: F[Exception, VersionResponse] = Resources.asString("/version.txt").flatMap {
    case Some(version) => Monad.pure(VersionResponse(version))
    case None => Monad.leftPure(new Exception("Version file missing"))
  }

}

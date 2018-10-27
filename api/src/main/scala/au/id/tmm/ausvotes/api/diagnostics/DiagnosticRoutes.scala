package au.id.tmm.ausvotes.api.diagnostics

import au.id.tmm.ausvotes.api.Routes
import au.id.tmm.ausvotes.shared.io.actions.Resources
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import unfiltered.request.{GET, Path, Seg}
import unfiltered.response.{InternalServerError, ResponseString}

object DiagnosticRoutes {

  def apply[F[+_, +_] : Monad : Resources]: Routes[F] = {
    case GET(Path(Seg("version" :: Nil))) => Resources.asString("/version.txt").map {
      case Some(version) => ResponseString(version)
      case None => InternalServerError andThen ResponseString("version undefined")
    }
  }

}

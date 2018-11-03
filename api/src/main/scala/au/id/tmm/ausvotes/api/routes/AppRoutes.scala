package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.Routes
import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.shared.aws.actions.LambdaActions.InvokesLambda
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.actions.Resources
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad

object AppRoutes {

  def apply[F[+_, +_] : Monad : Resources : ReadsS3 : InvokesLambda](config: Config): Routes[F] =
    DiagnosticRoutes[F] orElse
      RecountRoutes[F](config) orElse
      NotFoundRoute[F]

}

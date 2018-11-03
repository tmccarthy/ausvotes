package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.Routes
import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.controllers.RecountController
import au.id.tmm.ausvotes.api.utils.unfiltered.ResponseJson
import au.id.tmm.ausvotes.shared.aws.actions.LambdaActions.InvokesLambda
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import unfiltered.request._

object RecountRoutes {

  def apply[F[+_, +_] : Monad : ReadsS3 : InvokesLambda](config: Config): Routes[F] = {
    val controller = new RecountController(config)

    {
      case req @ GET(Path(Seg("recount" :: electionString :: stateString :: Nil))) =>
        controller.recount[F](electionString, stateString, QueryParams.unapply(req).get.mapValues(_.toList))
          .map(ResponseJson(_))
    }
  }

}

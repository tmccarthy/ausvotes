package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.Routes
import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.controllers.RecountController
import au.id.tmm.ausvotes.api.errors.recount.RecountException
import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest
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
        val queryParams = QueryParams.unapply(req).get.mapValues(_.toList)

        for {
          recountRequest <- Monad.fromEither(buildRecountRequest(electionString, stateString, queryParams))
            .leftMap(RecountException.BadRequestError)

          recountResult <- controller.recount[F](recountRequest)
        } yield ResponseJson(recountResult)
    }
  }

  private[routes] def buildRecountRequest(
                                           rawElection: String,
                                           rawState: String,
                                           queryParams: Map[String, List[String]],
                                         ): Either[RecountApiRequest.ConstructionException, RecountApiRequest] =
    RecountApiRequest.buildFrom(
      rawElection,
      rawState,
      rawNumVacancies = queryParams.get("vacancies").flatMap(_.headOption),
      rawIneligibleCandidates = queryParams.get("ineligibleCandidates").flatMap(_.headOption),
    )

}

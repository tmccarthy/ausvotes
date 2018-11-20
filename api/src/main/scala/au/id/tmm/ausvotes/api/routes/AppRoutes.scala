package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.InfallibleRoutes
import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.errors.NotFoundException
import au.id.tmm.ausvotes.api.model.GenericErrorResponse
import au.id.tmm.ausvotes.api.utils.unfiltered.ResponseJson
import au.id.tmm.ausvotes.shared.aws.actions.LambdaActions.InvokesLambda
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.actions.{Log, Resources}
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountResult
import unfiltered.response.{InternalServerError, NotFound, ResponseFunction}

object AppRoutes {

  def apply[F[+_, +_] : Monad : Resources : FetchCanonicalCountResult : ReadsS3 : InvokesLambda : Log](config: Config): InfallibleRoutes[F] =
    DiagnosticRoutes[F] orElse
      RecountRoutes[F](config) orElse
      NotFoundRoute[F] andThen
      recoverError[F]

  /*_*/
  private def recoverError[F[+_, +_] : Monad : Log](
                                                     responseOrException: F[Exception, ResponseFunction[Any]],
                                                   ): F[Nothing, ResponseFunction[Any]] =
    responseOrException.attempt.flatMap {
      case Right(response) => Monad.pure(response)
      case Left(exception) => handleException(exception)
    }
  /*_*/

  private[routes] def handleException[F[+_, +_] : Monad : Log](
                                                                exception: Exception,
                                                              ): F[Nothing, ResponseFunction[Any]] = exception match {
    case NotFoundException(path) =>
      for {
        _ <- Log.logInfo(LoggedEvent("NOT_FOUND_RESPONSE", List("path" -> path), None))
      } yield NotFound andThen ResponseJson(GenericErrorResponse(s"""Not found "$path""""))

    case e =>
      for {
        _ <- Log.logError(LoggedEvent("ERROR_RESPONSE", List.empty, Some(e)))
      } yield InternalServerError andThen ResponseJson(GenericErrorResponse())
  }

}

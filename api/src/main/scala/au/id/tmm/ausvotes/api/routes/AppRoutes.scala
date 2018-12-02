package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.errors.NotFoundException
import au.id.tmm.ausvotes.api.model.GenericErrorResponse
import au.id.tmm.ausvotes.api.utils.unfiltered.ResponseJson
import au.id.tmm.ausvotes.api.{CompleteRoutes, InfallibleRoutes, PartialRoutes}
import au.id.tmm.ausvotes.shared.aws.actions.LambdaActions.InvokesLambda
import au.id.tmm.ausvotes.shared.aws.actions.S3Actions.ReadsS3
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.actions.{Log, Resources}
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import unfiltered.netty.ReceivedMessage
import unfiltered.request.{DelegatingRequest, HttpRequest}
import unfiltered.response.{InternalServerError, NotFound, ResponseFunction}

object AppRoutes {

  def apply[F[+_, +_] : Monad : Resources : ReadsS3 : InvokesLambda : Log](config: Config): InfallibleRoutes[F] = {

    val allRoutes: List[PartialRoutes[F]] = List(
      DiagnosticRoutes[F],
      RecountRoutes[F](config),
    )

    val partialRoutes: PartialRoutes[F] = allRoutes.reduce(_ orElse _)

    val stripBasePath = stripBasePathUsing(config)

    val completeRoutes: CompleteRoutes[F] = req => partialRoutes.applyOrElse(stripBasePath(req), NotFoundRoute[F])

    completeRoutes andThen recoverError[F]
  }

  private def stripBasePathUsing(config: Config): HttpRequest[ReceivedMessage] => HttpRequest[ReceivedMessage] = {
    val prefixToStrip = config.basePath.mkString(start = "/", sep = "/", end = "")

    request => new DelegatingRequest[ReceivedMessage](request) {
      override def uri: String = super.uri.stripPrefix(prefixToStrip)
    }
  }

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

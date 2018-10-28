package au.id.tmm.ausvotes.api

import au.id.tmm.ausvotes.api.errors.NotFoundException
import au.id.tmm.ausvotes.api.model.GenericErrorResponse
import au.id.tmm.ausvotes.api.routes.AppRoutes
import au.id.tmm.ausvotes.api.utils.unfiltered.ResponseJson
import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import io.netty.handler.codec.http.HttpResponse
import org.slf4j.{Logger, LoggerFactory}
import scalaz.zio.{ExitResult, IO, RTS}
import unfiltered.netty.Server
import unfiltered.netty.async.{Plan, Planify}
import unfiltered.response.{InternalServerError, NotFound, ResponseFunction}

object Api {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {

    val rts = new RTS {}

    val routes = AppRoutes[IO]

    val intent: Plan.Intent = buildIntent(rts, routes)

    val plan: Plan = Planify(intent)

    Server
      .http(8080)
      .plan(plan)
      .start()
  }

  private def buildIntent(rts: RTS, routes: Routes[IO]): Plan.Intent = {
    case req => {
      val io = routes(req)

      rts.unsafeRunAsync(io) { exitResult: ExitResult[Exception, ResponseFunction[HttpResponse]] =>
        req.respond(exitResult.fold(
          completed = identity[ResponseFunction[HttpResponse]],
          failed = {
            case (exception: Exception, _) => {
              logger.error("An error occurred", exception)

              responseGiven(exception)
            }
          },
          interrupted = throwables => throw throwables.head,
        ))
      }
    }
  }

  private def responseGiven(exception: Exception): ResponseFunction[Any] = exception match {
    case NotFoundException() => NotFound andThen ResponseJson(GenericErrorResponse("Not found"))
    case _ => InternalServerError andThen ResponseJson(GenericErrorResponse())
  }

}

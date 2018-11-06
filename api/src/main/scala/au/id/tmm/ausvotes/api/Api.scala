package au.id.tmm.ausvotes.api

import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.routes.AppRoutes
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import io.netty.handler.codec.http.HttpResponse
import scalaz.zio.{ExitResult, IO, RTS}
import unfiltered.netty.Server
import unfiltered.netty.async.{Plan, Planify}
import unfiltered.response.ResponseFunction

object Api {

  def main(args: Array[String]): Unit = {

    val ioRuntime = new RTS {}

    val config = ioRuntime.unsafeRun(Config.fromEnvironment[IO])

    val routes = AppRoutes[IO](config)

    val intent: Plan.Intent = buildIntent(ioRuntime, routes)

    val plan: Plan = Planify(intent)

    Server
      .http(8080)
      .plan(plan)
      .start()
  }

  private def buildIntent(ioRuntime: RTS, routes: InfallibleRoutes[IO]): Plan.Intent = {
    case req => {
      val io = routes(req)

      ioRuntime.unsafeRunAsync(io) { exitResult: ExitResult[Nothing, ResponseFunction[HttpResponse]] =>
        //noinspection NotImplementedCode
        req.respond(exitResult.fold(
          completed = identity[ResponseFunction[HttpResponse]],
          failed = (_, _) => ???, // Impossible
          interrupted = throwables => throw throwables.head,
        ))
      }
    }
  }

}

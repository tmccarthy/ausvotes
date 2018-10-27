package au.id.tmm.ausvotes.api

import au.id.tmm.ausvotes.shared.io.typeclasses.IOInstances._
import io.netty.handler.codec.http.HttpResponse
import scalaz.zio.{ExitResult, IO, RTS}
import unfiltered.netty.Server
import unfiltered.netty.async.{Plan, Planify}
import unfiltered.response.ResponseFunction

object Api {

  def main(args: Array[String]): Unit = {

    val rts = new RTS {}

    val routes = Routes[IO]

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

      rts.unsafeRunAsync(io) { exitResult: ExitResult[Nothing, ResponseFunction[HttpResponse]] =>
        req.respond(exitResult.fold(
          completed = identity[ResponseFunction[HttpResponse]],
          failed = {
            case (_, throwables) => throw throwables.head
          },
          interrupted = throwables => throw throwables.head,
        ))
      }
    }
  }

}

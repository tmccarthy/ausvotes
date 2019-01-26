package au.id.tmm.ausvotes.api

import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.routes.AppRoutes
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching.{CanonicalCountSummaryCache, GroupsAndCandidatesCache}
import io.netty.handler.codec.http.HttpResponse
import scalaz.zio.{ExitResult, IO, RTS}
import unfiltered.netty.Server
import unfiltered.netty.async.{Plan, Planify}
import unfiltered.response.ResponseFunction

import scala.annotation.tailrec

object Api {

  def main(args: Array[String]): Unit = {

    val ioRuntime = new RTS {}

    val startupResources = ioRuntime.unsafeRun(buildStartupResources)

    val routes = AppRoutes[IO](startupResources.config)

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
        req.respond(exitResult.fold(
          completed = identity[ResponseFunction[HttpResponse]],
          failed = handleFailure,
        ))
      }
    }
  }

  @tailrec
  private def handleFailure(failureCause: ExitResult.Cause[Nothing]): ResponseFunction[HttpResponse] =
  //noinspection NotImplementedCode
    failureCause match {
      case ExitResult.Cause.Checked(e) => ??? // Impossible
      case ExitResult.Cause.Unchecked(t) => throw t
      case ExitResult.Cause.Interruption => throw new InterruptedException()
      case ExitResult.Cause.Both(left, right) => handleFailure(left)
      case ExitResult.Cause.Then(left, right) => handleFailure(left)
    }


  private val buildStartupResources: IO[Exception, StartupResources] =
    for {
      config <- Config.fromEnvironment[IO]
      groupsAndCandidatesCache <- GroupsAndCandidatesCache(config.recountDataBucket)
      canonicalCountResultCache <- CanonicalCountSummaryCache(groupsAndCandidatesCache)
    } yield StartupResources(config, groupsAndCandidatesCache, canonicalCountResultCache)

  final case class StartupResources(
                                     config: Config,
                                     groupsAndCandidatesCache: GroupsAndCandidatesCache,
                                     canonicalCountResultCache: CanonicalCountSummaryCache,
                                   )

}

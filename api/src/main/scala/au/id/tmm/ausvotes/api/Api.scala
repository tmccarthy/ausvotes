package au.id.tmm.ausvotes.api

import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.routes.AppRoutes
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.recountresources.entities.cached_fetching.{CanonicalCountSummaryCache, GroupsAndCandidatesCache}
import au.id.tmm.bfect.ziointerop._
import io.netty.handler.codec.http.HttpResponse
import scalaz.zio.{DefaultRuntime, Exit, IO, Runtime}
import unfiltered.netty.Server
import unfiltered.netty.async.{Plan, Planify}
import unfiltered.response.ResponseFunction

import scala.annotation.tailrec

object Api {

  def main(args: Array[String]): Unit = {

    val ioRuntime = new DefaultRuntime {}

    val startupResources = ioRuntime.unsafeRun(buildStartupResources)

    val routes = AppRoutes[IO](startupResources.config)

    val intent: Plan.Intent = buildIntent(ioRuntime, routes)

    val plan: Plan = Planify(intent)

    Server
      .http(8080)
      .plan(plan)
      .start()
  }

  private def buildIntent(ioRuntime: Runtime[Any], routes: InfallibleRoutes[IO]): Plan.Intent = {
    case req => {
      val io = routes(req)

      ioRuntime.unsafeRunAsync(io) { exitResult: Exit[Nothing, ResponseFunction[HttpResponse]] =>
        req.respond(exitResult.fold(
          completed = identity[ResponseFunction[HttpResponse]],
          failed = handleFailure,
        ))
      }
    }
  }

  @tailrec
  private def handleFailure(failureCause: Exit.Cause[Nothing]): ResponseFunction[HttpResponse] =
  //noinspection NotImplementedCode
    failureCause match {
      case Exit.Cause.Fail(e) => ??? // Impossible
      case Exit.Cause.Die(t) => throw t
      case Exit.Cause.Interrupt => throw new InterruptedException()
      case Exit.Cause.Both(left, right) => handleFailure(left)
      case Exit.Cause.Then(left, right) => handleFailure(left)
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

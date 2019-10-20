package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import java.nio.file.Paths

import au.id.tmm.ausvotes.data_sources.aec.federal.CanonicalFederalAecDataInstances
import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.bfect.BME.Ops
import au.id.tmm.bfect.ziointerop._
import zio.{App, IO}

object Main extends App {

  override def run(args: List[String]): IO[Nothing, Int] =
    applicationLogic(args)
      .timedLog("APP_RUN")
      .attempt.map(_.fold(_ => 1, _ => 0))

  private def applicationLogic(rawArgs: List[String]): IO[Exception, Unit] = {
    val dataStorePath = Paths.get("rawData")

    implicit val fetchDataInstances: CanonicalFederalAecDataInstances =
      CanonicalFederalAecDataInstances(dataStorePath, replaceExisting = false)

    import fetchDataInstances._

    for {
      args <- IO.fromEither(Args.from(rawArgs))

      _ <- AecResourcesRetrieval.withElectionResources[Unit](args.election) { case (election, groupsAndCandidates, divisionsAndPollingPlaces, countData, ballots) =>
        for {
          dataBundle <- DataBundleConstruction
            .constructDataBundle(election, groupsAndCandidates, divisionsAndPollingPlaces, countData, ballots)
            .timedLog("CONSTRUCT_DATA_BUNDLE", "election" -> election.election, "state" -> election.state)
          _ <- DataBundleWriting
            .writeToS3Bucket[IO](args.s3Bucket, dataBundle)
            .timedLog("WRITE_DATA_BUNDLE", "election" -> election.election, "state" -> election.state)
        } yield ()
      }
    } yield ()
  }

}

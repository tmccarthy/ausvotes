package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.ausvotes.shared.aws.actions.IOInstances._
import au.id.tmm.ausvotes.shared.io.Logging.LoggingOps
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import scalaz.zio.{App, IO}

object Main extends App {

  override def run(args: List[String]): IO[Nothing, Main.ExitStatus] =
    applicationLogic(args)
      .timedLog("APP_RUN")
      .attempt.map(_.fold(_ => 1, _ => 0))
      .map(ExitStatus.ExitNow(_))

  private def applicationLogic(rawArgs: List[String]): IO[Exception, Unit] = {
    for {
      args <- IO.fromEither(Args.from(rawArgs))

      _ <- AecResourcesRetrieval.withElectionResources[Unit](args.dataStorePath, args.election) { case (election, groupsAndCandidates, divisionsAndPollingPlaces, countData, ballots) =>
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

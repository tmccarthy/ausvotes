package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.ausvotes.tasks.generatepreferencetrees.Logging.IoOps
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

      _ <- AecResourcesRetrieval.withElectionResources[Unit](args.dataStorePath, args.election) { case (election, state, groupsAndCandidates, divisionsAndPollingPlaces, ballots) =>
        for {
          dataBundle <- DataBundleConstruction
            .constructDataBundle(election, state, groupsAndCandidates, divisionsAndPollingPlaces, ballots)
            .timedLog("CONSTRUCT_DATA_BUNDLE", "election" -> election, "state" -> state)
          _ <- DataBundleWriting
            .writeToS3Bucket(args.s3Bucket, dataBundle)
            .timedLog("WRITE_DATA_BUNDLE", "election" -> election, "state" -> state)
        } yield Unit
      }
    } yield Unit
  }

}

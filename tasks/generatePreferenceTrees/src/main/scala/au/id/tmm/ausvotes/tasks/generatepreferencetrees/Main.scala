package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import java.nio.file.Paths

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.ballots.FetchSenateBallotsFromRaw
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data.FetchSenateCountDataFromRaw
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.{FetchDivisionsAndFederalPollingPlacesFromRaw, FetchSenateGroupsAndCandidatesFromRaw}
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.{FetchDivisionsAndFederalPollingPlaces, FetchSenateBallots, FetchSenateCountData, FetchSenateGroupsAndCandidates}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.{AecResourceStore, FetchRawFederalElectionData}
import au.id.tmm.ausvotes.data_sources.common.DownloadToPath
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
    val dataStorePath = Paths.get("rawData")

    implicit val downloadToPath: DownloadToPath[IO] = DownloadToPath.IfTargetMissing

    implicit val aecResourceStore: AecResourceStore[IO] = AecResourceStore(dataStorePath)

    import aecResourceStore._

    implicit val fetchRawFederalElectionData: FetchRawFederalElectionData[IO] = FetchRawFederalElectionData()

    implicit val fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO] = FetchSenateGroupsAndCandidatesFromRaw[IO]
    implicit val fetchDivisions: FetchDivisionsAndFederalPollingPlaces[IO] = FetchDivisionsAndFederalPollingPlacesFromRaw[IO]
    implicit val fetchCountData: FetchSenateCountData[IO] = FetchSenateCountDataFromRaw[IO]
    implicit val fetchSenateBallots: FetchSenateBallots[IO] = FetchSenateBallotsFromRaw[IO]

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

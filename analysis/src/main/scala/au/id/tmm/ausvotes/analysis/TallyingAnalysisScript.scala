package au.id.tmm.ausvotes.analysis

import java.nio.file.Paths

import au.id.tmm.ausvotes.core.io_actions.implementations._
import au.id.tmm.ausvotes.core.tallying.impl.FetchTallyImpl
import au.id.tmm.ausvotes.data_sources.aec.federal.FetchSenateHtv
import au.id.tmm.ausvotes.data_sources.aec.federal.impl.htv.FetchSenateHtvFromHardcoded
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.ballots.FetchSenateBallotsFromRaw
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data.FetchSenateCountDataFromRaw
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.{FetchDivisionsAndFederalPollingPlacesFromRaw, FetchSenateGroupsAndCandidatesFromRaw}
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.{FetchDivisionsAndFederalPollingPlaces, FetchSenateBallots, FetchSenateCountData, FetchSenateGroupsAndCandidates}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.{AecResourceStore, FetchRawFederalElectionData}
import au.id.tmm.ausvotes.data_sources.common.DownloadToPath
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances.zioIsABME
import scalaz.zio.{IO, RTS}

abstract class TallyingAnalysisScript extends RTS {

  def main(args: Array[String]): Unit = {
    val dataStorePath = Paths.get("rawData")
    val jsonCachePath = Paths.get("rawData").resolve("jsonCache")

    implicit val jsonCache: OnDiskJsonCache = new OnDiskJsonCache(jsonCachePath)
    implicit val downloadToPath: DownloadToPath[IO] = DownloadToPath.IfTargetMissing

    implicit val aecResourceStore: AecResourceStore[IO] = AecResourceStore(dataStorePath)

    import aecResourceStore._

    implicit val fetchRawFederalElectionData: FetchRawFederalElectionData[IO] = FetchRawFederalElectionData(dataStorePath)

    implicit val fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO] = FetchSenateGroupsAndCandidatesFromRaw[IO]
    implicit val fetchDivisions: FetchDivisionsAndFederalPollingPlaces[IO] = FetchDivisionsAndFederalPollingPlacesFromRaw[IO]
    implicit val fetchCountData: FetchSenateCountData[IO] = FetchSenateCountDataFromRaw[IO]
    implicit val fetchSenateBallots: FetchSenateBallots[IO] = FetchSenateBallotsFromRaw[IO]
    implicit val fetchHtv: FetchSenateHtv[IO] = FetchSenateHtvFromHardcoded[IO]

    implicit val fetchTallies: FetchTallyImpl = unsafeRun(FetchTallyImpl())

    run()
  }

  def run()(
    implicit
    jsonCache: OnDiskJsonCache,
    fetchRawFederalElectionData: FetchRawFederalElectionData[IO],
    fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO],
    fetchDivisions: FetchDivisionsAndFederalPollingPlaces[IO],
    fetchCountData: FetchSenateCountData[IO],
    fetchSenateBallots: FetchSenateBallots[IO],
    fetchHtv: FetchSenateHtv[IO],
    fetchTallies: FetchTallyImpl,
  ): Unit

}

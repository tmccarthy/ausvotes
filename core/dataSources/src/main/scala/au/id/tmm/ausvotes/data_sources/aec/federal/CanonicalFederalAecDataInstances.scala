package au.id.tmm.ausvotes.data_sources.aec.federal

import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.aec.federal.extras.FetchSenateHtv
import au.id.tmm.ausvotes.data_sources.aec.federal.extras.htv.FetchSenateHtvFromHardcoded
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.ballots.FetchSenateBallotsFromRaw
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data.FetchSenateCountDataFromRaw
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.{FetchDivisionsAndFederalPollingPlacesFromRaw, FetchSenateGroupsAndCandidatesFromRaw}
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.{FetchDivisionsAndFederalPollingPlaces, FetchSenateBallots, FetchSenateCountData, FetchSenateGroupsAndCandidates}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.{AecResourceStore, FetchRawFederalElectionData}
import au.id.tmm.ausvotes.data_sources.common.{DownloadToPath, JsonCache}
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import scalaz.zio.IO

class CanonicalFederalAecDataInstances private (dataStorePath: Path) {

  private val jsonCachePath = dataStorePath.resolve("jsonCache")

  implicit val jsonCache: JsonCache[IO] = JsonCache.OnDisk(jsonCachePath)
  implicit val downloadToPath: DownloadToPath[IO] = DownloadToPath.IfTargetMissing

  implicit val aecResourceStore: AecResourceStore[IO] = AecResourceStore(dataStorePath)

  import aecResourceStore._

  implicit val fetchRawFederalElectionData: FetchRawFederalElectionData[IO] = FetchRawFederalElectionData()

  implicit val fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO] = FetchSenateGroupsAndCandidatesFromRaw[IO]
  implicit val fetchDivisions: FetchDivisionsAndFederalPollingPlaces[IO] = FetchDivisionsAndFederalPollingPlacesFromRaw[IO]
  implicit val fetchCountData: FetchSenateCountData[IO] = FetchSenateCountDataFromRaw[IO]
  implicit val fetchSenateBallots: FetchSenateBallots[IO] = FetchSenateBallotsFromRaw[IO]
  implicit val fetchHtv: FetchSenateHtv[IO] = FetchSenateHtvFromHardcoded[IO]

}

object CanonicalFederalAecDataInstances {

  def apply(dataStorePath: Path): CanonicalFederalAecDataInstances = new CanonicalFederalAecDataInstances(dataStorePath)

}

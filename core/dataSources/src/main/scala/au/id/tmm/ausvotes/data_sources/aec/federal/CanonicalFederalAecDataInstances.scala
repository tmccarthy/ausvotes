package au.id.tmm.ausvotes.data_sources.aec.federal

import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.aec.federal.extras.FetchSenateHtv
import au.id.tmm.ausvotes.data_sources.aec.federal.extras.htv.FetchSenateHtvFromHardcoded
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.ballots.FetchSenateBallotsFromRaw
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data.FetchSenateCountDataFromRaw
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.{FetchDivisionsAndFederalPollingPlacesFromRaw, FetchSenateGroupsAndCandidatesFromRaw}
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.{FetchDivisionsAndFederalPollingPlaces, FetchSenateBallots, FetchSenateCountData, FetchSenateGroupsAndCandidates}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.{AecResourceStore, FetchRawFederalElectionData}
import au.id.tmm.ausvotes.data_sources.common.JsonCache
import au.id.tmm.bfect.fs2interop._
import au.id.tmm.bfect.ziointerop._
import zio.IO

class CanonicalFederalAecDataInstances private (dataStorePath: Path, replaceExisting: Boolean) {

  private val jsonCachePath = dataStorePath.resolve("jsonCache")

  implicit val jsonCache: JsonCache[IO] = JsonCache.OnDisk(jsonCachePath)

  implicit val aecResourceStore: AecResourceStore[IO] = AecResourceStore(dataStorePath, replaceExisting)

  implicit val fetchRawFederalElectionData: FetchRawFederalElectionData[IO] = FetchRawFederalElectionData(
    aecResourceStore.makeSourceForFederalPollingPlaceResource,
    aecResourceStore.makeSourceForFormalSenatePreferencesResource,
    aecResourceStore.makeSourceForSenateDistributionOfPreferencesResource,
    aecResourceStore.makeSourceForSenateFirstPreferencesResource,
  )

  implicit val fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO] = FetchSenateGroupsAndCandidatesFromRaw[IO]
  implicit val fetchDivisions: FetchDivisionsAndFederalPollingPlaces[IO] = FetchDivisionsAndFederalPollingPlacesFromRaw[IO]
  implicit val fetchCountData: FetchSenateCountData[IO] = FetchSenateCountDataFromRaw[IO]
  implicit val fetchSenateBallots: FetchSenateBallots[IO] = FetchSenateBallotsFromRaw[IO]
  implicit val fetchHtv: FetchSenateHtv[IO] = FetchSenateHtvFromHardcoded[IO]

}

object CanonicalFederalAecDataInstances {

  def apply(dataStorePath: Path, replaceExisting: Boolean): CanonicalFederalAecDataInstances =
    new CanonicalFederalAecDataInstances(dataStorePath, replaceExisting: Boolean)

}

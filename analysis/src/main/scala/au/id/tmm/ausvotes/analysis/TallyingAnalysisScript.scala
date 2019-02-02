package au.id.tmm.ausvotes.analysis

import java.nio.file.Paths

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.io_actions.implementations._
import au.id.tmm.ausvotes.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances.zioIsABME
import scalaz.zio.{IO, RTS}

abstract class TallyingAnalysisScript extends RTS {

  def main(args: Array[String]): Unit = {
    val dataStorePath = Paths.get("rawData")
    val jsonCachePath = Paths.get("rawData").resolve("jsonCache")

    val parsedDataStore = ParsedDataStore(RawDataStore(AecResourceStore.at(dataStorePath)))
    implicit val jsonCache: OnDiskJsonCache = new OnDiskJsonCache(jsonCachePath)

    implicit val fetchGroupsAndCandidates: FetchGroupsAndCandidatesFromParsedDataStore = new FetchGroupsAndCandidatesFromParsedDataStore(parsedDataStore)
    implicit val fetchDivisions: FetchDivisionsAndPollingPlacesFromParsedDataStore = new FetchDivisionsAndPollingPlacesFromParsedDataStore(parsedDataStore)
    implicit val fetchCountData: FetchSenateCountDataFromParsedDataStore = new FetchSenateCountDataFromParsedDataStore(parsedDataStore)
    implicit val fetchHtv: FetchSenateHtvFromHardcoded[IO] = new FetchSenateHtvFromHardcoded[IO]

    implicit val fetchTallies: FetchTallyAsWithComputation = unsafeRun(FetchTallyAsWithComputation(parsedDataStore))

    run()
  }

  def run()(
    implicit
    jsonCache: OnDiskJsonCache,
    fetchGroupsAndCandidates: FetchGroupsAndCandidatesFromParsedDataStore,
    fetchDivisions: FetchDivisionsAndPollingPlacesFromParsedDataStore,
    fetchCountData: FetchSenateCountDataFromParsedDataStore,
    fetchHtv: FetchSenateHtvFromHardcoded[IO],
    fetchTallies: FetchTallyAsWithComputation,
  ): Unit

}

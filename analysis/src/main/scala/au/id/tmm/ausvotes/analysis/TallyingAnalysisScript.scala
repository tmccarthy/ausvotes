package au.id.tmm.ausvotes.analysis

import java.nio.file.Paths

import au.id.tmm.ausvotes.core.tallying.impl.FetchTallyImpl
import au.id.tmm.ausvotes.data_sources.aec.federal.CanonicalFederalAecDataInstances
import au.id.tmm.ausvotes.data_sources.aec.federal.extras.FetchSenateHtv
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.{FetchDivisionsAndFederalPollingPlaces, FetchSenateBallots, FetchSenateCountData, FetchSenateGroupsAndCandidates}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.FetchRawFederalElectionData
import au.id.tmm.ausvotes.data_sources.common.JsonCache
import scalaz.zio.{IO, RTS}

abstract class TallyingAnalysisScript extends RTS {

  def main(args: Array[String]): Unit = {
    val dataStorePath = Paths.get("rawData")

    val federalAecDataInstances = CanonicalFederalAecDataInstances(dataStorePath)

    import federalAecDataInstances._

    implicit val fetchTallies: FetchTallyImpl = unsafeRun(FetchTallyImpl())

    run()
  }

  def run()(
    implicit
    jsonCache: JsonCache[IO],
    fetchRawFederalElectionData: FetchRawFederalElectionData[IO],
    fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO],
    fetchDivisions: FetchDivisionsAndFederalPollingPlaces[IO],
    fetchCountData: FetchSenateCountData[IO],
    fetchSenateBallots: FetchSenateBallots[IO],
    fetchHtv: FetchSenateHtv[IO],
    fetchTallies: FetchTallyImpl,
  ): Unit

}

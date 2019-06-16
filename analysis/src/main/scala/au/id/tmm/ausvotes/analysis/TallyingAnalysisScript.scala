package au.id.tmm.ausvotes.analysis

import java.nio.file.Paths

import au.id.tmm.ausvotes.core.computations.StvBallotWithFacts
import au.id.tmm.ausvotes.core.tallying.impl.FetchTallyImpl
import au.id.tmm.ausvotes.core.tallying.{FetchTallyForElection, FetchTallyForSenate}
import au.id.tmm.ausvotes.data_sources.aec.federal.CanonicalFederalAecDataInstances
import au.id.tmm.ausvotes.data_sources.aec.federal.extras.FetchSenateHtv
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.{FetchDivisionsAndFederalPollingPlaces, FetchSenateBallots, FetchSenateCountData, FetchSenateGroupsAndCandidates}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.FetchRawFederalElectionData
import au.id.tmm.ausvotes.data_sources.common.JsonCache
import au.id.tmm.ausvotes.model.federal.FederalBallotJurisdiction
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotId, SenateElection, SenateElectionForState}
import au.id.tmm.bfect.ziointerop._
import scalaz.zio.{DefaultRuntime, IO}

abstract class TallyingAnalysisScript extends DefaultRuntime {

  def main(args: Array[String]): Unit = {
    val dataStorePath = Paths.get("rawData")

    val federalAecDataInstances = CanonicalFederalAecDataInstances(dataStorePath, replaceExisting = false)

    import federalAecDataInstances._

    implicit val fetchTallies: FetchTallyImpl[IO] = FetchTallyImpl[IO]
    implicit val fetchTalliesForElection: FetchTallyForElection[IO, SenateElection, StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]] = FetchTallyForSenate[IO]

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
    fetchTallies: FetchTallyImpl[IO],
    fetchTalliesForElection: FetchTallyForElection[IO, SenateElection, StvBallotWithFacts[SenateElectionForState, FederalBallotJurisdiction, SenateBallotId]],
  ): Unit

}

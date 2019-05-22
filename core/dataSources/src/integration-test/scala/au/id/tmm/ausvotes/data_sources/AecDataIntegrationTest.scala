package au.id.tmm.ausvotes.data_sources

import au.id.tmm.ausvotes.data_sources.aec.federal.CanonicalFederalAecDataInstances
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.{FetchRawFederalPollingPlaces, FetchRawFormalSenatePreferences, FetchRawSenateDistributionOfPreferences, FetchRawSenateFirstPreferences}
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop._
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}
import fs2.Stream
import scalaz.zio.{DefaultRuntime, IO}

class AecDataIntegrationTest extends ImprovedFlatSpec with NeedsCleanDirectory with DefaultRuntime {

  private val instances = CanonicalFederalAecDataInstances(cleanDirectory)

  import instances._

  private val senateElection = SenateElection.`2016`
  private val federalElection = senateElection.federalElection
  private val state = State.NT
  private val election = senateElection.electionForState(state).get

  private def sizeOf[O](streamIo: IO[_, Stream[IO[Throwable, +?], O]]): Int = unsafeRun {
    for {
      stream <- streamIo
      chunk <- stream.compile.toChunk
      size = chunk.size
    } yield size
  }

  "the loading of distribution of preferences data" should "be successful" in {
    val numLines = sizeOf(FetchRawSenateDistributionOfPreferences.senateDistributionOfPreferencesFor(election))

    assert(numLines === 21)
  }

  "the loading of raw first preferences data" should "download the data" in {
    val numLines = sizeOf(FetchRawSenateFirstPreferences.senateFirstPreferencesFor(senateElection))

    assert(numLines === 837)
  }

  "the loading of raw formal preferences data" should "download the data" in {
    val numLines = sizeOf(FetchRawFormalSenatePreferences.formalSenatePreferencesFor(election))

    assert(numLines === 102027)
  }

  "the loading of raw polling places data" should "download the data" in {
    val numLines = sizeOf(FetchRawFederalPollingPlaces.federalPollingPlacesForElection(federalElection))

    assert(numLines === 8328)
  }
}

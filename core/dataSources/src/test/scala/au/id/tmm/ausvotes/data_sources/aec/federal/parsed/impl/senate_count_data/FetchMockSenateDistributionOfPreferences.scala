package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data

import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawSenateDistributionOfPreferences
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.FetchRawFederalElectionData
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.resources.{FederalPollingPlacesResource, FormalSenatePreferencesResource, SenateDistributionOfPreferencesResource, SenateFirstPreferencesResource}
import au.id.tmm.ausvotes.data_sources.common.MakeSource
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.testIOIsABME
import au.id.tmm.ausvotes.shared.io.typeclasses.SyncEffects
import au.id.tmm.utilities.geo.australia.State
import fs2.Stream

import scala.io.Source

class FetchMockSenateDistributionOfPreferences(workingDir: Path) extends FetchRawSenateDistributionOfPreferences[BasicTestIO] {

  private implicit val makeDistributionOfPreferencesSource: MakeSource[BasicTestIO, Exception, SenateDistributionOfPreferencesResource] =
    new MakeSource[BasicTestIO, Exception, SenateDistributionOfPreferencesResource] {
      override def makeSourceFor(resource: SenateDistributionOfPreferencesResource): BasicTestIO[Exception, Source] =
        resource match {
          case SenateDistributionOfPreferencesResource(SenateElectionForState(SenateElection.`2016`, state @ State.WA)) =>
            MakeSource.fromGzipResource[BasicTestIO]().makeSourceFor(s"/au/id/tmm/ausvotes/core/fixtures/SenateStateDOPDownload-20499-${state.abbreviation}.csv.gz")

          case SenateDistributionOfPreferencesResource(SenateElectionForState(SenateElection.`2016`, state)) =>
            MakeSource.fromResource[BasicTestIO]().makeSourceFor(s"/au/id/tmm/ausvotes/core/fixtures/SenateStateDOPDownload-20499-${state.abbreviation}.csv")

          case r => MakeSource.never(implicitly[SyncEffects[BasicTestIO]]).makeSourceFor(r)
        }
    }

  private implicit val makeFederalPollingPlacesResourceSource: MakeSource[BasicTestIO, Exception, FederalPollingPlacesResource] = MakeSource.never
  private implicit val makeFormalSenatePreferencesResourceSource: MakeSource[BasicTestIO, Exception, FormalSenatePreferencesResource] = MakeSource.never
  private implicit val makeSenateFirstPreferencesResourceSource: MakeSource[BasicTestIO, Exception, SenateFirstPreferencesResource] = MakeSource.never

  private val underlying: FetchRawFederalElectionData[BasicTestIO] = FetchRawFederalElectionData[BasicTestIO](workingDir)

  override def senateDistributionOfPreferencesFor(
                                                   election: SenateElectionForState,
                                                 ): BasicTestIO[FetchRawSenateDistributionOfPreferences.Error, Stream[BasicTestIO[Throwable, +?], FetchRawSenateDistributionOfPreferences.Row]] = {
    underlying.senateDistributionOfPreferencesFor(election)
  }

}

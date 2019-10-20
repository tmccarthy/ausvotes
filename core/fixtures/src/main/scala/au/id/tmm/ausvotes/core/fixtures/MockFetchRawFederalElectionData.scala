package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.data_sources.aec.federal.raw._
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.FetchRawFederalElectionData
import au.id.tmm.ausvotes.data_sources.aec.federal.resources.{FederalPollingPlacesResource, FormalSenatePreferencesResource, SenateDistributionOfPreferencesResource, SenateFirstPreferencesResource}
import au.id.tmm.ausvotes.data_sources.common.streaming.MakeSource
import au.id.tmm.ausvotes.data_sources.common.streaming.OpeningInputStreams._
import au.id.tmm.ausvotes.data_sources.common.streaming.ReadingInputStreams._
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.bfect.BME
import au.id.tmm.ausgeo.State
import fs2.Stream

object MockFetchRawFederalElectionData extends FetchRawSenateFirstPreferences[BasicTestIO]
  with FetchRawSenateDistributionOfPreferences[BasicTestIO]
  with FetchRawFederalPollingPlaces[BasicTestIO]
  with FetchRawFormalSenatePreferences[BasicTestIO] {

  private val makeDistributionOfPreferencesSource: MakeSource[BasicTestIO, Exception, SenateDistributionOfPreferencesResource] = {
    case SenateDistributionOfPreferencesResource(SenateElectionForState(SenateElection.`2016`, state @ State.WA)) =>
      streamLines(openGzip(openResource[BasicTestIO](s"/au/id/tmm/ausvotes/core/fixtures/SenateStateDOPDownload-20499-${state.abbreviation}.csv.gz")))

    case SenateDistributionOfPreferencesResource(SenateElectionForState(SenateElection.`2016`, state)) =>
      streamLines(openResource[BasicTestIO](s"/au/id/tmm/ausvotes/core/fixtures/SenateStateDOPDownload-20499-${state.abbreviation}.csv"))

    case _ => BME[BasicTestIO].leftPure(new Exception())
  }

  private def makeSourceFor[R](resolveResource: PartialFunction[R, String]): MakeSource[BasicTestIO, Exception, R] = r =>
    resolveResource.lift(r) match {
      case Some(resourcePath) => streamLines(openResource[BasicTestIO](resourcePath))
      case None               => BME[BasicTestIO].leftPure(new RuntimeException(s"No test data for $r"))
    }

  private implicit val makeFederalPollingPlacesResourceSource: MakeSource[BasicTestIO, Exception, FederalPollingPlacesResource] = makeSourceFor[FederalPollingPlacesResource] {
    case FederalPollingPlacesResource(FederalElection.`2016`) => "GeneralPollingPlacesDownload-20499.csv"
  }

  private implicit val makeFormalSenatePreferencesResourceSource: MakeSource[BasicTestIO, Exception, FormalSenatePreferencesResource] = makeSourceFor[FormalSenatePreferencesResource] {
    case FormalSenatePreferencesResource(SenateElectionForState(SenateElection.`2016`, State.ACT)) => "formalPreferencesTest.csv"
  }

  private implicit val makeSenateFirstPreferencesResourceSource: MakeSource[BasicTestIO, Exception, SenateFirstPreferencesResource] = makeSourceFor[SenateFirstPreferencesResource] {
    case SenateFirstPreferencesResource(SenateElection.`2016`) => "firstPreferencesTest.csv"
  }

  private val underlying: FetchRawFederalElectionData[BasicTestIO] = FetchRawFederalElectionData[BasicTestIO](
    makeFederalPollingPlacesResourceSource,
    makeFormalSenatePreferencesResourceSource,
    makeDistributionOfPreferencesSource,
    makeSenateFirstPreferencesResourceSource,
  )

  override def senateDistributionOfPreferencesFor(
                                                   election: SenateElectionForState,
                                                 ): BasicTestIO[FetchRawSenateDistributionOfPreferences.Error, Stream[BasicTestIO[Throwable, +?], FetchRawSenateDistributionOfPreferences.Row]] =
    underlying.senateDistributionOfPreferencesFor(election)

  override def senateFirstPreferencesFor(
                                          election: SenateElection,
                                        ): BasicTestData.BasicTestIO[FetchRawSenateFirstPreferences.Error, Stream[BasicTestData.BasicTestIO[Throwable, +?], FetchRawSenateFirstPreferences.Row]] =
    underlying.senateFirstPreferencesFor(election)

  override def federalPollingPlacesForElection(
                                                election: FederalElection,
                                              ): BasicTestData.BasicTestIO[FetchRawFederalPollingPlaces.Error, Stream[BasicTestData.BasicTestIO[Throwable, +?], FetchRawFederalPollingPlaces.Row]] =
    underlying.federalPollingPlacesForElection(election)

  override def formalSenatePreferencesFor(
                                           election: SenateElectionForState,
                                         ): BasicTestData.BasicTestIO[FetchRawFormalSenatePreferences.Error, Stream[BasicTestData.BasicTestIO[Throwable, ?], FetchRawFormalSenatePreferences.Row]] =
    underlying.formalSenatePreferencesFor(election)
}

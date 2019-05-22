package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.data_sources.aec.federal.raw._
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.FetchRawFederalElectionData
import au.id.tmm.ausvotes.data_sources.aec.federal.resources.{FederalPollingPlacesResource, FormalSenatePreferencesResource, SenateDistributionOfPreferencesResource, SenateFirstPreferencesResource}
import au.id.tmm.ausvotes.data_sources.common.MakeSource
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.testIOIsABME
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.utilities.geo.australia.State
import fs2.Stream

import scala.io.Source

object MockFetchRawFederalElectionData extends FetchRawSenateFirstPreferences[BasicTestIO]
  with FetchRawSenateDistributionOfPreferences[BasicTestIO]
  with FetchRawFederalPollingPlaces[BasicTestIO]
  with FetchRawFormalSenatePreferences[BasicTestIO] {

  private implicit val makeDistributionOfPreferencesSource: MakeSource[BasicTestIO, Exception, SenateDistributionOfPreferencesResource] =
    new MakeSource[BasicTestIO, Exception, SenateDistributionOfPreferencesResource] {
      override def makeSourceFor(resource: SenateDistributionOfPreferencesResource): BasicTestIO[Exception, Source] =
        resource match {
          case SenateDistributionOfPreferencesResource(SenateElectionForState(SenateElection.`2016`, state @ State.WA)) =>
            MakeSource.fromGzipResource[BasicTestIO]().makeSourceFor(s"/au/id/tmm/ausvotes/core/fixtures/SenateStateDOPDownload-20499-${state.abbreviation}.csv.gz")

          case SenateDistributionOfPreferencesResource(SenateElectionForState(SenateElection.`2016`, state)) =>
            MakeSource.fromResource[BasicTestIO]().makeSourceFor(s"/au/id/tmm/ausvotes/core/fixtures/SenateStateDOPDownload-20499-${state.abbreviation}.csv")

          case r => MakeSource.never(implicitly[Sync[BasicTestIO]]).makeSourceFor(r)
        }
    }

  private def makeSourceFor[R](resolveResource: PartialFunction[R, String]): MakeSource[BasicTestIO, Exception, R] =
    MakeSource.fromResource[BasicTestIO]()
      .butFirst[R, Exception](resource => resolveResource.lift(resource) match {
      case Some(resourceLocation) => BME[BasicTestIO].pure(resourceLocation)
      case None => BME[BasicTestIO].leftPure(new RuntimeException(s"No test data for $resource"))
    })

  private implicit val makeFederalPollingPlacesResourceSource: MakeSource[BasicTestIO, Exception, FederalPollingPlacesResource] = makeSourceFor[FederalPollingPlacesResource] {
    case FederalPollingPlacesResource(FederalElection.`2016`) => "GeneralPollingPlacesDownload-20499.csv"
  }

  private implicit val makeFormalSenatePreferencesResourceSource: MakeSource[BasicTestIO, Exception, FormalSenatePreferencesResource] = makeSourceFor[FormalSenatePreferencesResource] {
    case FormalSenatePreferencesResource(SenateElectionForState(SenateElection.`2016`, State.ACT)) => "formalPreferencesTest.csv"
  }

  private implicit val makeSenateFirstPreferencesResourceSource: MakeSource[BasicTestIO, Exception, SenateFirstPreferencesResource] = makeSourceFor[SenateFirstPreferencesResource] {
    case SenateFirstPreferencesResource(SenateElection.`2016`) => "firstPreferencesTest.csv"
  }

  private val underlying: FetchRawFederalElectionData[BasicTestIO] = FetchRawFederalElectionData[BasicTestIO]

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

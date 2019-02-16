package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.{FetchDivisionsAndFederalPollingPlaces, FetchSenateBallots, FetchSenateCountData, FetchSenateGroupsAndCandidates}
import au.id.tmm.ausvotes.model.federal.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.instances.StateInstances
import au.id.tmm.utilities.geo.australia.State
import fs2.Stream
import scalaz.zio.IO

object AecResourcesRetrieval {

  type AecResourcesUse[A] = (SenateElectionForState, SenateGroupsAndCandidates, DivisionsAndPollingPlaces, SenateCountData, Stream[IO[Throwable, +?], SenateBallot]) => IO[Exception, A]

  def withElectionResources[A](election: SenateElection)(resourcesUse: AecResourcesUse[A])(
    implicit
    fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO],
    fetchDivisionsAndPollingPlaces: FetchDivisionsAndFederalPollingPlaces[IO],
    fetchCountData: FetchSenateCountData[IO],
    fetchBallots: FetchSenateBallots[IO],
  ): IO[Exception, Map[State, A]] = {
    val stateElectionsInOrder = election.allStateElections.toList.sortBy(_.state)(StateInstances.orderStatesByPopulation)

    for {
      valueResources <- retrieveValueResources(election)

      finalResultsPerState <- IO.parTraverse(stateElectionsInOrder) { election =>
        processResourcesForState(election, valueResources)(resourcesUse)
          .map(election.state -> _)
      }
    } yield finalResultsPerState.toMap
  }

  private def retrieveValueResources(election: SenateElection)(
    implicit
    fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO],
    fetchDivisionsAndPollingPlaces: FetchDivisionsAndFederalPollingPlaces[IO],
  ): IO[Exception, ValueResources] =
    (fetchGroupsAndCandidates.senateGroupsAndCandidatesFor(election) par fetchDivisionsAndPollingPlaces.divisionsAndFederalPollingPlacesFor(election.federalElection))
      .map { case (groupsAndCandidates, divisionsAndPollingPlaces) =>
        ValueResources(groupsAndCandidates, divisionsAndPollingPlaces)
      }

  private def processResourcesForState[A](
                                           election: SenateElectionForState,
                                           valueResources: ValueResources,
                                         )(
                                           resourceUse: AecResourcesUse[A],
                                         )(
                                           implicit
                                           fetchCountData: FetchSenateCountData[IO],
                                           fetchBallots: FetchSenateBallots[IO],
                                         ): IO[Exception, A] = {
    val relevantGroupsAndCandidates = valueResources.groupsAndCandidates.findFor(election)
    val relevantDivisionsAndPollingPlaces = valueResources.divisionsAndPollingPlaces.findFor(election.election.federalElection, election.state)

    for {
      countData <- fetchCountData.senateCountDataFor(election, valueResources.groupsAndCandidates)

      ballotsStream <- fetchBallots.senateBallotsFor(election, relevantGroupsAndCandidates, relevantDivisionsAndPollingPlaces)

      result <- resourceUse.apply(election, relevantGroupsAndCandidates, relevantDivisionsAndPollingPlaces, countData, ballotsStream)
    } yield result
  }

  private final case class ValueResources(
                                           groupsAndCandidates: SenateGroupsAndCandidates,
                                           divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                         )

}

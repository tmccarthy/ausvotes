package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import java.nio.file.Path

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.ausvotes.model.federal.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.instances.StateInstances
import au.id.tmm.ausvotes.shared.io.Closeables
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.IO

object AecResourcesRetrieval {

  type AecResourcesUse[A] = (SenateElectionForState, SenateGroupsAndCandidates, DivisionsAndPollingPlaces, SenateCountData, Iterator[SenateBallot]) => IO[Exception, A]

  def withElectionResources[A](dataStorePath: Path, election: SenateElection)(resourcesUse: AecResourcesUse[A]): IO[Exception, Map[State, A]] = {
    val stateElectionsInOrder = election.allStateElections.toList.sortBy(_.state)(StateInstances.orderStatesByPopulation)

    for {
      dataStore <- IO.syncException(ParsedDataStore(RawDataStore(AecResourceStore.at(dataStorePath))))

      valueResources <- retrieveValueResources(dataStore, election)

      finalResultsPerState <- IO.parTraverse(stateElectionsInOrder) { election =>
        processResourcesForState(election, dataStore, valueResources)(resourcesUse)
          .map(election.state -> _)
      }
    } yield finalResultsPerState.toMap
  }

  private def retrieveValueResources(dataStore: ParsedDataStore, election: SenateElection): IO[Exception, ValueResources] = {
    (
      IO.syncException(dataStore.groupsAndCandidatesFor(election)) par
        IO.syncException(dataStore.divisionsAndPollingPlacesFor(election.federalElection))
      )
      .map { case (groupsAndCandidates, divisionsAndPollingPlaces) =>
        ValueResources(groupsAndCandidates, divisionsAndPollingPlaces)
      }
  }

  private def processResourcesForState[A](
                                           election: SenateElectionForState,
                                           dataStore: ParsedDataStore,
                                           valueResources: ValueResources,
                                         )(
                                           resourceUse: AecResourcesUse[A],
                                         ): IO[Exception, A] = {
    val relevantGroupsAndCandidates = valueResources.groupsAndCandidates.findFor(election)
    val relevantDivisionsAndPollingPlaces = valueResources.divisionsAndPollingPlaces.findFor(election.election.federalElection, election.state)

    val openBallotsLogic = IO.syncException {
      dataStore.ballotsFor(election, relevantGroupsAndCandidates, relevantDivisionsAndPollingPlaces)
    }

    val computeCountDataLogic = IO.syncException {
      dataStore.countDataFor(election, valueResources.groupsAndCandidates)
    }

    computeCountDataLogic.flatMap { countData =>
      Closeables.bracketCloseable(openBallotsLogic) { ballots =>
        resourceUse(election, relevantGroupsAndCandidates, relevantDivisionsAndPollingPlaces, countData, ballots)
      }
    }
  }

  private final case class ValueResources(
                                           groupsAndCandidates: SenateGroupsAndCandidates,
                                           divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                         )

}

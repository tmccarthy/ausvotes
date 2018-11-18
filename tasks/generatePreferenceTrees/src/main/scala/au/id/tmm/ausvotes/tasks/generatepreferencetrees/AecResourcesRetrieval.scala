package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import java.nio.file.Path

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.model.parsing.Ballot
import au.id.tmm.ausvotes.core.model.{CountData, DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.ausvotes.shared.io.Closeables
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.IO

object AecResourcesRetrieval {

  type AecResourcesUse[A] = (SenateElection, State, GroupsAndCandidates, DivisionsAndPollingPlaces, CountData, Iterator[Ballot]) => IO[Exception, A]

  def withElectionResources[A](dataStorePath: Path, election: SenateElection)(resourcesUse: AecResourcesUse[A]): IO[Exception, Map[State, A]] = {
    val statesInSizeOrder = election.states.toList.sortBy(StateUtils.numBallots)

    for {
      dataStore <- IO.syncException(ParsedDataStore(RawDataStore(AecResourceStore.at(dataStorePath))))

      valueResources <- retrieveValueResources(dataStore, election)

      finalResultsPerState <- IO.parTraverse(statesInSizeOrder) {
        state => processResourcesForState(election, state, dataStore, valueResources)(resourcesUse)
          .map(state -> _)
      }
    } yield finalResultsPerState.toMap
  }

  private def retrieveValueResources(dataStore: ParsedDataStore, election: SenateElection): IO[Exception, ValueResources] = {
    (
      IO.syncException(dataStore.groupsAndCandidatesFor(election)) par
        IO.syncException(dataStore.divisionsAndPollingPlacesFor(election))
      )
      .map { case (groupsAndCandidates, divisionsAndPollingPlaces) =>
        ValueResources(groupsAndCandidates, divisionsAndPollingPlaces)
      }
  }

  private def processResourcesForState[A](
                                           election: SenateElection,
                                           state: State,
                                           dataStore: ParsedDataStore,
                                           valueResources: ValueResources,
                                         )(
                                           resourceUse: AecResourcesUse[A],
                                         ): IO[Exception, A] = {
    val relevantGroupsAndCandidates = valueResources.groupsAndCandidates.findFor(election, state)
    val relevantDivisionsAndPollingPlaces = valueResources.divisionsAndPollingPlaces.findFor(election, state)

    val openBallotsLogic = IO.syncException {
      dataStore.ballotsFor(election, relevantGroupsAndCandidates, relevantDivisionsAndPollingPlaces, state)
    }

    val computeCountDataLogic = IO.syncException {
      dataStore.countDataFor(election, valueResources.groupsAndCandidates, state)
    }

    computeCountDataLogic.flatMap { countData =>
      Closeables.bracketCloseable(openBallotsLogic) { ballots =>
        resourceUse(election, state, relevantGroupsAndCandidates, relevantDivisionsAndPollingPlaces, countData, ballots)
      }
    }
  }

  private final case class ValueResources(
                                           groupsAndCandidates: GroupsAndCandidates,
                                           divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                         )

}

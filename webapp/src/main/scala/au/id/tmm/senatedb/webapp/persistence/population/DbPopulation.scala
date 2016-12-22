package au.id.tmm.senatedb.webapp.persistence.population

import au.id.tmm.senatedb.core.engine.{ParsedDataStore, TallyEngine}
import au.id.tmm.senatedb.core.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.core.tallies.{CountFormalBallots, Tallier, Tallies}
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.{Inject, Singleton}

import scala.concurrent.Future

@Singleton
class DbPopulation @Inject() (entityPopulationChecker: EntityPopulationChecker,
                              tallyPopulationChecker: TallyPopulationChecker,
                              parsedDataStore: ParsedDataStore,
                              tallyEngine: TallyEngine,
                              entityClassPopulator: EntityClassPopulator,
                              tallyPopulator: TallyPopulator) {

  def populateAsNeeded(election: SenateElection): Future[Unit] = {

    for {
      entitiesToPopulate <- entityPopulationChecker.unpopulatedOf(election, DbPopulation.requiredEntities)
      talliesToPopulate <- tallyPopulationChecker.unpopulatedOf(election, DbPopulation.requiredTallies)
    } yield {
      populate(election, entitiesToPopulate, talliesToPopulate)
    }
  }

  private def populate(senateElection: SenateElection,
                       entitiesForPopulation: Set[PopulatableEntityClass],
                       talliersForPopulation: Set[Tallier]): Future[Unit] = {

    // TODO don't run at all if nothing to do

    // TODO parallelism
    val divisionsAndPollingPlaces = parsedDataStore.divisionsAndPollingPlacesFor(senateElection)
    val groupsAndCandidates = parsedDataStore.groupsAndCandidatesFor(senateElection)

    val talliersRequiredToPopulateEntities = entitiesForPopulation.flatMap(talliersRequiredFor)

    val allTalliersToRegister = talliersForPopulation ++ talliersRequiredToPopulateEntities

    // TODO don't always want to run for all states
    val eventualTallies = tallyEngine.talliesForStates(parsedDataStore, senateElection, State.ALL_STATES,
      divisionsAndPollingPlaces, groupsAndCandidates, allTalliersToRegister)

    eventualTallies.map { tallies =>
      val entityPopulationFutures = entitiesForPopulation.map(populateEntityClass(_, tallies, divisionsAndPollingPlaces, groupsAndCandidates))
      val tallyPopulationFutures = talliersForPopulation.map(tallier => populateFromTallier(tallier, tallies))

      // TODO find something more sophisticated than sequence
      for {
        _ <- Future.sequence(entityPopulationFutures)
        _ <- Future.sequence(tallyPopulationFutures)
      } yield ()
    }
  }

  private def talliersRequiredFor(populatableEntityClass: PopulatableEntityClass): Set[Tallier] = {
    populatableEntityClass match {
      case PopulatableEntityClass.OtherVoteCollectionPoints => Set(CountFormalBallots.ByVoteCollectionPoint)
      case _ => Set()
    }
  }

  private def populateEntityClass(entityClass: PopulatableEntityClass,
                                  tallies: Tallies,
                                  divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                  groupsAndCandidates: GroupsAndCandidates): Future[Unit] = {
    entityClass match {
      case PopulatableEntityClass.Divisions => entityClassPopulator.populateDivisions(divisionsAndPollingPlaces)
      case PopulatableEntityClass.PollingPlaces => entityClassPopulator.populatePollingPlaces(divisionsAndPollingPlaces)
      case PopulatableEntityClass.OtherVoteCollectionPoints => {
        val formalBallotsByVoteCollectionPoint = tallies.tallyBy(CountFormalBallots.ByVoteCollectionPoint)

        entityClassPopulator.populateOtherVoteCollectionPoints(formalBallotsByVoteCollectionPoint)
      }
    }
  }

  private def populateFromTallier(tallier: Tallier, tallies: Tallies): Future[Unit] = {
    tallier match {
      case CountFormalBallots.ByDivision => tallyPopulator.populateFormalBallotsByDivision(tallies.tallyBy(CountFormalBallots.ByDivision))
      case CountFormalBallots.ByVoteCollectionPoint => tallyPopulator.populateFormalBallotsByVoteCollectionPoint(tallies.tallyBy(CountFormalBallots.ByVoteCollectionPoint))
    }
  }
}

object DbPopulation {
  val requiredEntities: Set[PopulatableEntityClass] = Set(
    PopulatableEntityClass.Divisions,
    PopulatableEntityClass.PollingPlaces,
    PopulatableEntityClass.OtherVoteCollectionPoints
  )

  val requiredTallies: Set[Tallier] = Set(
    CountFormalBallots.ByDivision,
    CountFormalBallots.ByVoteCollectionPoint
  )
}

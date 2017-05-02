package au.id.tmm.senatedb.webapp.persistence.population

import au.id.tmm.senatedb.core.engine.{ParsedDataStore, TallyEngine}
import au.id.tmm.senatedb.core.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.core.tallies.{CountFormalBallots, Tallier, Tallies}
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.Inject
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

// TODO log what this is doing
class DbPopulator @Inject()(entityPopulationChecker: EntityPopulationChecker,
                            tallyPopulationChecker: TallyPopulationChecker,
                            parsedDataStore: ParsedDataStore,
                            tallyEngine: TallyEngine,
                            entityClassPopulator: EntityClassPopulator,
                            tallyPopulator: TallyPopulator) {

  def populateAsNeeded(election: SenateElection): Future[Unit] = {
    for {
      entitiesToPopulate <- entityPopulationChecker.unpopulatedOf(election, DbPopulator.requiredEntities)
      talliesToPopulate <- tallyPopulationChecker.unpopulatedOf(election, DbPopulator.requiredTallies)
      _ <- populate(election, entitiesToPopulate, talliesToPopulate)
    } yield {}
  }

  private def populate(election: SenateElection,
                       entitiesForPopulation: Set[PopulatableEntityClass],
                       talliersForPopulation: Set[Tallier]): Future[Unit] = {

    if (entitiesForPopulation.isEmpty && talliersForPopulation.isEmpty) {
      return Future.successful {}
    }

    // TODO parallelism
    val divisionsAndPollingPlaces = parsedDataStore.divisionsAndPollingPlacesFor(election)
    val groupsAndCandidates = parsedDataStore.groupsAndCandidatesFor(election)

    val talliersRequiredToPopulateEntities = entitiesForPopulation.flatMap(talliersRequiredFor)

    val allTalliersToRegister = talliersForPopulation ++ talliersRequiredToPopulateEntities

    val eventualTallies = {
      if (allTalliersToRegister.isEmpty) {
        Future.successful(Tallies())
      } else {
        // TODO don't always want to run for all states
        tallyEngine.runFor(parsedDataStore, election, State.ALL_STATES,
          divisionsAndPollingPlaces, groupsAndCandidates, allTalliersToRegister)
      }
    }

    for {
      tallies <- eventualTallies
      // TODO Do something more sophisticated than sequence
      _ <- Future.sequence(entitiesForPopulation.map(populateEntityClass(_, tallies, divisionsAndPollingPlaces, groupsAndCandidates)))
      _ <- Future.sequence(talliersForPopulation.map(tallier => populateFromTallier(tallier, tallies, election)))
    } yield {}
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

  private def populateFromTallier(tallier: Tallier, tallies: Tallies, election: SenateElection): Future[Unit] = {
    tallier match {
      case CountFormalBallots.ByDivision => tallyPopulator.populateFormalBallotsByDivision(election, tallies.tallyBy(CountFormalBallots.ByDivision))
      case CountFormalBallots.ByVoteCollectionPoint => tallyPopulator.populateFormalBallotsByVoteCollectionPoint(election, tallies.tallyBy(CountFormalBallots.ByVoteCollectionPoint))
    }
  }
}

object DbPopulator {
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

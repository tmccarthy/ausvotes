package au.id.tmm.senatedb.api.persistence.population

import au.id.tmm.senatedb.api.persistence.population.DbPopulator.talliesFormalBallotsByVoteCollectionPoint
import au.id.tmm.senatedb.core.engine.{ParsedDataStore, TallyEngine}
import au.id.tmm.senatedb.core.model.parsing.{Division, VoteCollectionPoint}
import au.id.tmm.senatedb.core.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.core.tallies._
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

// TODO log what this is doing
class DbPopulator @Inject()(entityPopulationChecker: EntityPopulationChecker,
                            tallyPopulationChecker: TallyPopulationChecker,
                            parsedDataStore: ParsedDataStore,
                            tallyEngine: TallyEngine,
                            entityClassPopulator: EntityClassPopulator,
                            tallyPopulator: TallyPopulator)
                           (implicit ec: ExecutionContext) {

  def populateAsNeeded(election: SenateElection): Future[Unit] = {
    for {
      entitiesToPopulate <- entityPopulationChecker.unpopulatedOf(election, DbPopulator.requiredEntities)
      talliesToPopulate <- tallyPopulationChecker.unpopulatedOf(election, DbPopulator.requiredTallies)
      _ <- populate(election, entitiesToPopulate, talliesToPopulate)
    } yield {}
  }

  def isPopulatedFor(election: SenateElection): Future[Boolean] = {
    for {
      entitiesToPopulate <- entityPopulationChecker.unpopulatedOf(election, DbPopulator.requiredEntities)
      talliesToPopulate <- tallyPopulationChecker.unpopulatedOf(election, DbPopulator.requiredTallies)
    } yield entitiesToPopulate.isEmpty && talliesToPopulate.isEmpty
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
        Future.successful(TallyBundle())
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
      case PopulatableEntityClass.OtherVoteCollectionPoints => Set(talliesFormalBallotsByVoteCollectionPoint)
      case _ => Set()
    }
  }

  private def populateEntityClass(entityClass: PopulatableEntityClass,
                                  tallies: TallyBundle,
                                  divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                                  groupsAndCandidates: GroupsAndCandidates): Future[Unit] = {
    entityClass match {
      case PopulatableEntityClass.Divisions => entityClassPopulator.populateDivisions(divisionsAndPollingPlaces)
      case PopulatableEntityClass.PollingPlaces => entityClassPopulator.populatePollingPlaces(divisionsAndPollingPlaces)
      case PopulatableEntityClass.OtherVoteCollectionPoints => {
        val formalBallotsByVoteCollectionPoint = tallies.tallyProducedBy(talliesFormalBallotsByVoteCollectionPoint)

        entityClassPopulator.populateOtherVoteCollectionPoints(formalBallotsByVoteCollectionPoint)
      }
    }
  }

  private def populateFromTallier(tallier: Tallier, tallies: TallyBundle, election: SenateElection): Future[Unit] = {
    ???
  }
}

object DbPopulator {
  val requiredEntities: Set[PopulatableEntityClass] = Set(
    PopulatableEntityClass.Divisions,
    PopulatableEntityClass.PollingPlaces,
    PopulatableEntityClass.OtherVoteCollectionPoints
  )

  val talliesFormalBallotsByDivision: Tallier1[Division] = TallierBuilder
    .counting(BallotCounter.FormalBallots)
    .groupedBy(BallotGrouping.Division)

  val talliesFormalBallotsByVoteCollectionPoint: Tallier1[VoteCollectionPoint] = TallierBuilder
    .counting(BallotCounter.FormalBallots)
    .groupedBy(BallotGrouping.VoteCollectionPoint)

  val requiredTallies: Set[Tallier] = Set(
    talliesFormalBallotsByDivision,
    talliesFormalBallotsByVoteCollectionPoint
  )
}

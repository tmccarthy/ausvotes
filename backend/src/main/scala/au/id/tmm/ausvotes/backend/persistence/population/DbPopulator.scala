package au.id.tmm.ausvotes.backend.persistence.population

import au.id.tmm.ausvotes.backend.persistence.ManagePersistence
import au.id.tmm.ausvotes.backend.persistence.daos.{DivisionDao, StatDao, VoteCollectionPointDao}
import au.id.tmm.ausvotes.backend.persistence.entities.stats.StatClass
import au.id.tmm.ausvotes.core.engine.{ParsedDataStore, TallyEngine}
import au.id.tmm.ausvotes.core.logging.LoggedEvent.FutureOps
import au.id.tmm.ausvotes.core.logging.Logger
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.VoteCollectionPoint.SpecialVoteCollectionPoint
import au.id.tmm.ausvotes.core.tallies.{BallotCounter, BallotGrouping, TallierBuilder}
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DbPopulator @Inject()(divisionDao: DivisionDao,
                            voteCollectionPointDao: VoteCollectionPointDao,
                            statDao: StatDao,
                            parsedDataStore: ParsedDataStore,
                            tallyEngine: TallyEngine,
                           )(implicit ec: ExecutionContext) {

  import DbPopulator._

  def populateAsRequired(election: SenateElection): Future[Unit] = {
    isPopulatedFor(election).flatMap { isPopulated =>
      if (isPopulated) {
        DbPopulator.logger.info("DB_POPULATION_NOT_NEEDED", "election" -> election)
        Future.successful(Unit)
      } else {
        populateFor(election)
          .logEvent("DB_POPULATION", "election" -> election)
      }
    }
  }

  def isPopulatedFor(election: SenateElection): Future[Boolean] = {

    val areDivisionsPopulated = divisionDao.hasAnyDivisionsFor(election)
    val arePollingPlacesPopulated = voteCollectionPointDao.hasAnyPollingPlacesFor(election)
    val areSpecialVcpsPopulated = voteCollectionPointDao.hasAnySpecialVoteCollectionPointsFor(election)

    val areAllRequiredStatsAreRequired = statDao.hasSomeStatsForEachOf(election, requiredStatClasses)

    Future.sequence(Vector(
      areDivisionsPopulated,
      arePollingPlacesPopulated,
      areSpecialVcpsPopulated,
      areAllRequiredStatsAreRequired,
    ))
      .map(_.reduce(_ && _))
  }

  def populateFor(election: SenateElection): Future[Unit] = {

    val talliers = requiredStatClasses.flatMap(_.requiredTalliers) + formalBallotsByVcpTallier

    val divisionsAndPollingPlaces = parsedDataStore.divisionsAndPollingPlacesFor(election)
    val groupsAndCandidates = parsedDataStore.groupsAndCandidatesFor(election)

    clearDbFor(election).flatMap { _ =>
      tallyEngine.runFor(
        parsedDataStore,
        election,
        State.ALL_STATES,
        divisionsAndPollingPlaces,
        groupsAndCandidates,
        talliers,
      )
    }.flatMap { tallyBundle =>
      val specialVoteCollectionPoints = tallyBundle.tallyProducedBy(formalBallotsByVcpTallier).asMap
        .keys
        .collect {
          case specialVcp: SpecialVoteCollectionPoint => specialVcp
        }

      val stats = requiredStatClasses
        .toVector
        .flatMap(_.statsFromTallyBundle(tallyBundle))

      for {
        _ <- divisionDao.write(divisionsAndPollingPlaces.divisions)
        _ <- voteCollectionPointDao.write(divisionsAndPollingPlaces.pollingPlaces)
        _ <- voteCollectionPointDao.write(specialVoteCollectionPoints)
        _ <- statDao.writeStats(election, stats)
      } yield {}
    }
  }

  private def clearDbFor(election: SenateElection): Future[Unit] = {
    Future {
      ManagePersistence.clearSchema()
      ManagePersistence.migrateSchema()
    }
      .logEvent("CLEAR_DB", "election" -> election)
  }
}

object DbPopulator {
  private implicit val logger: Logger = Logger()

  private val requiredStatClasses = StatClass.ALL

  private val formalBallotsByVcpTallier = TallierBuilder
    .counting(BallotCounter.FormalBallots)
    .groupedBy(BallotGrouping.VoteCollectionPoint)
}
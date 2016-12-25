package au.id.tmm.senatedb.webapp.persistence.population

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.webapp.persistence.daos.GeneralDao
import au.id.tmm.utilities.concurrent.FutureCollectionUtils.FutureSetOps
import com.google.inject.{Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

@Singleton
class EntityPopulationChecker @Inject() (generalDao: GeneralDao) {

  def unpopulatedOf(election: SenateElection, entityClasses: Set[PopulatableEntityClass]): Future[Set[PopulatableEntityClass]] = {
    entityClasses.filterEventually(isPopulatedAtElection(election, _))
  }

  private def isPopulatedAtElection(election: SenateElection, entityClass: PopulatableEntityClass): Future[Boolean] = {
    entityClass match {
      case PopulatableEntityClass.Divisions => generalDao.divisionDao.hasAnyDivisionsFor(election)
      case PopulatableEntityClass.PollingPlaces => generalDao.voteCollectionPointDao.hasAnyPollingPlacesFor(election)
      case PopulatableEntityClass.OtherVoteCollectionPoints =>
        generalDao.voteCollectionPointDao.hasAnyNonPollingPlaceVoteCollectionPointsFor(election)
    }
  }

}

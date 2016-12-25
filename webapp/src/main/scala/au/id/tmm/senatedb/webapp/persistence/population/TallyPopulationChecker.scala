package au.id.tmm.senatedb.webapp.persistence.population

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.tallies.{CountFormalBallots, Tallier}
import au.id.tmm.senatedb.webapp.persistence.daos.GeneralTallyDao
import au.id.tmm.utilities.concurrent.FutureCollectionUtils.FutureSetOps
import com.google.inject.{Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

@Singleton
class TallyPopulationChecker @Inject() (generalTallyDao: GeneralTallyDao) {
  def unpopulatedOf(election: SenateElection, talliers: Set[Tallier]): Future[Set[Tallier]] = {
    talliers.filterEventually(resultIsPopulated(election, _))
  }

  private def resultIsPopulated(election: SenateElection, tallier: Tallier): Future[Boolean] = {
    tallier match {
      case CountFormalBallots.ByDivision => generalTallyDao.totalFormalBallotsDao.hasTallyForAnyDivision
      case CountFormalBallots.ByVoteCollectionPoint => generalTallyDao.totalFormalBallotsDao.hasTallyForAnyVoteCollectionPoint
    }
  }
}

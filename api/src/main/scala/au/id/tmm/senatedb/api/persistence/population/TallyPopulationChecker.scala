package au.id.tmm.senatedb.api.persistence.population

import au.id.tmm.senatedb.api.persistence.daos.GeneralTallyDao
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.tallies.Tallier
import au.id.tmm.utilities.concurrent.FutureCollectionUtils.FutureSetOps
import com.google.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TallyPopulationChecker @Inject() (generalTallyDao: GeneralTallyDao)
                                       (implicit ec: ExecutionContext) {

  private val totalFormalBallotsDao = generalTallyDao.totalFormalBallotsDao

  def unpopulatedOf(election: SenateElection, talliers: Set[Tallier]): Future[Set[Tallier]] = {
    talliers.filterEventually(resultIsPopulated(election, _).map(!_))
  }

  private def resultIsPopulated(election: SenateElection, tallier: Tallier): Future[Boolean] = {
    tallier match {
      case x if x == DbPopulator.talliesFormalBallotsByDivision =>
        totalFormalBallotsDao.hasTallyForAnyDivisionAt(election)

      case x if x == DbPopulator.talliesFormalBallotsByVoteCollectionPoint =>
        totalFormalBallotsDao.hasTallyForAnyVoteCollectionPointAt(election)
    }
  }
}

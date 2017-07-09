package au.id.tmm.senatedb.api.persistence.population

import au.id.tmm.senatedb.core.model.DivisionsAndPollingPlaces
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint
import au.id.tmm.senatedb.core.tallies.Tally
import au.id.tmm.senatedb.api.persistence.daos.{DivisionDao, VoteCollectionPointDao}
import com.google.inject.{Inject, Singleton}

import scala.concurrent.Future

@Singleton
class EntityClassPopulator @Inject() (divisionDao: DivisionDao, voteCollectionPointDao: VoteCollectionPointDao) {

  def populateDivisions(divisionsAndPollingPlaces: DivisionsAndPollingPlaces): Future[Unit] =
    divisionDao.write(divisionsAndPollingPlaces.divisions)

  def populatePollingPlaces(divisionsAndPollingPlaces: DivisionsAndPollingPlaces): Future[Unit] =
    voteCollectionPointDao.write(divisionsAndPollingPlaces.pollingPlaces)

  def populateOtherVoteCollectionPoints(formalBallotsByVoteCollectionPoint: Tally[VoteCollectionPoint]): Future[Unit] = {
    val voteCollectionPoints = formalBallotsByVoteCollectionPoint.values.keys

    voteCollectionPointDao.write(voteCollectionPoints)
  }
}

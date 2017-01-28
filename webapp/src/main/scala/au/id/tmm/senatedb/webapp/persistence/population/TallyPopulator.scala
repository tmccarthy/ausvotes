package au.id.tmm.senatedb.webapp.persistence.population

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Division, VoteCollectionPoint}
import au.id.tmm.senatedb.core.tallies.Tally
import au.id.tmm.senatedb.webapp.persistence.daos.GeneralTallyDao
import com.google.inject.{Inject, Singleton}

import scala.concurrent.Future

@Singleton
class TallyPopulator @Inject() (tallyDao: GeneralTallyDao) {

  def populateFormalBallotsByDivision(senateElection: SenateElection, tally: Tally[Division]): Future[Unit] =
    tallyDao.totalFormalBallotsDao.writePerDivision(tally)

  def populateFormalBallotsByVoteCollectionPoint(election: SenateElection,
                                                 tally: Tally[VoteCollectionPoint]): Future[Unit] =
    tallyDao.totalFormalBallotsDao.writePerVoteCollectionPoint(election, tally)

}

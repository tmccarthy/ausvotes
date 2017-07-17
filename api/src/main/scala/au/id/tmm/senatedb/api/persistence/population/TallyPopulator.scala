package au.id.tmm.senatedb.api.persistence.population

import au.id.tmm.senatedb.api.persistence.daos.GeneralTallyDao
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Division, VoteCollectionPoint}
import au.id.tmm.senatedb.core.tallies.Tally1
import com.google.inject.{Inject, Singleton}

import scala.concurrent.Future

@Singleton
class TallyPopulator @Inject() (tallyDao: GeneralTallyDao) {

  def populateFormalBallotsByDivision(senateElection: SenateElection, tally: Tally1[Division]): Future[Unit] =
    tallyDao.totalFormalBallotsDao.writePerDivision(tally)

  def populateFormalBallotsByVoteCollectionPoint(election: SenateElection,
                                                 tally: Tally1[VoteCollectionPoint]): Future[Unit] =
    tallyDao.totalFormalBallotsDao.writePerVoteCollectionPoint(election, tally)

}

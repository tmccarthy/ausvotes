package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Division, VoteCollectionPoint}
import au.id.tmm.senatedb.core.tallies.Tally
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits._
import scalikejdbc._

import scala.concurrent.Future

@ImplementedBy(classOf[ConcreteTotalFormalBallotsDao])
trait TotalFormalBallotsDao {
  def hasTallyForAnyDivisionAt(election: SenateElection): Future[Boolean]

  def hasTallyForAnyVoteCollectionPointAt(election: SenateElection): Future[Boolean]

  def writePerDivision(tally: Tally[Division]): Future[Unit]

  def writePerVoteCollectionPoint(tally: Tally[VoteCollectionPoint]): Future[Unit]
}

@Singleton
class ConcreteTotalFormalBallotsDao @Inject() (electionDao: ElectionDao, divisionDao: DivisionDao)
    extends TotalFormalBallotsDao {

  override def hasTallyForAnyDivisionAt(election: SenateElection): Future[Boolean] = Future {
    val electionId = electionDao.idOfBlocking(election).get

    DB.readOnly { implicit session =>
      val statement = sql"""SELECT *
           |FROM division
           |  LEFT JOIN division_stats
           |WHERE division.election = $electionId
           |    AND division_stats.total_formal_ballot_count_id IS NOT NULL""".stripMargin

      statement.map(_ => Unit)
        .first()
        .apply()
        .isDefined
    }
  }

  override def hasTallyForAnyVoteCollectionPointAt(election: SenateElection): Future[Boolean] = Future {
    val electionId = electionDao.idOfBlocking(election).get

    DB.readOnly { implicit session =>
      val statement = sql"""SELECT *
                         |FROM vote_collection_point
                         |  LEFT JOIN vote_collection_point_stats
                         |WHERE vote_collection_point.election = $electionId
                         |    AND vote_collection_point_stats.total_formal_ballot_count_id IS NOT NULL""".stripMargin

      statement.map(_ => Unit)
        .first()
        .apply()
        .isDefined
    }
  }

  override def writePerDivision(tally: Tally[Division]): Future[Unit] = ???

  override def writePerVoteCollectionPoint(tally: Tally[VoteCollectionPoint]): Future[Unit] = ???
}

private[daos] class TotalFormalBallotsConversions extends RowConversions {

}
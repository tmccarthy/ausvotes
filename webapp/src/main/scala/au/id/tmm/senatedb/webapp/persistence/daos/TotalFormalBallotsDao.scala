package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.parsing.{Division, VoteCollectionPoint}
import au.id.tmm.senatedb.core.tallies.Tally
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc._

import scala.concurrent.Future

@ImplementedBy(classOf[ConcreteTotalFormalBallotsDao])
trait TotalFormalBallotsDao {
  def hasTallyForAnyDivision: Future[Boolean]

  def hasTallyForAnyVoteCollectionPoint: Future[Boolean]

  def writePerDivision(tally: Tally[Division]): Future[Unit]

  def writePerVoteCollectionPoint(tally: Tally[VoteCollectionPoint]): Future[Unit]
}

@Singleton
class ConcreteTotalFormalBallotsDao @Inject() () extends TotalFormalBallotsDao {
  override def hasTallyForAnyDivision: Future[Boolean] = {
    DB.readOnly { implicit session =>
      ???
    }
  }

  override def hasTallyForAnyVoteCollectionPoint: Future[Boolean] = ???

  override def writePerDivision(tally: Tally[Division]): Future[Unit] = ???

  override def writePerVoteCollectionPoint(tally: Tally[VoteCollectionPoint]): Future[Unit] = ???
}

private[daos] class TotalFormalBallotsConversions extends RowConversions {

}
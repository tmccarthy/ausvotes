package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.db.Database

import scala.concurrent.Future

@Singleton
@ImplementedBy(classOf[ConcreteVoteCollectionPointDao])
trait VoteCollectionPointDao {
  def write(voteCollectionPoints: TraversableOnce[VoteCollectionPoint]): Future[Unit]

  def allAtElection(election: SenateElection): Future[Set[VoteCollectionPoint]]

  def hasAnyNonPollingPlaceVoteCollectionPointsFor(election: SenateElection): Future[Boolean]

  def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean]
}

class ConcreteVoteCollectionPointDao @Inject() (db: Database) extends VoteCollectionPointDao {
  override def write(voteCollectionPoints: TraversableOnce[VoteCollectionPoint]): Future[Unit] = ???

  override def allAtElection(election: SenateElection): Future[Set[VoteCollectionPoint]] = ???

  override def hasAnyNonPollingPlaceVoteCollectionPointsFor(election: SenateElection): Future[Boolean] = ???

  override def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean] = ???
}
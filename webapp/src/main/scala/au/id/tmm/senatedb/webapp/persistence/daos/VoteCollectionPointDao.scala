package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc.{ConnectionPoolContext, WrappedResultSet}

import scala.concurrent.Future

@ImplementedBy(classOf[ConcreteVoteCollectionPointDao])
trait VoteCollectionPointDao {
  def write(voteCollectionPoints: TraversableOnce[VoteCollectionPoint]): Future[Unit]

  def allAtElection(election: SenateElection): Future[Set[VoteCollectionPoint]]

  def hasAnyNonPollingPlaceVoteCollectionPointsFor(election: SenateElection): Future[Boolean]

  def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean]
}

@Singleton
class ConcreteVoteCollectionPointDao @Inject() (connectionPool: ConnectionPoolContext) extends VoteCollectionPointDao {

//  CREATE TABLE vote_collection_point (
//    id SERIAL PRIMARY KEY,
//
//    election VARCHAR(5) REFERENCES senate_election(id),
//    aec_id INTEGER,
//    state VARCHAR(3) REFERENCES state(abbreviation),
//    division_id INTEGER REFERENCES division(id),
//
//  type VOTE_COLLECTION_POINT_TYPE,
//  name VARCHAR,
//
//  -- Only if this is a polling place
//
//  polling_place_type POLLING_PLACE_TYPE,
//
//  multiple_locations BOOLEAN,
//
//  premises_name VARCHAR,
//  address INTEGER REFERENCES address(id),
//
//  latitude DOUBLE PRECISION,
//  longtitude DOUBLE PRECISION
//  );


  override def write(voteCollectionPoints: TraversableOnce[VoteCollectionPoint]): Future[Unit] = ???

  override def allAtElection(election: SenateElection): Future[Set[VoteCollectionPoint]] = ???

  override def hasAnyNonPollingPlaceVoteCollectionPointsFor(election: SenateElection): Future[Boolean] = ???

  override def hasAnyPollingPlacesFor(election: SenateElection): Future[Boolean] = ???

  private def voteCollectionPointToRow(voteCollectionPoint: VoteCollectionPoint): Seq[(Symbol, Any)] = ???

  private def voteCollectionPointFromRow(wrappedResultSet: WrappedResultSet): VoteCollectionPoint = ???
}
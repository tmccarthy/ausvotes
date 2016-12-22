package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.db.Database

import scala.concurrent.Future

@Singleton
@ImplementedBy(classOf[ConcreteDivisionDao])
trait DivisionDao {
  def write(divisions: TraversableOnce[Division]): Future[Unit]

  def allAtElection(election: SenateElection): Future[Set[Division]]

  def fromAecId(aecId: String): Future[Division]

  def hasAnyDivisionsFor(election: SenateElection): Future[Boolean]
}

class ConcreteDivisionDao @Inject() (db: Database) extends DivisionDao {
  override def write(divisions: TraversableOnce[Division]): Future[Unit] = ???

  override def allAtElection(election: SenateElection): Future[Set[Division]] = ???

  override def fromAecId(aecId: String): Future[Division] = ???

  override def hasAnyDivisionsFor(election: SenateElection): Future[Boolean] = ???
}
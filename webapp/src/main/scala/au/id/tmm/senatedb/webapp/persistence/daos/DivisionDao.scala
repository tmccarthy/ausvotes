package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import com.google.inject.{ImplementedBy, Inject}
import play.api.db.Database

import scala.concurrent.Future

@ImplementedBy(classOf[ConcreteDivisionDao])
trait DivisionDao {
  def write(division: Division): Future[Unit]

  def allAtElection(election: SenateElection): Future[Division]

  def fromAecId(aecId: String): Future[Division]
}

class ConcreteDivisionDao @Inject() (db: Database) extends DivisionDao {
  override def write(division: Division): Future[Unit] = ???

  override def allAtElection(election: SenateElection): Future[Division] = ???

  override def fromAecId(aecId: String): Future[Division] = ???
}
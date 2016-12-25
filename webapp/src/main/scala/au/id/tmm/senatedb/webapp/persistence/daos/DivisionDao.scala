package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits._
import scalikejdbc.{DB, _}

import scala.concurrent.Future

@ImplementedBy(classOf[ConcreteDivisionDao])
trait DivisionDao {
  def write(divisions: TraversableOnce[Division]): Future[Unit]

  def allAtElection(election: SenateElection): Future[Set[Division]]

  def hasAnyDivisionsFor(election: SenateElection): Future[Boolean]

  def fromName(divisionName: String): Future[Option[Division]]
}

@Singleton
class ConcreteDivisionDao @Inject() (electionDao: ElectionDao) extends DivisionDao {

  override def write(divisions: TraversableOnce[Division]): Future[Unit] = Future {
    DB.localTx { implicit session =>

      val rowsToInsert = divisions.map(divisionToRow).toSeq

      sql"INSERT INTO division(election, aec_id, state, name) VALUES ({election}, {aec_id}, {state}, {name})"
        .batchByName(rowsToInsert : _*)
        .apply()
    }
  }

  override def allAtElection(election: SenateElection): Future[Set[Division]] = Future {
    DB.readOnly { implicit session =>
      val electionId = election.aecID

      // TODO needs native toSet
      sql"SELECT * FROM division WHERE election = ${electionId}"
        .map(divisionFromRow)
        .list()
        .apply()
        .toSet
    }
  }

  override def hasAnyDivisionsFor(election: SenateElection): Future[Boolean] = Future {
    DB.readOnly { implicit session =>
      val electionId = election.aecID

      sql"SELECT * FROM division WHERE election = ${electionId} LIMIT 1"
        .first()
        .apply()
        .isDefined
    }
  }

  override def fromName(divisionName: String): Future[Option[Division]] = Future {
    DB.readOnly { implicit session =>
      val divisionNameLowerCase = divisionName.toLowerCase

      sql"SELECT * FROM division WHERE name = ${divisionNameLowerCase} LIMIT 1"
        .map(divisionFromRow)
        .first
        .apply()
    }
  }

  private def divisionToRow(division: Division): Seq[(Symbol, Any)] = {
    Seq(
      Symbol("election") -> division.election.aecID,
      Symbol("aec_id") -> division.aecId,
      Symbol("state") -> division.state.abbreviation,
      Symbol("name") -> division.name
    )
  }

  private def divisionFromRow(wrappedResultSet: WrappedResultSet): Division = {
    val electionId = wrappedResultSet.string("election")
    val election = electionDao.electionWithIdBlocking(electionId).get

    val stateAbbreviation = wrappedResultSet.string("state")
    val state = State.fromAbbreviation(stateAbbreviation).get

    val name = wrappedResultSet.string("name")

    val aecId = wrappedResultSet.int("aec_id")

    Division(election, state, name, aecId)
  }
}
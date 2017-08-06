package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.hashing.Pairing
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc.{DB, _}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ConcreteDivisionDao])
trait DivisionDao {
  def write(divisions: TraversableOnce[Division]): Future[Unit]

  def allAtElection(election: SenateElection): Future[Set[Division]]

  def hasAnyDivisionsFor(election: SenateElection): Future[Boolean]

  def idOf(division: Division): Long
}

@Singleton
class ConcreteDivisionDao @Inject() (dbStructureCache: DbStructureCache)
                                    (implicit ec: ExecutionContext) extends DivisionDao {

  override def write(divisions: TraversableOnce[Division]): Future[Unit] = Future {
    val rowsToInsert = divisions.map(DivisionRowConversions.toRow()).toSeq

    DB.localTx { implicit session =>
      sql"INSERT INTO division(id, election, aec_id, state, name) VALUES ({id}, {election}, {aec_id}, {state}, {name})"
        .batchByName(rowsToInsert: _*)
        .apply()
    }
  }

  override def allAtElection(election: SenateElection): Future[Set[Division]] = Future {
    DB.readOnly { implicit session =>
      val electionId = election.aecID

      // TODO needs native toSet
      sql"SELECT * FROM division WHERE election = ${electionId}"
        .map(DivisionRowConversions.fromRow())
        .list()
        .apply()
        .toSet
    }
  }

  override def hasAnyDivisionsFor(election: SenateElection): Future[Boolean] = Future {
    DB.readOnly { implicit session =>
      val electionId = ElectionDao.idOf(election)

      sql"SELECT * FROM division WHERE election = ${electionId} LIMIT 1"
        .first()
        .map(DivisionRowConversions.fromRow())
        .first()
        .apply()
        .isDefined
    }
  }

  override def idOf(division: Division): Long = DivisionRowConversions.idOf(division)
}

private[daos] object DivisionRowConversions extends RowConversions {

  def fromRow(alias: String = "")(row: WrappedResultSet): Division = {
    val c = aliasedColumnName(alias)(_)

    val electionId = row.string(c("election"))
    val election = ElectionDao.electionWithId(electionId).get

    val stateAbbreviation = row.string(c("state"))
    val state = State.fromAbbreviation(stateAbbreviation).get

    val name = row.string(c("name"))

    val aecId = row.int(c("aec_id"))

    Division(election, state, name, aecId)
  }

  def toRow()(division: Division): Seq[(Symbol, Any)] = {
    Seq(
      Symbol("id") -> idOf(division),
      Symbol("election") -> ElectionDao.idOf(division.election.aecID),
      Symbol("aec_id") -> division.aecId,
      Symbol("state") -> division.state.abbreviation,
      Symbol("name") -> division.name
    )
  }

  def idOf(division: Division): Long = Pairing.Szudzik.pair(division.election.aecID, division.aecId)
}
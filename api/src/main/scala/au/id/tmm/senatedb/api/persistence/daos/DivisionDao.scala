package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.persistence.daos.rowentities.DivisionRow
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
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
class ConcreteDivisionDao @Inject() ()
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
    DB.localTx { implicit session =>
      val electionId = ElectionDao.idOf(election)

      val d = DivisionRow.syntax

      // TODO needs native toSet
      withSQL(select.from(DivisionRow as d).where.eq(d.election, electionId))
        .map(DivisionRow(d))
        .list()
        .apply()
        .toStream
        .map(_.asDivision)
        .toSet
    }
  }

  override def hasAnyDivisionsFor(election: SenateElection): Future[Boolean] = Future {
    DB.localTx { implicit session =>
      val electionId = ElectionDao.idOf(election)

      val d = DivisionRow.syntax

      withSQL(select.from(DivisionRow as d).where.eq(d.election, electionId).limit(1))
        .map(DivisionRow(d))
        .first()
        .apply()
        .isDefined
    }
  }

  override def idOf(division: Division): Long = DivisionRowConversions.idOf(division)
}

private[daos] object DivisionRowConversions extends RowConversions {

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
package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.persistence.daos.enumconverters.{ElectionEnumConverter, StateEnumConverter}
import au.id.tmm.senatedb.api.persistence.daos.insertionhelpers.DivisionInsertableHelper
import au.id.tmm.senatedb.api.persistence.daos.rowentities.DivisionRow
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scalikejdbc.{DB, _}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ConcreteDivisionDao])
trait DivisionDao {
  def write(divisions: TraversableOnce[Division]): Future[Unit]

  def allAtElection(election: SenateElection): Future[Set[Division]]

  def hasAnyDivisionsFor(election: SenateElection): Future[Boolean]

  def idOf(division: Division): Long

  def find(election: SenateElection, state: State, divisionName: String): Future[Option[Division]]
}

@Singleton
class ConcreteDivisionDao @Inject() ()
                                    (implicit ec: ExecutionContext) extends DivisionDao {

  override def write(divisions: TraversableOnce[Division]): Future[Unit] = Future {
    val rowsToInsert = divisions.map(DivisionInsertableHelper.toInsertable).toSeq

    DB.localTx { implicit session =>
      sql"INSERT INTO division(id, election, aec_id, state, name) VALUES ({id}, {election}, {aec_id}, {state}, {name})"
        .batchByName(rowsToInsert: _*)
        .apply()
    }
  }

  override def allAtElection(election: SenateElection): Future[Set[Division]] = Future {
    DB.localTx { implicit session =>
      val d = DivisionRow.syntax

      // TODO needs native toSet
      withSQL(select.from(DivisionRow as d).where.eq(d.election, ElectionEnumConverter(election)))
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
      val d = DivisionRow.syntax

      withSQL(select.from(DivisionRow as d).where.eq(d.election, ElectionEnumConverter(election)).limit(1))
        .map(DivisionRow(d))
        .first()
        .apply()
        .isDefined
    }
  }

  override def idOf(division: Division): Long = DivisionInsertableHelper.idOf(division)

  override def find(election: SenateElection, state: State, divisionName: String): Future[Option[Division]] = Future {
    DB.localTx { implicit session =>

      val d = DivisionRow.syntax

      withSQL(
        select
          .from(DivisionRow as d)
          .where
          .eq(d.election, ElectionEnumConverter(election))
            .and
          .eq(d.state, StateEnumConverter(state))
            .and
          .eq(sqls"LOWER(${d.name})", divisionName.toLowerCase)  // TODO LOWER should have its own SqlSyntax
          .limit(1)
      )
        .map(DivisionRow(d))
        .first()
        .apply()
        .map(_.asDivision)
    }
  }
}

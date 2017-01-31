package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.senatedb.webapp.persistence.entities.DivisionStats
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.hashing.Pairing
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits._
import scalikejdbc.{DB, _}

import scala.concurrent.Future

@ImplementedBy(classOf[ConcreteDivisionDao])
trait DivisionDao {
  def write(divisions: TraversableOnce[Division]): Future[Unit]

  def allAtElection(election: SenateElection): Future[Set[Division]]

  def hasAnyDivisionsFor(election: SenateElection): Future[Boolean]

  def idOf(division: Division): Long

  def findWithStats(electionId: String,
                    stateAbbreviation: String,
                    divisionName: String): Future[Option[(Division, DivisionStats)]]
}

@Singleton
class ConcreteDivisionDao @Inject() (electionDao: ElectionDao, dbStructureCache: DbStructureCache) extends DivisionDao {

  override def write(divisions: TraversableOnce[Division]): Future[Unit] = Future {
    val rowsToInsert = divisions.map(DivisionRowConversions.toRow(electionDao)).toSeq

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
        .map(DivisionRowConversions.fromRow(electionDao))
        .list()
        .apply()
        .toSet
    }
  }

  override def hasAnyDivisionsFor(election: SenateElection): Future[Boolean] = Future {
    DB.readOnly { implicit session =>
      val electionId = electionDao.idOf(election)

      sql"SELECT * FROM division WHERE election = ${electionId} LIMIT 1"
        .first()
        .map(DivisionRowConversions.fromRow(electionDao))
        .first()
        .apply()
        .isDefined
    }
  }

  override def findWithStats(electionId: String,
                             stateAbbreviation: String,
                             divisionName: String
                            ): Future[Option[(Division, DivisionStats)]] = Future {
    DB.readOnly { implicit session =>
      val * = dbStructureCache.columnListFor("division", "total_formal_ballot_count")

      sql"""SELECT
           |  ${*}
           |FROM division
           |  INNER JOIN division_stats ON division.id = division_stats.division
           |  INNER JOIN total_formal_ballot_count ON division_stats.total_formal_ballot_count_id = total_formal_ballot_count.id
           |WHERE
           |  division.election = $electionId AND
           |  division.state = ${stateAbbreviation.toUpperCase} AND
           |  LOWER(division.name) = ${divisionName.toLowerCase}
           |LIMIT 1
           |""".stripMargin
        .map { row =>
          val division = DivisionRowConversions.fromRow(electionDao, "division")(row)
          val totalFormalBallotsTally = TotalFormalBallotsRowConversions.fromRow(_ => division, alias="total_formal_ballot_count")(row)

          val divisionStats = DivisionStats(totalFormalBallotsTally)

          division -> divisionStats
        }
        .first()
        .apply()
    }
  }

  override def idOf(division: Division): Long = DivisionRowConversions.idOf(division)
}

private[daos] object DivisionRowConversions extends RowConversions {

  def fromRow(electionDao: ElectionDao, alias: String = "")(row: WrappedResultSet): Division = {
    val c = aliasedColumnName(alias)(_)

    val electionId = row.string(c("election"))
    val election = electionDao.electionWithId(electionId).get

    val stateAbbreviation = row.string(c("state"))
    val state = State.fromAbbreviation(stateAbbreviation).get

    val name = row.string(c("name"))

    val aecId = row.int(c("aec_id"))

    Division(election, state, name, aecId)
  }

  def toRow(electionDao: ElectionDao)(division: Division): Seq[(Symbol, Any)] = {
    Seq(
      Symbol("id") -> idOf(division),
      Symbol("election") -> electionDao.idOf(division.election.aecID),
      Symbol("aec_id") -> division.aecId,
      Symbol("state") -> division.state.abbreviation,
      Symbol("name") -> division.name
    )
  }

  def idOf(division: Division): Long = Pairing.Szudzik.pair(division.election.aecID, division.aecId)
}
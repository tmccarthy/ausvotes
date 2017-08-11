package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.persistence.entities.stats.Stat
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Division, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.{ImplementedBy, Inject}
import scalikejdbc.{DB, WrappedResultSet, _}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ConcreteStatDao])
trait StatDao {

  def statsFor(election: SenateElection): Future[Set[Stat[SenateElection]]]

  def statsFor(state: State, election: SenateElection): Future[Set[Stat[State]]]

  def statsFor(division: Division): Future[Set[Stat[Division]]]

  def statsFor(voteCollectionPoint: VoteCollectionPoint): Future[Set[Stat[VoteCollectionPoint]]]

  def writeStats(stats: Iterable[Stat[Any]]): Future[Unit]

}

class ConcreteStatDao @Inject() (dbStructureCache: DbStructureCache,
                                )(implicit ec: ExecutionContext) extends StatDao {
  private def * = dbStructureCache.aliasedColumnNamesFor("stat", "rank", "division")

  override def statsFor(election: SenateElection): Future[Set[Stat[SenateElection]]] = Future {
    val electionId = ElectionDao.idOf(election)

    DB.readOnly { implicit session =>
      val statement =
        sql"""
             |SELECT
             |${*}
             |FROM stat
             |LEFT JOIN rank
             |  ON stat.id = rank.stat
             |WHERE election = $electionId
             |""".stripMargin

      val rows = statement
        .fetchSize(1000)
        .map(r => r)
        .traversable()
        .apply()

      val stats = StatRowConversions.fromRows()(rows)
        .toSet
        .asInstanceOf[Set[Stat[SenateElection]]]

      stats
    }
  }

  override def statsFor(state: State, election: SenateElection): Future[Set[Stat[State]]] = ???

  override def statsFor(division: Division): Future[Set[Stat[Division]]] = ???

  override def statsFor(voteCollectionPoint: VoteCollectionPoint): Future[Set[Stat[VoteCollectionPoint]]] = ???

  override def writeStats(stats: Iterable[Stat[Any]]): Future[Unit] = ???
}

private [daos] object StatRowConversions extends RowConversions {

  def fromRows(statTableAlias: String = "stat", rankTableAlias: String = "rank")
              (rows: Traversable[WrappedResultSet]): Traversable[Stat[_]] = ???

  def fromRow(alias: String)(row: WrappedResultSet): Stat[_] = ???

  def toStatRow[A]()(stat: Stat[A]): Map[Symbol, Any] = ???

  def toRankRows[A]()(stat: Stat[A]): Vector[Map[Symbol, Any]] = ???

}
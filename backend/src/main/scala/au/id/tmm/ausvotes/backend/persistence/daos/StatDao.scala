package au.id.tmm.ausvotes.backend.persistence.daos

import au.id.tmm.ausvotes.backend.persistence.daos.enumconverters.{ElectionEnumConverter, StatClassEnumConverter, StateEnumConverter}
import au.id.tmm.ausvotes.backend.persistence.daos.insertionhelpers.InsertableSupport.Insertable
import au.id.tmm.ausvotes.backend.persistence.daos.insertionhelpers._
import au.id.tmm.ausvotes.backend.persistence.daos.rowentities._
import au.id.tmm.ausvotes.backend.persistence.entities.stats.{Stat, StatClass}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.ausvotes.core.model.parsing.VoteCollectionPoint.SpecialVoteCollectionPoint
import au.id.tmm.ausvotes.core.model.parsing.{Division, PollingPlace, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.name.Named
import com.google.inject.{ImplementedBy, Inject}
import scalikejdbc.{DB, _}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ConcreteStatDao])
trait StatDao {

  def statsFor(election: SenateElection): Future[Set[Stat[SenateElection]]]

  def statsFor(election: SenateElection, state: State): Future[Set[Stat[State]]]

  def statsFor(division: Division): Future[Set[Stat[Division]]]

  def statsFor(voteCollectionPoint: VoteCollectionPoint): Future[Set[Stat[VoteCollectionPoint]]]

  def writeStats(election: SenateElection, stats: Iterable[Stat[Any]]): Future[Unit]

  def hasSomeStatsForEachOf(election: SenateElection, statClasses: Set[StatClass]): Future[Boolean]

}

class ConcreteStatDao @Inject() (postcodeFlyweight: PostcodeFlyweight,
                                )(implicit @Named("dbExecutionContext") ec: ExecutionContext) extends StatDao {

  private val (s, d, p, v, a, r) = (
    StatRow.syntax,
    DivisionRow.syntax,
    PollingPlaceRow.syntax,
    SpecialVcpRow.syntax,
    AddressRow.syntax,
    RankRow.syntax,
  )

  private def statsMatching[A](whereClause: SQLSyntax): Future[Set[Stat[A]]] = Future {
    DB.localTx { implicit session =>

      withSQL[StatRow] {
        select
          .from(StatRow as s)
          .leftJoin(DivisionRow as d).on(s.division, d.id)
          .leftJoin(PollingPlaceRow as p).on(s.pollingPlace, p.id)
          .leftJoin(SpecialVcpRow as v).on(s.specialVcp, v.id)
          .leftJoin(AddressRow as a).on(p.address, a.id)
          .leftJoin(RankRow as r).on(s.id, r.stat)
          .where(whereClause)
      }
        .one(StatRow(postcodeFlyweight, s, d, p, v, a))
        .toMany(RankRow.opt(r))
        .map { (stat, ranks) => stat.copy(rankRows = ranks.toVector)}
        .traversable()
        .apply()
        .map(_.asStat)
        .toSet
    }
  }

  override def statsFor(election: SenateElection): Future[Set[Stat[SenateElection]]] = {
    import SQLSyntax._

    statsMatching(
      joinWithAnd(
        SQLSyntax.eq(s.election, ElectionEnumConverter(election)),
        isNull(s.state),
        isNull(s.division),
        isNull(s.pollingPlace),
        isNull(s.specialVcp),
      )
    )
  }

  override def statsFor(election: SenateElection, state: State): Future[Set[Stat[State]]] = {
    import SQLSyntax._

    statsMatching(
      joinWithAnd(
        SQLSyntax.eq(s.election, ElectionEnumConverter(election)),
        SQLSyntax.eq(s.state, StateEnumConverter(state)),
        isNull(s.division),
        isNull(s.pollingPlace),
        isNull(s.specialVcp),
      )
    )
  }

  override def statsFor(division: Division): Future[Set[Stat[Division]]] = {
    import SQLSyntax._

    statsMatching(
      joinWithAnd(
        SQLSyntax.eq(s.election, ElectionEnumConverter(division.election)),
        SQLSyntax.eq(s.state, StateEnumConverter(division.state)),
        SQLSyntax.eq(s.division, DivisionInsertableHelper.idOf(division)),
        isNull(s.pollingPlace),
        isNull(s.specialVcp),
      )
    )
  }

  override def statsFor(voteCollectionPoint: VoteCollectionPoint): Future[Set[Stat[VoteCollectionPoint]]] = {
    voteCollectionPoint match {
      case p: PollingPlace => statsFor(p)
      case s: SpecialVoteCollectionPoint => statsFor(s)
    }
  }

  private def statsFor(pollingPlace: PollingPlace): Future[Set[Stat[VoteCollectionPoint]]] = {
    import SQLSyntax._

    statsMatching(
      joinWithAnd(
        SQLSyntax.eq(s.election, ElectionEnumConverter(pollingPlace.election)),
        SQLSyntax.eq(s.state, StateEnumConverter(pollingPlace.state)),
        SQLSyntax.eq(s.division, DivisionInsertableHelper.idOf(pollingPlace.division)),
        SQLSyntax.eq(s.pollingPlace, PollingPlaceInsertableHelper.idOf(pollingPlace)),
        isNull(s.specialVcp),
      )
    )
  }

  private def statsFor(specialVcp: SpecialVoteCollectionPoint): Future[Set[Stat[VoteCollectionPoint]]] = {
    import SQLSyntax._

    statsMatching(
      joinWithAnd(
        SQLSyntax.eq(s.election, ElectionEnumConverter(specialVcp.election)),
        SQLSyntax.eq(s.state, StateEnumConverter(specialVcp.state)),
        SQLSyntax.eq(s.division, DivisionInsertableHelper.idOf(specialVcp.division)),
        isNull(s.pollingPlace),
        SQLSyntax.eq(s.specialVcp, SpecialVcpInsertableHelper.idOf(specialVcp)),
      )
    )
  }

  override def writeStats(election: SenateElection, stats: Iterable[Stat[Any]]): Future[Unit] = Future {
    DB.localTx { implicit session =>

      val idPerStat = insertStatsReturningIds(election, stats.toVector)

      insertRanksFor(idPerStat)
    }
  }

  private def insertStatsReturningIds(election: SenateElection, stats: Vector[Stat[Any]]
                                     )(implicit session: DBSession): Map[Stat[_], Long] = {
    val insertables = stats.map { stat =>
      StatInsertableHelper.toInsertableTuple(election, stat)
    }

    val insertStatement =
      sql"""INSERT INTO stat(stat_class, election, state, division, special_vcp, polling_place, amount, per_capita)
           |  VALUES (?, ?, ?, ?, ?, ?, ?, ?);
         """.stripMargin

    val ids: Vector[Long] = insertStatement.batchAndReturnGeneratedKey(insertables: _*).apply()

    (stats zip ids).toMap
  }

  private def insertRanksFor(idsPerStat: Map[Stat[_], Long])(implicit session: DBSession): Unit = {
    val rankInsertables: Seq[Insertable] = rankInsertablesFrom(idsPerStat)

    val insertStatement =
      sql"""INSERT INTO rank(stat,
           |    jurisdiction_level,
           |    ordinal,
           |    ordinal_is_shared,
           |    ordinal_per_capita,
           |    ordinal_per_capita_is_shared,
           |    total_count
           |  ) VALUES (
           |    {stat},
           |    {jurisdiction_level},
           |    {ordinal},
           |    {ordinal_is_shared},
           |    {ordinal_per_capita},
           |    {ordinal_per_capita_is_shared},
           |    {total_count}
           |  );
         """.stripMargin

    insertStatement.batchByName(rankInsertables: _*).apply()
  }

  private def rankInsertablesFrom(idsPerStat: Map[Stat[_], Long]): Seq[Insertable] = {
    idsPerStat.flatMap { case (stat, statId) =>
      stat.rankPerJurisdictionLevel.map { case (jurisdictionLevel, rank) =>
          val rankPerCapita = stat.rankPerCapitaPerJurisdictionLevel.get(jurisdictionLevel)

          RankInsertableHelper.toInsertable(statId, jurisdictionLevel, rank, rankPerCapita)
      }
    }
      .toSeq
  }

  override def hasSomeStatsForEachOf(election: SenateElection, statClasses: Set[StatClass]): Future[Boolean] = Future {
    DB.localTx { implicit session =>
      val electionId = ElectionDao.idOf(election).get

      val query = sql"""SELECT
                       |  stat_class,
                       |  COUNT(DISTINCT id) AS num_stats
                       |FROM stat
                       |WHERE
                       |  election = ${electionId}
                       |GROUP BY stat_class
                     """.stripMargin

      val numStatsPerClass = query
        .map(rs => StatClassEnumConverter.apply(rs.string("stat_class")) -> rs.int("num_stats"))
        .traversable()
        .apply()
        .toMap
        .withDefaultValue(0)

      statClasses.forall(numStatsPerClass(_) > 0)
    }
  }

}

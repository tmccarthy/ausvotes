package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.persistence.daos.rowentities._
import au.id.tmm.senatedb.api.persistence.entities.stats.Stat
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.senatedb.core.model.parsing.{Division, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.{ImplementedBy, Inject}
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ConcreteStatDao])
trait StatDao {

  def statsFor(election: SenateElection): Future[Set[Stat[SenateElection]]]

  def statsFor(state: State, election: SenateElection): Future[Set[Stat[State]]]

  def statsFor(division: Division): Future[Set[Stat[Division]]]

  def statsFor(voteCollectionPoint: VoteCollectionPoint): Future[Set[Stat[VoteCollectionPoint]]]

  def writeStats(stats: Iterable[Stat[Any]]): Future[Unit]

}

class ConcreteStatDao @Inject() (postcodeFlyweight: PostcodeFlyweight)(implicit ec: ExecutionContext) extends StatDao {

  override def statsFor(election: SenateElection): Future[Set[Stat[SenateElection]]] = Future {
    DB.localTx { implicit session =>
      val (s, r, d, v, a) = (
        StatRow.syntax,
        RankRow.syntax,
        DivisionRow.syntax,
        VoteCollectionPointRow.syntax,
        AddressRow.syntax,
      )

      withSQL[StatRow] {
        select
          .from(StatRow as s)
          .leftJoin(RankRow as r).on(s.id, r.statId)
          .leftJoin(DivisionRow as d).on(s.divisionId, d.id)
          .leftJoin(VoteCollectionPointRow as v).on(s.voteCollectionPointId, v.id)
          .leftJoin(AddressRow as a).on(v.addressId, a.id)
          .where
          .isNull(d.id)
          .and
          .isNull(v.id)
          .and
          .isNull(s.state)
      }
        .one(StatRow(postcodeFlyweight, s, d, v, a))
        .toMany(RankRow.opt(r))
        .map((statRow, rankRows) => statRow.copy(rankRows = rankRows.toVector))
        .list
        .apply()
        .toStream
        .map(_.asStat[SenateElection])
        .toSet
    }
  }

  override def statsFor(state: State, election: SenateElection): Future[Set[Stat[State]]] = ???

  override def statsFor(division: Division): Future[Set[Stat[Division]]] = ???

  override def statsFor(voteCollectionPoint: VoteCollectionPoint): Future[Set[Stat[VoteCollectionPoint]]] = ???

  override def writeStats(stats: Iterable[Stat[Any]]): Future[Unit] = ???

}

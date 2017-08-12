package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.persistence.entities.stats.Stat
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Division, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.State
import com.google.inject.{ImplementedBy, Inject}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ConcreteStatDao])
trait StatDao {

  def statsFor(election: SenateElection): Future[Set[Stat[SenateElection]]]

  def statsFor(state: State, election: SenateElection): Future[Set[Stat[State]]]

  def statsFor(division: Division): Future[Set[Stat[Division]]]

  def statsFor(voteCollectionPoint: VoteCollectionPoint): Future[Set[Stat[VoteCollectionPoint]]]

  def writeStats(stats: Iterable[Stat[Any]]): Future[Unit]

}

class ConcreteStatDao @Inject() ()(implicit ec: ExecutionContext) extends StatDao {

  override def statsFor(election: SenateElection): Future[Set[Stat[SenateElection]]] = ???

  override def statsFor(state: State, election: SenateElection): Future[Set[Stat[State]]] = ???

  override def statsFor(division: Division): Future[Set[Stat[Division]]] = ???

  override def statsFor(voteCollectionPoint: VoteCollectionPoint): Future[Set[Stat[VoteCollectionPoint]]] = ???

  override def writeStats(stats: Iterable[Stat[Any]]): Future[Unit] = ???
}

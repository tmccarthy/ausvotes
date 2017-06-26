package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.webapp.services.exceptions.NoSuchElectionException
import com.google.common.collect.ImmutableBiMap
import com.google.inject.{ImplementedBy, Singleton}

import scala.concurrent.{ExecutionContext, Future}

// TODO the election table in the db should be populated from the enum
@ImplementedBy(classOf[HardCodedElectionDao])
trait ElectionDao {
  def electionWithIdFuture(electionId: String): Future[Option[SenateElection]]

  def electionWithId(electionId: String): Option[SenateElection]

  def idOf(election: SenateElection): Option[String]

  def idOf(aecElectionId: Int): Option[String]

  def withParsedElection[A](electionId: String)(block: SenateElection => Future[A])(implicit ec: ExecutionContext): Future[A] = {
    electionWithIdFuture(electionId)
      .flatMap {
        case Some(matchingElection) => block(matchingElection)
        case None => Future.failed(NoSuchElectionException(electionId))
      }
  }
}

@Singleton
class HardCodedElectionDao extends ElectionDao {

  // TODO needs scala implementation
  private val electionIdLookup: ImmutableBiMap[SenateElection, String] = ImmutableBiMap.of(
    SenateElection.`2016`, "2016",
    SenateElection.`2014 WA`, "2014WA",
    SenateElection.`2013`, "2013"
  )

  private val electionIdLookupByAecId: ImmutableBiMap[Int, String] = ImmutableBiMap.of(
    SenateElection.`2016`.aecID, electionIdLookup.get(SenateElection.`2016`),
    SenateElection.`2014 WA`.aecID, electionIdLookup.get(SenateElection.`2014 WA`),
    SenateElection.`2013`.aecID, electionIdLookup.get(SenateElection.`2013`)
  )

  override def electionWithIdFuture(electionId: String): Future[Option[SenateElection]] = {
    val election = electionWithId(electionId)

    Future.successful(election)
  }

  override def electionWithId(electionId: String): Option[SenateElection] = {
    Option(electionIdLookup.inverse.get(electionId.toUpperCase))
  }

  override def idOf(election: SenateElection): Option[String] = {
    Option(electionIdLookup.get(election))
  }

  override def idOf(aecElectionId: Int): Option[String] = {
    Option(electionIdLookupByAecId.get(aecElectionId))
  }
}
package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.services.exceptions.NoSuchElectionException
import au.id.tmm.senatedb.core.model.SenateElection
import com.google.common.collect.ImmutableBiMap
import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

@Singleton
object ElectionDao {

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

  def electionWithId(electionId: String): Option[SenateElection] = {
    Option(electionIdLookup.inverse.get(electionId.toUpperCase))
  }

  def idOf(election: SenateElection): Option[String] = {
    Option(electionIdLookup.get(election))
  }

  def idOf(aecElectionId: Int): Option[String] = {
    Option(electionIdLookupByAecId.get(aecElectionId))
  }

  def withParsedElection[A](electionId: String)(block: SenateElection => Future[A])
                           (implicit ec: ExecutionContext): Future[A] = {
    electionWithId(electionId) match {
      case Some(matchingElection) => block(matchingElection)
      case None => Future.failed(NoSuchElectionException(electionId))
    }
  }
}
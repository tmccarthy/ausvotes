package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.services.exceptions.NoSuchElectionException
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.utilities.collection.BiMap
import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

@Singleton
object ElectionDao {

  private val lookup: BiMap[SenateElection, String] = BiMap(
    SenateElection.`2016` -> "2016",
    SenateElection.`2014 WA` -> "2014WA",
    SenateElection.`2013` -> "2013"
  )

  private val electionIdLookup: BiMap[Int, String] = BiMap(
    SenateElection.`2016`.aecID -> lookup(SenateElection.`2016`),
    SenateElection.`2014 WA`.aecID -> lookup(SenateElection.`2014 WA`),
    SenateElection.`2013`.aecID -> lookup(SenateElection.`2013`)
  )

  def electionWithId(electionId: String): Option[SenateElection] = {
    Option(lookup.inverse(electionId.toUpperCase))
  }

  def idOf(election: SenateElection): Option[String] = {
    Option(lookup(election))
  }

  def idOf(aecElectionId: Int): Option[String] = {
    Option(electionIdLookup(aecElectionId))
  }

  def withParsedElection[A](electionId: String)(block: SenateElection => Future[A])
                           (implicit ec: ExecutionContext): Future[A] = {
    electionWithId(electionId) match {
      case Some(matchingElection) => block(matchingElection)
      case None => Future.failed(NoSuchElectionException(electionId))
    }
  }
}
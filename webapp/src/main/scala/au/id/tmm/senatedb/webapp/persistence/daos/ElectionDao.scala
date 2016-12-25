package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.model.SenateElection
import com.google.inject.{ImplementedBy, Singleton}

import scala.concurrent.Future

// TODO the election table in the db should be populated from the enum
@ImplementedBy(classOf[HardCodedElectionDao])
trait ElectionDao {
  def electionWithId(electionId: String): Future[Option[SenateElection]]

  def electionWithIdBlocking(electionId: String): Option[SenateElection]
}

// TODO replace this with something that goes to the db?
@Singleton
class HardCodedElectionDao extends ElectionDao {

  override def electionWithId(electionId: String): Future[Option[SenateElection]] = {
    val election = electionWithIdBlocking(electionId)

    Future.successful(election)
  }

  override def electionWithIdBlocking(electionId: String): Option[SenateElection] = {
    electionId.toLowerCase match {
      case "2016" => Some(SenateElection.`2016`)
      case "2014WA" => Some(SenateElection.`2014 WA`)
      case "2013" => Some(SenateElection.`2013`)
      case _ => None
    }
  }

}
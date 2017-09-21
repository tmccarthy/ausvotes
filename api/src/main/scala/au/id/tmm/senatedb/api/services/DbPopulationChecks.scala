package au.id.tmm.senatedb.api.services

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import au.id.tmm.senatedb.api.persistence.daos.ElectionDao
import au.id.tmm.senatedb.api.persistence.population.DbPopulationActor
import au.id.tmm.senatedb.core.model.SenateElection

import scala.concurrent.duration.DurationLong
import scala.concurrent.{ExecutionContext, Future}

trait DbPopulationChecks {

  private implicit val timeout: Timeout = Timeout(1.seconds)

  protected def dbPopulationActor: ActorRef

  protected def requiresElectionPopulated[A](electionId: String)(block: SenateElection => Future[A])
                                            (implicit ec: ExecutionContext): Future[A] = {
    ElectionDao.withParsedElection(electionId) { election =>
      requiresElectionPopulated(election)(block(election))
    }
  }

  protected def requiresElectionPopulated[A](election: SenateElection)(block: => Future[A])(implicit ec: ExecutionContext): Future[A] = {
    checkElectionPopulated(election)
      .flatMap {
        case true => block
        case false => Future.failed(RequiredElectionNotPopulatedException(election))
      }
  }

  private def checkElectionPopulated(election: SenateElection)(implicit ec: ExecutionContext): Future[Boolean] = {
    (dbPopulationActor ? DbPopulationActor.Requests.IsElectionPopulated(election))
      .map {
        case DbPopulationActor.Responses.ElectionPopulatedStatus(electionForResponse, isPopulated) => {
          assert(electionForResponse == election)

          isPopulated
        }
      }
  }
}

case class RequiredElectionNotPopulatedException(missingElection: SenateElection) extends Exception

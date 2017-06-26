package au.id.tmm.senatedb.webapp.services

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.webapp.persistence.daos.ElectionDao
import au.id.tmm.senatedb.webapp.persistence.population.DbPopulationActor

import scala.concurrent.duration.DurationLong
import scala.concurrent.{ExecutionContext, Future}

trait DbPopulationChecks {

  private implicit val timeout = Timeout(1.seconds)

  protected def dbPopulationActor: ActorRef

  protected def electionDao: ElectionDao

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

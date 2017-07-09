package au.id.tmm.senatedb.api.services

import akka.actor.ActorRef
import akka.pattern.ask
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.api.persistence.daos.ElectionDao
import au.id.tmm.senatedb.api.persistence.population.DbPopulationActor
import com.google.inject.Inject
import com.google.inject.name.Named

import scala.concurrent.duration.DurationDouble
import scala.concurrent.{ExecutionContext, Future}

class DbPopulationService @Inject() (@Named("dbPopulationActor") dbPopulationActor: ActorRef,
                                     electionDao: ElectionDao)
                                    (implicit ec: ExecutionContext) {


  def isElectionPopulated(electionId: String): Future[Boolean] =
    electionDao.withParsedElection(electionId) { election =>
      isElectionPopulated(election)
    }

  private def isElectionPopulated(election: SenateElection): Future[Boolean] = {
    implicit val timeout = akka.util.Timeout(1.seconds)

    (dbPopulationActor ? DbPopulationActor.Requests.IsElectionPopulated(election)).map {
      case DbPopulationActor.Responses.ElectionPopulatedStatus(_, isPopulated) => isPopulated
    }
  }

  def beginPopulationFor(electionId: String): Future[Unit] =
    electionDao.withParsedElection(electionId) { election =>
      beginPopulationFor(election)
    }

  private def beginPopulationFor(election: SenateElection): Future[Unit] = {
    implicit val timeout = akka.util.Timeout(1.seconds)

    (dbPopulationActor ? DbPopulationActor.Requests.PleasePopulateForElection(election, replyWhenDone = false)).map {
      case DbPopulationActor.Responses.OkIWillPopulateElection(electionToPopulate) =>
        Unit

      case DbPopulationActor.Responses.AlreadyPopulatingAnotherElection(electionBeingPopulated) =>
        throw DbPopulationService.Exceptions.AnotherElectionCurrentlyPopulatingException(electionBeingPopulated)
    }
  }
}

object DbPopulationService {

  object Exceptions {
    case class AnotherElectionCurrentlyPopulatingException(election: SenateElection) extends Exception
  }

}
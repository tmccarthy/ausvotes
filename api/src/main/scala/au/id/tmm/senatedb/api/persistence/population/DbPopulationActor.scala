package au.id.tmm.senatedb.api.persistence.population

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.pipe
import au.id.tmm.senatedb.api.persistence.population.DbPopulationActor.{Requests, Responses, SelfMessages}
import au.id.tmm.senatedb.core.model.SenateElection
import com.google.inject.Inject

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class DbPopulationActor @Inject() (dbPopulator: DbPopulator)(implicit ec: ExecutionContext) extends Actor {

  private val actorsToBeToldWhenFinishedCurrentPopulationJob: mutable.Set[ActorRef] = mutable.Set()

  private var electionCurrentlyPopulating: Option[SenateElection] = None

  override def receive: Receive = {
    case Requests.AreYouCurrentlyPopulating =>
      sender ! Responses.CurrentlyPopulatingForElection(electionCurrentlyPopulating)

    case Requests.IsElectionPopulated(election) => {
      if (electionCurrentlyPopulating contains election) {
        sender ! Responses.ElectionPopulatedStatus(election, isPopulated = false)
      } else {
        dbPopulator.isPopulatedFor(election)
          .map(isPopulatedFlag => Responses.ElectionPopulatedStatus(election, isPopulatedFlag))
          .pipeTo(sender)
      }
    }

    case Requests.PleasePopulateForElection(election, replyWhenDone) => {
      // TODO we should respond differently if the election is already populated
      if (electionCurrentlyPopulating.isDefined && !electionCurrentlyPopulating.contains(election)) {
        sender ! Responses.AlreadyPopulatingAnotherElection(electionCurrentlyPopulating.get)
      } else {
        if (replyWhenDone) {
          actorsToBeToldWhenFinishedCurrentPopulationJob += sender
        } else {
          sender ! Responses.OkIWillPopulateElection(election)
        }

        if (electionCurrentlyPopulating.isEmpty) {
          electionCurrentlyPopulating = Some(election)

          dbPopulator.populateAsRequired(election)
            .map(_ => SelfMessages.ElectionPopulationFinished)
            .pipeTo(self)
        }
      }
    }

    case SelfMessages.ElectionPopulationFinished => {
      actorsToBeToldWhenFinishedCurrentPopulationJob
        .foreach(_ ! Responses.FinishedPopulatingFor(electionCurrentlyPopulating.get))

      actorsToBeToldWhenFinishedCurrentPopulationJob.clear()
      electionCurrentlyPopulating = None
    }
  }
}

object DbPopulationActor {
  //noinspection AppropriateActorConstructorNotFound
  def props(dbPopulator: DbPopulator)(implicit ec: ExecutionContext) =
    Props(classOf[DbPopulationActor], dbPopulator, ec)

  object Requests {
    case object AreYouCurrentlyPopulating
    case class IsElectionPopulated(election: SenateElection)
    case class PleasePopulateForElection(election: SenateElection, replyWhenDone: Boolean)
  }

  object Responses {
    case class CurrentlyPopulatingForElection(electionCurrentlyPopulating: Option[SenateElection])
    case class ElectionPopulatedStatus(election: SenateElection, isPopulated: Boolean)

    case class OkIWillPopulateElection(election: SenateElection)
    case class AlreadyPopulatingAnotherElection(election: SenateElection)

    case class FinishedPopulatingFor(election: SenateElection)
  }

  private object SelfMessages {
    case object ElectionPopulationFinished
  }
}
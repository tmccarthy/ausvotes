package au.id.tmm.senatedb.api.controllers

import au.id.tmm.senatedb.api.services.DbPopulationService
import au.id.tmm.senatedb.api.services.exceptions.NoSuchElectionException
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

@Singleton
class DbPopulationController @Inject()(dbPopulationService: DbPopulationService)
                                      (implicit ec: ExecutionContext) extends Controller {

  def populateFor(electionId: String): Action[AnyContent] = Action.async {
    dbPopulationService.beginPopulationFor(electionId)
      .map(_ => Ok(s"Begun populating for $electionId"))
      .recover {
        case _: NoSuchElectionException => NotFound(electionId) // TODO handle this with an application level error handler
        case e: DbPopulationService.Exceptions.AnotherElectionCurrentlyPopulatingException => Ok(s"Already populating ${e.election}")
      }
  }

  def checkPopulationStatusFor(electionId: String): Action[AnyContent] = Action.async {
    dbPopulationService.isElectionPopulated(electionId)
      .map(electionIsPopulated => Ok(electionIsPopulated.toString))
      .recover {
        case _: NoSuchElectionException => NotFound(electionId) // TODO handle this with an application level error handler
      }
  }
}

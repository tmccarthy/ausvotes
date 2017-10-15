package au.id.tmm.ausvotes.api.servlets

import au.id.tmm.ausvotes.backend.services.DivisionService
import au.id.tmm.ausvotes.backend.services.exceptions.{NoSuchDivisionException, NoSuchElectionException, NoSuchStateException}
import com.google.inject.Inject
import org.scalatra.{NotFound, Ok, ScalatraServlet}

import scala.concurrent.Await
import scala.concurrent.duration.DurationLong

class DivisionServlet @Inject() (divisionService: DivisionService) extends ScalatraServlet {

  get("/:election/:state/:divisionName") {
    try {
      // TODO don't use await
      val foundDivision = Await.result(
        divisionService.divisionWith(
          electionId = params('election),
          stateAbbreviation = params('state),
          divisionName = params('divisionName),
        ), 1.second)
      Ok(foundDivision.name)
    } catch {
      // TODO generic "not found" handling
      case e: NoSuchElectionException => NotFound(e.electionId)
      case e: NoSuchStateException    => NotFound(e.stateAbbreviation)
      case e: NoSuchDivisionException => NotFound(e.divisionName)
    }
  }

}

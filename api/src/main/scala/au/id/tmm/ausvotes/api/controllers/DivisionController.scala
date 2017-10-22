package au.id.tmm.ausvotes.api.controllers

import javax.inject.Named

import au.id.tmm.ausvotes.backend.services.DivisionService
import au.id.tmm.ausvotes.backend.services.exceptions.{NoSuchDivisionException, NoSuchElectionException, NoSuchStateException}
import com.google.inject.Inject
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json._

import scala.concurrent.ExecutionContext

class DivisionController @Inject()(divisionService: DivisionService)
                                  (@Named("servletExecutionContext") override implicit protected val executor: ExecutionContext)
  extends ScalatraServlet with FutureSupport with JacksonJsonSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/:election/:state/:divisionName") {
    new AsyncResult() {
      val is = {
        divisionService.divisionWith(
          electionId = params('election),
          stateAbbreviation = params('state),
          divisionName = params('divisionName),
        )
          .map(foundDivision => Ok(foundDivision))
          .recover {
            // TODO generic "not found" handling
            case e: NoSuchElectionException => NotFound(e.electionId)
            case e: NoSuchStateException => NotFound(e.stateAbbreviation)
            case e: NoSuchDivisionException => NotFound(e.divisionName)
          }
      }
    }
  }

}

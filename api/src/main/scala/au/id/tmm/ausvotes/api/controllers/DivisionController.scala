package au.id.tmm.ausvotes.api.controllers

import javax.inject.Named

import au.id.tmm.ausvotes.api.errorhandling.HandlesErrors
import au.id.tmm.ausvotes.api.jsonformats.ApiFormats
import au.id.tmm.ausvotes.backend.services.DivisionService
import com.google.inject.Inject
import org.json4s.Formats
import org.scalatra._
import org.scalatra.json._

import scala.concurrent.ExecutionContext

class DivisionController @Inject()(divisionService: DivisionService)
                                  (@Named("servletExecutionContext") override implicit protected val executor: ExecutionContext)
  extends ScalatraServlet with FutureSupport with JacksonJsonSupport with HandlesErrors {

  protected implicit lazy val jsonFormats: Formats = ApiFormats

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
      }
    }
  }

  get("/anError") {
    throw new Exception("this is a thing")
  }
}

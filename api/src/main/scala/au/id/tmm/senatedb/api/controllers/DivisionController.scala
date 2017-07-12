package au.id.tmm.senatedb.api.controllers

import javax.inject.{Inject, Singleton}

import au.id.tmm.senatedb.api.services.{CannotFindStatsForDivision, DivisionService, RequiredElectionNotPopulatedException}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

@Singleton
class DivisionController @Inject() (divisionService: DivisionService)(implicit ec: ExecutionContext) extends Controller {

  import au.id.tmm.senatedb.api.controllers.entities.JsonConversions._

  def index(electionId: String, stateAbbreviation: String, divisionName: String): Action[AnyContent] = Action.async {
    divisionService
      .divisionStatsFor(electionId, stateAbbreviation, divisionName)
      .map { divisionStats =>
        Ok(Json.toJson(divisionStats)) // TODO error handling
      }
      .recover {
        case CannotFindStatsForDivision(_, _, _) => NotFound("No dice!")
        case RequiredElectionNotPopulatedException(_) => Ok("Site under maintenance")
      }
  }
}

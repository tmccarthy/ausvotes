package au.id.tmm.senatedb.api.controllers

import javax.inject.{Inject, Singleton}

import au.id.tmm.senatedb.api.services.{CannotFindStatsForDivision, DivisionService, RequiredElectionNotPopulatedException}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

@Singleton
class DivisionController @Inject() (divisionService: DivisionService)(implicit ec: ExecutionContext) extends Controller {

  def index(electionId: String, stateAbbreviation: String, divisionName: String): Action[AnyContent] = Action.async {
    divisionService
      .divisionWithStatsFor(electionId, stateAbbreviation, divisionName)
      .map {
        case (division, divisionStats) => Ok(views.html.division(division, divisionStats))
      }
      .recover {
        case CannotFindStatsForDivision(_, _, _) => NotFound("No dice!")
        case RequiredElectionNotPopulatedException(_) => Ok("Site under maintenance")
      }
  }
}

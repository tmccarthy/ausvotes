package au.id.tmm.senatedb.webapp.controllers

import javax.inject.{Inject, Singleton}

import au.id.tmm.senatedb.webapp.persistence.daos.{DivisionDao, ElectionDao}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, AnyContent, Controller}

@Singleton
class DivisionController @Inject() (electionDao: ElectionDao, divisionDao: DivisionDao) extends Controller {

  def index(electionId: String, stateAbbreviation: String, divisionName: String): Action[AnyContent] = Action.async {
    divisionDao.findWithStats(electionId, stateAbbreviation, divisionName).map {
      case Some((division, divisionStats)) => Ok(views.html.division(division, divisionStats))
      case None => NotFound("No dice!")
    }
  }

}

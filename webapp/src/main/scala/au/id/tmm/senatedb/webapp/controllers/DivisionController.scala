package au.id.tmm.senatedb.webapp.controllers

import javax.inject.{Inject, Singleton}

import au.id.tmm.senatedb.webapp.persistence.daos.{DivisionDao, ElectionDao}
import au.id.tmm.utilities.geo.australia.State
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

@Singleton
class DivisionController @Inject() (electionDao: ElectionDao, divisionDao: DivisionDao) extends Controller {

  def index(electionId: String, stateAbbreviation: String, divisionName: String): Action[AnyContent] = Action.async {
    for {
      election <- electionDao.electionWithId(electionId)
      state <- Future.successful(State.fromAbbreviation(stateAbbreviation.toUpperCase))
      division <- divisionDao.fromName(divisionName)
    } yield {
      if (election.isDefined && state.isDefined && division.isDefined) {
        Ok(views.html.division(election.get, state.get, division.get))
      } else {
        NotFound("No dice!")
      }
    }
  }

}

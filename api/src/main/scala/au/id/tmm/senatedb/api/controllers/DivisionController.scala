package au.id.tmm.senatedb.api.controllers

import javax.inject.{Inject, Singleton}

import au.id.tmm.senatedb.api.services.DivisionService
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DivisionController @Inject() (divisionService: DivisionService)(implicit ec: ExecutionContext) extends Controller {

  def index(electionId: String, stateAbbreviation: String, divisionName: String): Action[AnyContent] = Action.async {
    Future(???)
  }
}

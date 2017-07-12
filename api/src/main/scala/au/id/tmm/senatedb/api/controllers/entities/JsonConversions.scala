package au.id.tmm.senatedb.api.controllers.entities

import au.id.tmm.senatedb.api.persistence.entities.{DivisionStats, TotalFormalBallotsTally}
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Division
import au.id.tmm.utilities.geo.australia.State
import play.api.libs.json._

object JsonConversions {

  implicit val writesSenateElection = new Writes[SenateElection] {
    override def writes(election: SenateElection): JsValue = Json.obj(
      "date" -> election.date,
      "name" -> election.name
    )
  }

  implicit val writesState: OWrites[State] = Json.writes[State]

  implicit val writesDivision: OWrites[Division] = Json.writes[Division]

  implicit val writesTotalFormalBallotsTally: OWrites[TotalFormalBallotsTally] = Json.writes[TotalFormalBallotsTally]

  implicit val writesDivisionStats = new Writes[DivisionStats] {
    override def writes(stats: DivisionStats): JsValue = Json.obj(
      "division" -> stats.division,
      "total_formal_ballots" -> stats.totalFormalBallots
    )
  }
}

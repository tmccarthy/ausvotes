package au.id.tmm.senatedb.api.controllers.entities

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
}

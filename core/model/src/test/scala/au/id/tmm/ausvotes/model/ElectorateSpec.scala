package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.federal.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class ElectorateSpec extends ImprovedFlatSpec {

  "an stv candidate" can "be encoded to json" in {
    val electorate = Electorate(
      election = SenateElectionForState(SenateElection.`2016`, State.VIC).right.get,
      name = "Electorate",
      id = Electorate.Id(42),
    )

    val json = Json.obj(
      "election" -> electorate.election.asJson,
      "name" -> electorate.name.asJson,
      "id" -> electorate.id.asJson,
    )

    assert(electorate.asJson === json)
  }

  it can "be decoded from json" in {
    val electorate = Electorate(
      election = SenateElectionForState(SenateElection.`2016`, State.VIC).right.get,
      name = "Electorate",
      id = Electorate.Id(42),
    )

    val json = Json.obj(
      "election" -> electorate.election.asJson,
      "name" -> electorate.name.asJson,
      "id" -> electorate.id.asJson,
    )

    assert(json.as[Electorate[SenateElectionForState]] === Right(electorate))
  }

}

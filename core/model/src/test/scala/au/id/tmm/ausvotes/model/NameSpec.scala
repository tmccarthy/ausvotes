package au.id.tmm.ausvotes.model

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class NameSpec extends ImprovedFlatSpec {

  "two names" can "be compared ignoring case" in {
    assert(Name("Jane", "LANE") equalsIgnoreCase Name("JANE", "Lane"))
  }

  they can "be found unequal, ignoring case" in {
    assert(!(Name("Jane", "LANE") equalsIgnoreCase Name("Daria", "Morgandorfer")))
  }

  "a name" can "be encoded to json" in {
    val name = Name("Jane", "Lane")

    val json = Json.obj(
      "givenNames" -> Json.fromString("Jane"),
      "surname" -> Json.fromString("Lane"),
    )

    assert(name.asJson === json)
  }

  it can "be decoded from json" in {
    val json = Json.obj(
      "givenNames" -> Json.fromString("Jane"),
      "surname" -> Json.fromString("Lane"),
    )

    assert(json.as[Name] === Right(Name("Jane", "Lane")))
  }

}

package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Name
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class GeneralCodecsSpec extends ImprovedFlatSpec {

  import GeneralCodecs._

  "the election codec" should "encode an election" in {
    val election: SenateElection = SenateElection.`2016`

    assert(election.asJson === jString("2016"))
  }

  it should "decode an election" in {
    val json = jString("2016")

    assert(json.as[SenateElection] === DecodeResult.ok(SenateElection.`2016`))
  }

  it should "fail to decode if the election is not recognised" in {
    val json = jString("invalid")

    assert(json.as[SenateElection].isError)
  }

  "the state codec" should "encode a state" in {
    val state: State = State.SA

    assert(state.asJson === jString("SA"))
  }

  it should "decode a state" in {
    val json = jString("SA")

    assert(json.as[State] === DecodeResult.ok(State.SA))
  }

  it should "fail to decode if the election is not recognised" in {
    val json = jString("invalid")

    assert(json.as[State].isError)
  }

  "the name codec" should "encode a name" in {
    val name = Name("Jane", "Doe")

    val expectedJson = jObjectFields(
      "givenNames" -> jString("Jane"),
      "surname" -> jString("Doe"),
    )

    assert(name.asJson === expectedJson)
  }

  it should "decode a name" in {
    val json = jObjectFields(
      "givenNames" -> jString("Jane"),
      "surname" -> jString("Doe"),
    )

    val expectedName = Name("Jane", "Doe")

    assert(json.as[Name] === DecodeResult.ok(expectedName))
  }

}

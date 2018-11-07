package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.parsing.Party
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PartyCodecSpec extends ImprovedFlatSpec {

  import PartyCodec._

  "the party codec" should "encode a registered party" in {
    val party: Party = Party.RegisteredParty.ALP

    val expectedJson = jString(Party.RegisteredParty.ALP.name)

    assert(party.asJson === expectedJson)
  }

  it should "encode an independent" in {
    val independent: Party = Party.Independent

    val expectedJson = jString("independent")

    assert(independent.asJson === expectedJson)
  }

  it should "decode a party" in {
    val json = jString("Australian Labor Party")

    val expectedParty = Party.RegisteredParty.ALP

    assert(json.as[Party] === DecodeResult.ok(expectedParty))
  }

  it should "decode an independent" in {
    val json = jString("independent")

    assert(json.as[Party] === DecodeResult.ok(Party.Independent))
  }

}

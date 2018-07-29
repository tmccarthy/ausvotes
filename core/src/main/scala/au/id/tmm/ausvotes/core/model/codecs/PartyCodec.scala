package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.flyweights.RegisteredPartyFlyweight
import au.id.tmm.ausvotes.core.model.parsing.Party

final class PartyCodec private (private val registeredPartyFlyweight: RegisteredPartyFlyweight)
  extends EncodeJson[Party] with DecodeJson[Party] {
  override def encode(a: Party): Json = a match {
    case Party.Independent => jString("independent")
    case Party.RegisteredParty(name) => jString(name)
  }

  override def decode(cursor: HCursor): DecodeResult[Party] = cursor.as[String].map{
    case "independent" => Party.Independent
    case name => Party.RegisteredParty(name)
  }
}

object PartyCodec {
  def apply(registeredPartyFlyweight: RegisteredPartyFlyweight = RegisteredPartyFlyweight()): PartyCodec =
    new PartyCodec(registeredPartyFlyweight)
}
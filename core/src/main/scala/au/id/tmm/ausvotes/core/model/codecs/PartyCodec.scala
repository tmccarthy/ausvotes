package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.flyweights.RegisteredPartyFlyweight
import au.id.tmm.ausvotes.core.model.parsing.Party

object PartyCodec {

  implicit val encodeParty: EncodeJson[Party] = {
    case Party.Independent => jString("independent")
    case Party.RegisteredParty(name) => jString(name)
  }

  implicit def decodeParty: DecodeJson[Party] = {
    val flyweight = RegisteredPartyFlyweight()

    DecodeJson { cursor =>
      cursor.as[String].map{
        case "independent" => Party.Independent
        case name => flyweight(name)
      }
    }
  }

}

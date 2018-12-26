package au.id.tmm.ausvotes.model

import io.circe.{Decoder, Encoder, Json}

sealed trait Preference

object Preference {

  implicit val encoder: Encoder[Preference] = {
    case Numbered(asInt) => Json.fromInt(asInt)
    case Tick => Json.fromString("✓")
    case Cross => Json.fromString("x")
  }

  implicit val decoder: Decoder[Preference] =
    Decoder.decodeInt.map[Preference](Numbered) or
      Decoder.decodeString.emap[Preference] {
        case "✓" => Right(Tick)
        case "x" => Right(Cross)
        case x => Left(s"""Invalid preference "$x"""")
      }

  final case class Numbered(asInt: Int) extends Preference
  case object Tick extends Preference
  case object Cross extends Preference

}

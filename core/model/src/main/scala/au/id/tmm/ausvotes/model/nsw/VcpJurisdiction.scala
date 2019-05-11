package au.id.tmm.ausvotes.model.nsw

import io.circe.{Decoder, Encoder}

final case class VcpJurisdiction(
                                  district: District,
                                )

object VcpJurisdiction {
  implicit val encoder: Encoder[VcpJurisdiction] = Encoder.forProduct1("district")(_.district)
  implicit val decoder: Decoder[VcpJurisdiction] = Decoder.forProduct1("district")(VcpJurisdiction.apply)
}

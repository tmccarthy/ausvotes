package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.Codecs.Codec
import io.circe.{Decoder, Encoder}

final case class CandidateDetails[E](
                                      election: E,
                                      name: Name,
                                      party: Option[Party],
                                      id: CandidateDetails.Id,
                                    )

object CandidateDetails {

  implicit def encoder[E : Encoder]: Encoder[CandidateDetails[E]] = Encoder.forProduct4("election", "name", "party", "id")(c => (c.election, c.name, c.party, c.id))
  implicit def decoder[E : Decoder]: Decoder[CandidateDetails[E]] = Decoder.forProduct4("election", "name", "party", "id")(CandidateDetails.apply)

  final case class Id(asInt: Int)

  object Id {
    implicit val codec: Codec[Id] = Codecs.simpleCodec(_.asInt, Id(_))
  }

}

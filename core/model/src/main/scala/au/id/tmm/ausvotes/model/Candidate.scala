package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.Codecs.Codec
import io.circe.{Decoder, Encoder}

final case class Candidate[E](
                               election: E,
                               name: Name,
                               party: Option[Party],
                               id: Candidate.Id,
                             )

object Candidate {

  implicit def encoder[E : Encoder]: Encoder[Candidate[E]] = Encoder.forProduct4("election", "name", "party", "id")(c => (c.election, c.name, c.party, c.id))
  implicit def decoder[E : Decoder]: Decoder[Candidate[E]] = Decoder.forProduct4("election", "name", "party", "id")(Candidate.apply)

  final case class Id(asInt: Int)

  object Id {
    implicit val codec: Codec[Id] = Codecs.simpleCodec(_.asInt, Id(_))
  }

}

package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.Codecs.Codec
import io.circe.{Decoder, Encoder}

final case class Electorate[E](
                                election: E,
                                name: String,
                                id: Electorate.Id,
                              )

object Electorate {

  implicit def encoder[E : Encoder]: Encoder[Electorate[E]] = Encoder.forProduct3("election", "name", "id")(c => (c.election, c.name, c.id))
  implicit def decoder[E : Decoder]: Decoder[Electorate[E]] = Decoder.forProduct3("election", "name", "id")(Electorate.apply)

  final case class Id(asInt: Int) extends AnyVal

  object Id {
    implicit val codec: Codec[Id] = Codecs.simpleCodec[Id, Int](encode = _.asInt, decode = Id(_))
  }

}

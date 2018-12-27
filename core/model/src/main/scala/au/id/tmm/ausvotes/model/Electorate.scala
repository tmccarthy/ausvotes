package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.Codecs.Codec
import io.circe.{Decoder, Encoder}

final case class Electorate[E, J](
                                   election: E,
                                   jurisdiction: J,
                                   name: String,
                                   id: Electorate.Id,
                                 )

object Electorate {

  implicit def ordering[E : Ordering, J : Ordering]: Ordering[Electorate[E, J]] = Ordering.by(e => (e.election, e.jurisdiction, e.name))

  implicit def encoder[E : Encoder, J : Encoder]: Encoder[Electorate[E, J]] = Encoder.forProduct4("election", "jurisdiction", "name", "id")(c => (c.election, c.jurisdiction, c.name, c.id))
  implicit def decoder[E : Decoder, J : Decoder]: Decoder[Electorate[E, J]] = Decoder.forProduct4("election", "jurisdiction", "name", "id")(Electorate.apply)

  final case class Id(asInt: Int) extends AnyVal

  object Id {
    implicit val codec: Codec[Id] = Codecs.simpleCodec[Id, Int](encode = _.asInt, decode = Id(_))
  }

}

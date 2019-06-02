package au.id.tmm.ausvotes.model

import io.circe.{Decoder, Encoder}

final case class Electorate[E, J](
                                   election: E,
                                   jurisdiction: J,
                                   name: String,
                                 )

object Electorate {

  implicit def ordering[E : Ordering, J : Ordering]: Ordering[Electorate[E, J]] = Ordering.by(e => (e.election, e.jurisdiction, e.name))

  implicit def encoder[E : Encoder, J : Encoder]: Encoder[Electorate[E, J]] = Encoder.forProduct3("election", "jurisdiction", "name")(c => (c.election, c.jurisdiction, c.name))
  implicit def decoder[E : Decoder, J : Decoder]: Decoder[Electorate[E, J]] = Decoder.forProduct3("election", "jurisdiction", "name")(Electorate.apply)

}

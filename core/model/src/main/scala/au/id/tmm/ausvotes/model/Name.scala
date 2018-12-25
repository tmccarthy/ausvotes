package au.id.tmm.ausvotes.model

import io.circe.{Decoder, Encoder}

final case class Name(givenNames: String, surname: String) {

  def equalsIgnoreCase(that: Name): Boolean = (this.givenNames equalsIgnoreCase that.givenNames) &&
    (this.surname equalsIgnoreCase that.surname)

}

object Name {

  implicit val encoder: Encoder[Name] = Encoder.forProduct2("givenNames", "surname")(n => (n.givenNames, n.surname))
  implicit val decoder: Decoder[Name] = Decoder.forProduct2("givenNames", "surname")(Name.apply)

}

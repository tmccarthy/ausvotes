package au.id.tmm.ausvotes.model

import io.circe.Decoder.Result
import io.circe._
import io.circe.syntax.EncoderOps

object Codecs {

  type Codec[A] = Decoder[A] with Encoder[A]

  def simpleCodec[A, A1 : Decoder : Encoder](encode: A => A1, decode: A1 => A): Codec[A] = new Decoder[A] with Encoder[A] {
    override def apply(c: HCursor): Result[A] = c.as[A1].map(decode)

    override def apply(a: A): Json = encode(a).asJson
  }

  def partialCodec[A, A1 : Decoder : Encoder](
                                               encode: A => A1,
                                               decode: A1 => Option[A],
                                             ): Codec[A] = new Decoder[A] with Encoder[A] {

    private val completeDecoder: A1 => Either[String, A] = intermediateValue => decode(intermediateValue) match {
      case Some(value) => Right(value)
      case None => Left(s"""Unable to parse value "$intermediateValue"""")
    }

    override def apply(c: HCursor): Result[A] = c.as[A1].flatMap { intermediateValue =>
      completeDecoder(intermediateValue).left.map(errorMessage => DecodingFailure(errorMessage, c.history))
    }

    override def apply(a: A): Json = encode(a).asJson
  }

}

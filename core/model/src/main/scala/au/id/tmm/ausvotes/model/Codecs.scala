package au.id.tmm.ausvotes.model

import io.circe.Decoder.Result
import io.circe._
import io.circe.syntax.EncoderOps

import scala.reflect.ClassTag

object Codecs {

  type Codec[A] = Decoder[A] with Encoder[A]

  def simpleCodec[A, A1 : Decoder : Encoder](encode: A => A1, decode: A1 => A): Codec[A] = new Decoder[A] with Encoder[A] {
    override def apply(c: HCursor): Result[A] = c.as[A1].map(decode)

    override def apply(a: A): Json = encode(a).asJson
  }

  def partialDecoder[T_INTERMEDIATE : Decoder, A: ClassTag](pf: PartialFunction[T_INTERMEDIATE, A]): Decoder[A] =
    partialLiftedDecoder(pf.lift)

  def partialLiftedDecoder[T_INTERMEDIATE : Decoder, A: ClassTag](pfLifted: Function[T_INTERMEDIATE, Option[A]]): Decoder[A] =
    implicitly[Decoder[T_INTERMEDIATE]].emap { asString =>
      pfLifted.apply(asString) match {
        case Some(decoded) => Right(decoded)
        case None => Left(s"""Unable to parse value "$asString" to ${implicitly[ClassTag[A]].runtimeClass.getName}""")
      }
    }

  def partialLiftedCodec[A : ClassTag, T_INTERMEDIATE : Decoder : Encoder](
                                                                            encode: A => T_INTERMEDIATE,
                                                                            decode: T_INTERMEDIATE => Option[A],
                                                                          ): Codec[A] = new Decoder[A] with Encoder[A] {
    private val decoder = partialLiftedDecoder(decode)

    override def apply(c: HCursor): Result[A] = decoder.apply(c)

    override def apply(a: A): Json = encode(a).asJson
  }

  def partialCodec[A : ClassTag, T_INTERMEDIATE : Decoder : Encoder](
                                                                      encode: A => T_INTERMEDIATE,
                                                                      decode: PartialFunction[T_INTERMEDIATE, A],
                                                                    ): Codec[A] = partialLiftedCodec(encode, decode.lift)

}

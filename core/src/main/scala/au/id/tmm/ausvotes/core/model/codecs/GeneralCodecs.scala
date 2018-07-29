package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Name
import au.id.tmm.utilities.geo.australia.State

import scala.reflect.ClassTag

object GeneralCodecs {

  private def codecUsing[A : ClassTag](encode: A => String, decode: String => Option[A]): CodecJson[A] = CodecJson[A](
    encoder = a => jString(encode(a)),
    decoder = cursor => cursor.as[String].flatMap { json =>
      val resolvedEntity = decode(json)

      resolvedEntity match {
        case Some(election) => DecodeResult.ok(election)
        case None => DecodeResult.fail(s"Could not resolve ${implicitly[ClassTag[A]].runtimeClass.getSimpleName} '$json'", cursor.history)
      }
    }
  )

  implicit val senateElectionCodec: CodecJson[SenateElection] = codecUsing(encode = _.id, decode = SenateElection.forId)

  implicit val stateCodec: CodecJson[State] = codecUsing(encode = _.abbreviation, decode = State.fromAbbreviation)

  implicit val nameCodec: CodecJson[Name] = casecodec2(Name.apply, Name.unapply)("givenNames", "surname")

}

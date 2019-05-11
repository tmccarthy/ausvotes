package au.id.tmm.ausvotes.model.nsw.legco

import au.id.tmm.ausvotes.model.Codecs
import au.id.tmm.ausvotes.model.Codecs.Codec

final case class BallotId(asInt: Int) extends AnyVal

object BallotId {
  implicit val codec: Codec[BallotId] = Codecs.simpleCodec[BallotId, Int](_.asInt, BallotId.apply)
}

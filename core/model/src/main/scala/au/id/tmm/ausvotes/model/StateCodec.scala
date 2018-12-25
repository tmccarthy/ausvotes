package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.Codecs._
import au.id.tmm.utilities.geo.australia.State

object StateCodec {

  implicit val codec: Codec[State] = partialCodec(
    encode = _.abbreviation,
    decode = State.fromAbbreviation,
  )

}

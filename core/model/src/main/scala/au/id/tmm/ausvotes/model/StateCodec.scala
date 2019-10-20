package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.Codecs._
import au.id.tmm.ausgeo.State

object StateCodec {

  implicit val codec: Codec[State] = partialLiftedCodec(
    encode = _.abbreviation,
    decode = State.fromAbbreviation,
  )

}

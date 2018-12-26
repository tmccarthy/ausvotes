package au.id.tmm.ausvotes.model.instances

import au.id.tmm.ausvotes.model.Codecs
import au.id.tmm.ausvotes.model.Codecs.Codec
import au.id.tmm.utilities.geo.australia.State

object StateInstances {

  implicit val codec: Codec[State] = Codecs.partialCodec(_.abbreviation, State.fromAbbreviation)

}

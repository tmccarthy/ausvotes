package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.Codecs.Codec

final case class Party(name: String)

object Party {

  implicit val codec: Codec[Party] = Codecs.simpleCodec[Party, String](encode = _.name, decode = Party(_))

}

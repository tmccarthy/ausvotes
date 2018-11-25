package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}
import au.id.tmm.utilities.collection.DupelessSeq

object DupelessSeqCodec {

  implicit def encodeDupelessSeq[A : EncodeJson]: EncodeJson[DupelessSeq[A]] = seq => seq.toVector.asJson

  implicit def decodeDupelessSeq[A : DecodeJson]: DecodeJson[DupelessSeq[A]] = cursor =>
    cursor.as[List[A]].map(DupelessSeq(_: _*))

}

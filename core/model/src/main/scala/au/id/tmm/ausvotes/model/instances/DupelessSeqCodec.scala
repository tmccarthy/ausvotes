package au.id.tmm.ausvotes.model.instances

import au.id.tmm.utilities.collection.DupelessSeq
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}

object DupelessSeqCodec {

  implicit def encodeDupelessSeq[A : Encoder]: Encoder[DupelessSeq[A]] = seq => seq.toVector.asJson

  implicit def decodeDupelessSeq[A : Decoder]: Decoder[DupelessSeq[A]] = cursor =>
    cursor.as[List[A]].map(DupelessSeq(_: _*))

}

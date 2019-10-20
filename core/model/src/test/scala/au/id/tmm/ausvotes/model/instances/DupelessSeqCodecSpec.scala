package au.id.tmm.ausvotes.model.instances

import au.id.tmm.utilities.collection.DupelessSeq
import org.scalatest.FlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class DupelessSeqCodecSpec extends FlatSpec {

  "the dupeless seq encoder" should "encode a dupeless seq" in {
    assert(
      DupelessSeq("the", "quick", "brown", "fox").asJson ===
        Json.arr("the".asJson, "quick".asJson, "brown".asJson, "fox".asJson)
    )
  }

  "the dupeless seq decoder" should "decode a dupeless seq" in {
    assert(
      Json.arr("the".asJson, "quick".asJson, "brown".asJson, "fox".asJson).as[DupelessSeq[String]] ===
        Right(DupelessSeq("the", "quick", "brown", "fox"))
    )
  }

  it should "decode a dupeless seq with duplicates" in {
    assert(
      Json.arr("the".asJson, "quick".asJson, "brown".asJson, "brown".asJson).as[DupelessSeq[String]] ===
        Right(DupelessSeq("the", "quick", "brown"))
    )
  }

}

package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut.DecodeResult
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DupelessSeqCodecSpec extends ImprovedFlatSpec {

  import DupelessSeqCodec._

  "the dupeless seq encoder" should "encode a dupeless seq" in {
    assert(
      DupelessSeq("the", "quick", "brown", "fox").asJson ===
        jArrayElements("the".asJson, "quick".asJson, "brown".asJson, "fox".asJson)
    )
  }

  "the dupeless seq decoder" should "decode a dupeless seq" in {
    assert(
      jArrayElements("the".asJson, "quick".asJson, "brown".asJson, "fox".asJson).as[DupelessSeq[String]] ===
        DecodeResult.ok(DupelessSeq("the", "quick", "brown", "fox"))
    )
  }

  it should "decode a dupeless seq with duplicates" in {
    assert(
      jArrayElements("the".asJson, "quick".asJson, "brown".asJson, "brown".asJson).as[DupelessSeq[String]] ===
        DecodeResult.ok(DupelessSeq("the", "quick", "brown"))
    )
  }

}

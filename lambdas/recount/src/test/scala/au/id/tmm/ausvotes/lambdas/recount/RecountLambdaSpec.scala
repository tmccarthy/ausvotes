package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import argonaut.DecodeResult
import au.id.tmm.ausvotes.lambdas.recount.RecountLambda.SnsMessage
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountLambdaSpec extends ImprovedFlatSpec {

  "a recount lambda sns message" can "be decoded from json" in {
    val json = jObjectFields(
      "election" -> "2016".asJson,
      "state" -> "VIC".asJson,
      "vacancies" -> "12".asJson,
      "ineligibleCandidates" -> "123,456".asJson,
    )

    val expectedMessage = SnsMessage(
      election = Some("2016"),
      state = Some("VIC"),
      vacancies = Some("12"),
      ineligibleCandidates = Some("123,456"),
    )

    assert(json.as[SnsMessage] === DecodeResult.ok(expectedMessage))
  }

}

package au.id.tmm.ausvotes.api.utils.unfiltered

import argonaut.Argonaut._
import argonaut.EncodeJson
import au.id.tmm.ausvotes.api.MockResponse
import au.id.tmm.ausvotes.api.utils.unfiltered.ResponseJsonSpec.TestObject
import au.id.tmm.http_constants.HttpHeader
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ResponseJsonSpec extends ImprovedFlatSpec {

  "the json response function" should "encode an object with an encoder" in {
    val response = MockResponse.from(ResponseJson[TestObject](TestObject("hello", 5)))

    assert(response.content === """{"string":"hello","int":5}""")
  }

  it should "add a json content header" in {
    val response = MockResponse.from(ResponseJson[TestObject](TestObject("hello", 5)))

    assert(response.headers(HttpHeader.ContentType) contains "application/json; charset=utf-8")
  }

}

object ResponseJsonSpec {
  final case class TestObject(string: String, int: Int)

  object TestObject {
    implicit val encodeTestObject: EncodeJson[TestObject] =
      casecodec2(TestObject.apply, TestObject.unapply)("string", "int")
  }
}

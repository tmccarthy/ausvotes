package au.id.tmm.ausvotes.api.utils.unfiltered

import au.id.tmm.ausvotes.api.MockResponse
import au.id.tmm.ausvotes.api.utils.unfiltered.ResponseJsonSpec.TestObject
import au.id.tmm.http_constants.HttpHeader
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Encoder

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
    implicit val encodeTestObject: Encoder[TestObject] = Encoder.forProduct2("string", "int")(t => (t.string, t.int))
  }
}

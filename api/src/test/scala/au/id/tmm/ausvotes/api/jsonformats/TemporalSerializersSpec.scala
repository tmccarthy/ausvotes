package au.id.tmm.ausvotes.api.jsonformats

import java.time.{LocalDate, LocalDateTime}

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.json4s.{Extraction, JObject, JString}

class TemporalSerializersSpec extends ImprovedFlatSpec {

  implicit val formats = ApiFormats

  "the local date serializer" should "serialise a local date to a string" in {

    val date = LocalDate.of(2017, 10, 28)

    val json = Extraction.decompose(date)

    assert(json === JString("2017-10-28"))
  }

  it should "not serialise a datetime" in {

    val dateTime = LocalDateTime.of(2017, 10, 28, 13, 22, 25)

    val json = Extraction.decompose(dateTime)

    assert(json === JObject(List()))
  }

  it should "deserialise a date string" in {

    val json = Extraction.extract[LocalDate](JString("2017-10-28"))

    assert(json === LocalDate.of(2017, 10, 28))
  }
}

package au.id.tmm.ausvotes.core.logging

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class LoggerSpec extends ImprovedFlatSpec {

  "the logger format" should "write the event id" in {
    val actualFormat = Logger.format("EVENT_ID", Vector())

    assert(actualFormat === "event_id=EVENT_ID")
  }

  it should "escape the event id as needed" in {
    val actualFormat = Logger.format("EVENT_ID\"", Vector())

    assert(actualFormat === """event_id="EVENT_ID\""""")
  }

  it should "escape null values" in {
    val actualFormat = Logger.format("EVENT_ID", Vector("a" -> null))

    assert(actualFormat === """event_id=EVENT_ID; a=<null>""")
  }

  it should "quote the null representation" in {
    val actualFormat = Logger.format("EVENT_ID", Vector("a" -> "<null>"))

    assert(actualFormat === """event_id=EVENT_ID; a="<null>"""")
  }

  it should "escape the escape char" in {
    val actualFormat = Logger.format("EVENT_ID", Vector("a" -> "\\"))

    assert(actualFormat === """event_id=EVENT_ID; a="\\"""")
  }

  it should "escape the separator char" in {
    val actualFormat = Logger.format("EVENT_ID", Vector("a" -> ";"))

    assert(actualFormat === """event_id=EVENT_ID; a="\;"""")
  }

  it should "quote strings with whitespace" in {
    val actualFormat = Logger.format("EVENT_ID", Vector("a" -> "a string"))

    assert(actualFormat === """event_id=EVENT_ID; a="a string"""")
  }

}

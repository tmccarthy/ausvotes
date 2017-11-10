package au.id.tmm.ausvotes.core.logging

import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.collection.mutable.ArrayBuffer

class LoggedEventSpec extends ImprovedFlatSpec {

  "a logged event" can "be constructed with an id" in {
    val loggedEvent = LoggedEvent("EVENT_ID")

    assert(loggedEvent.eventId === "EVENT_ID")
  }

  it can "be constructed with an id and some key value pairs" in {
    val loggedEvent = LoggedEvent("EVENT_ID", "a" -> 1, "b" -> 2)

    assert(loggedEvent.eventId === "EVENT_ID")
    assert(loggedEvent.kvPairs === ArrayBuffer("a" -> 1, "b" -> 2))
  }

  it can "have a key value pair appended" in {
    val loggedEvent = LoggedEvent("EVENT_ID", "a" -> 1, "b" -> 2)

    loggedEvent.kvPairs += ("c" -> 3)

    assert(loggedEvent.kvPairs === ArrayBuffer("a" -> 1, "b" -> 2, "c" -> 3))
  }

  it can "be marked as success" in {
    val loggedEvent = LoggedEvent("EVENT_ID")

    loggedEvent.markSuccessful()

    assert(loggedEvent.kvPairs === ArrayBuffer("successful" -> true))
  }

  it can "be marked as failed" in {
    val loggedEvent = LoggedEvent("EVENT_ID")

    loggedEvent.markFailed()

    assert(loggedEvent.kvPairs === ArrayBuffer("successful" -> false))
  }

  it can "have an exception associated with it" in {
    val loggedEvent = LoggedEvent("EVENT_ID")

    val exception = new RuntimeException

    loggedEvent.exception = Some(exception)

    assert(loggedEvent.exception contains exception)
  }

}
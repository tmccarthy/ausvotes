package au.id.tmm.ausvotes.model

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class PreferenceSpec extends ImprovedFlatSpec {

  "a numbered preference" can "be encoded to json" in {
    assert((Preference.Numbered(42): Preference).asJson === Json.fromInt(42))
  }

  it can "be decoded from json" in {
    assert(Json.fromInt(42).as[Preference] === Right(Preference.Numbered(42)))
  }

  "a ticked preference" can "be encoded to json" in {
    assert((Preference.Tick: Preference).asJson === Json.fromString("✓"))
  }

  it can "be decoded from json" in {
    assert(Json.fromString("✓").as[Preference] === Right(Preference.Tick))
  }

  "a crossed preference" can "be encoded to json" in {
    assert((Preference.Cross: Preference).asJson === Json.fromString("x"))
  }

  it can "be decoded from json" in {
    assert(Json.fromString("x").as[Preference] === Right(Preference.Cross))
  }

  "an invalid preference" should "fail to decode" in {
    assert(Json.fromString("invalid").as[Preference].left.map(_.message) === Left("""Invalid preference "invalid""""))
  }

}

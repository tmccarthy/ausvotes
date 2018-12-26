package au.id.tmm.ausvotes.model.instances

import au.id.tmm.ausvotes.model.instances.RationalCodec._
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.syntax.EncoderOps
import spire.math.Rational

class RationalCodecSpec extends ImprovedFlatSpec {

  "the rational encoder" can "encode a rational number" in {
    assert(Rational(1, 3).asJson === "1/3".asJson)
  }

  it can "encode a rational number with a divisor of 1" in {
    assert(Rational(4, 1).asJson === "4".asJson)
  }

  "the rational decoder" can "decode a rational number" in {
    assert("1/3".asJson.as[Rational] === Right(Rational(1, 3)))
  }

  it can "decode a rational number with a divisor of 1" in {
    assert("4".asJson.as[Rational] === Right(Rational(4)))
  }

  it should "fail to decode an invalid rational" in {
    assert("a/1".asJson.as[Rational].left.map(_.message) === Left("For input string: \"a\""))
  }

  it should "fail to decode rational with too many slashes" in {
    assert("1/2/3".asJson.as[Rational].left.map(_.message) === Left("Invalid rational 1/2/3"))
  }

}

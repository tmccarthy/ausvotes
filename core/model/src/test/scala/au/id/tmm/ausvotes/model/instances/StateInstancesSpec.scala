package au.id.tmm.ausvotes.model.instances

import au.id.tmm.ausvotes.model.instances.StateInstances.codec
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class StateInstancesSpec extends ImprovedFlatSpec {

  "the state codec" should "encode a state" in {
    val state: State = State.SA

    assert(state.asJson === Json.fromString("SA"))
  }

  it should "decode a state" in {
    val json = Json.fromString("SA")

    assert(json.as[State] === Right(State.SA))
  }

  it should "fail to decode if the election is not recognised" in {
    val json = Json.fromString("invalid")

    assert(json.as[State].isLeft)
  }

  "ordering by state size" should "be correct" in {
    val expectedOrdering = List(
      State.NT,
      State.ACT,
      State.TAS,
      State.SA,
      State.WA,
      State.QLD,
      State.VIC,
      State.NSW,
    )

    assert(State.ALL_STATES.toList.sorted(StateInstances.orderStatesByPopulation) === expectedOrdering)
  }

}

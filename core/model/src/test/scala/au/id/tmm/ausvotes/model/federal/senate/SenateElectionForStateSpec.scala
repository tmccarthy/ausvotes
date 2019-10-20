package au.id.tmm.ausvotes.model.federal.senate

import au.id.tmm.ausvotes.model.StateCodec.codec
import au.id.tmm.ausgeo.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class SenateElectionForStateSpec extends ImprovedFlatSpec {

  "a Senate election for a state" can "not be created invalidly" in {
    assert(SenateElectionForState(SenateElection.`2014 WA`, State.VIC) ===
      Left(SenateElectionForState.NoElectionForState(SenateElection.`2014 WA`, State.VIC)))
  }

  it can "be encoded to json" in {
    val electionForState = SenateElectionForState(SenateElection.`2016`, State.VIC).right.get

    val json = Json.obj(
      "election" -> (SenateElection.`2016`: SenateElection).asJson,
      "state" -> (State.VIC: State).asJson,
    )

    assert(electionForState.asJson === json)
  }

  it can "be decoded from json" in {
    val electionForState = SenateElectionForState(SenateElection.`2016`, State.VIC).right.get

    val json = Json.obj(
      "election" -> (SenateElection.`2016`: SenateElection).asJson,
      "state" -> (State.VIC: State).asJson,
    )

    assert(json.as[SenateElectionForState] === Right(electionForState))
  }

  it can "not be decoded from json if the election state combination is invalid" in {
    val json = Json.obj(
      "election" -> (SenateElection.`2014 WA`: SenateElection).asJson,
      "state" -> (State.VIC: State).asJson,
    )

    assert(json.as[SenateElectionForState].left.map(_.message) === Left("No election for VIC at 2014 WA Senate election"))
  }

  it can "be ordered" in {
    val list = List(
      SenateElectionForState(SenateElection.`2014 WA`, State.WA),
      SenateElectionForState(SenateElection.`2016`, State.NSW),
      SenateElectionForState(SenateElection.`2016`, State.NT),
      SenateElectionForState(SenateElection.`2013`, State.VIC),
    ).map(_.right.get)

    val expectedOrdered = List(
      SenateElectionForState(SenateElection.`2016`, State.NSW),
      SenateElectionForState(SenateElection.`2016`, State.NT),
      SenateElectionForState(SenateElection.`2014 WA`, State.WA),
      SenateElectionForState(SenateElection.`2013`, State.VIC),
    ).map(_.right.get)

    assert(list.sorted === expectedOrdered)
  }

  "the numVacancies computation" should "indicate the number of vacancies for a state at a normal election" in {
    assert(SenateElection.`2013`.electionForState(State.SA).get.numVacancies === 6)
  }

  it should "indicate the number of vacancies for a territory at a normal election" in {
    assert(SenateElection.`2013`.electionForState(State.NT).get.numVacancies === 2)
  }

  it should "indicate the number of vacancies for a state at a double dissolution election" in {
    assert(SenateElection.`2016`.electionForState(State.WA).get.numVacancies === 12)
  }

  it should "indicate the number of vacancies for a territory at a double dissolution election" in {
    assert(SenateElection.`2016`.electionForState(State.ACT).get.numVacancies === 2)
  }


}

package au.id.tmm.ausvotes.model.federal.senate

import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class SenateElectionSpec extends ImprovedFlatSpec {

  "a senate election" should "have a name" in {
    assert(SenateElection.`2016`.name === "2016 election")
  }

  it should "be ordered by its date" in {
    val elections = List[SenateElection](
      SenateElection.`2007`,
      SenateElection.`2004`,
      SenateElection.`2016`,
      SenateElection.`2013`,
      SenateElection.`2010`,
      SenateElection.`2014 WA`,
    )

    val orderedElections = List[SenateElection](
      SenateElection.`2004`,
      SenateElection.`2007`,
      SenateElection.`2010`,
      SenateElection.`2013`,
      SenateElection.`2014 WA`,
      SenateElection.`2016`,
    )

    assert(elections.sorted === orderedElections)
  }

  it can "be encoded to json" in {
    assert((SenateElection.`2016`: SenateElection).asJson === Json.fromString("2016"))
  }

  it can "be decoded from json" in {
    assert(Json.fromString("2016").as[SenateElection] === Right(SenateElection.`2016`))
  }

  it can "be looked up by its ID if it is the 2013 election" in {
    assert(SenateElection.from(SenateElection.Id("2013")) === Some(SenateElection.`2013`))
  }

  it can "be looked up by its ID if it is the 2014 WA election" in {
    assert(SenateElection.from(SenateElection.Id("2014WA")) === Some(SenateElection.`2014 WA`))
  }

  it can "be looked up by its ID if it is the 2016 election" in {
    assert(SenateElection.from(SenateElection.Id("2016")) === Some(SenateElection.`2016`))
  }

  it can "construct the election for a state" in {
    assert(SenateElection.`2016`.electionForState(State.VIC) === Some(SenateElectionForState.makeUnsafe(SenateElection.`2016`, State.VIC)))
  }

  it should "not construct the election for a state that did not have an election" in {
    assert(SenateElection.`2014 WA`.electionForState(State.VIC) === None)
  }

}

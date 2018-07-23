package au.id.tmm.ausvotes.core.model

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class SenateElectionSpec extends ImprovedFlatSpec {

  "a senate election" can "be looked up by its ID if it is the 2013 election" in {
    assert(SenateElection.forId("2013") === Some(SenateElection.`2013`))
  }

  it can "be looked up by its ID if it is the 2014 WA election" in {
    assert(SenateElection.forId("2014WA") === Some(SenateElection.`2014 WA`))
  }

  it can "be looked up by its ID if it is the 2016 election" in {
    assert(SenateElection.forId("2016") === Some(SenateElection.`2016`))
  }

}

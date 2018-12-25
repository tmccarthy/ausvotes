package au.id.tmm.ausvotes.model.federal

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class FederalElectionSpec extends ImprovedFlatSpec {

  "a federal election" should "have a name" in {
    assert(FederalElection.`2016`.name === "2016 election")
  }

  it should "be ordered by date" in {
    val elections = List[FederalElection](
      FederalElection.`2007`,
      FederalElection.`2004`,
      FederalElection.`2016`,
      FederalElection.`2013`,
      FederalElection.`2010`,
      FederalElection.`2014 WA`,
    )

    val orderedElections = List[FederalElection](
      FederalElection.`2004`,
      FederalElection.`2007`,
      FederalElection.`2010`,
      FederalElection.`2013`,
      FederalElection.`2014 WA`,
      FederalElection.`2016`,
    )

    assert(elections.sorted === orderedElections)
  }

}

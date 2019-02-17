package au.id.tmm.ausvotes.core.computations.ballotnormalisation

import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallot, SenateElection}
import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

// Section references in this spec refer to the Commonwealth Electoral Act 1918
class BallotNormaliserSpec extends ImprovedFlatSpec {

  private val ballots = BallotFixture.ACT

  private val candidates = CandidateFixture.ACT.candidates

  private val sut = BallotNormaliser.forSenate(SenateElection.`2016`.electionForState(State.ACT).get, candidates)

  import ballots._
  import ballots.ballotMaker._

  // Section 269(2)
  "normalising either atl or btl preferences" should "use the btl preferences if they are formal" in {
    val ballot: SenateBallot = formalAtlAndBtl

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.isNormalisedToBtl)
  }

  // Section 269(2)
  it should "use the atl preferences if the btl preferences are informal" in {
    val ballot = formalAtlInformalBtl

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.isNormalisedToAtl)
  }

  // Section 272
  "normalising atl preferences" should "distribute preferences to members of the group" in {
    val ballot: SenateBallot = formalAtl

    val normalisedBallot = sut.normalise(ballot)

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 269(1)(b)
  it should "mark a ballot as formal if at least 1 square is numbered with 1" in {
    val ballot: SenateBallot = oneAtl

    val expectedOrder = candidateOrder("A0", "A1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 269(1A)(a)
  it should "consider a tick in a square as equivalent to marking 1 in that square" in {
    val ballot: SenateBallot = tickedAtl

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 269(1A)(a)
  it should "consider a cross in a square as equivalent to marking 1 in that square" in {
    val ballot: SenateBallot = crossedAtl

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 269(1A)(b)
  it should "mark a ballot as formal if it has repeated numbers after 1" in {
    val ballot: SenateBallot = atlWithRepeatedNumbers

    val expectedOrder = candidateOrder("A0", "A1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 269(1A)(b)
  it should "mark a ballot as formal if it has missed numbers after 1" in {
    val ballot: SenateBallot = atlMissedNumbers

    val expectedOrder = candidateOrder("A0", "A1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 269(1A)(b)
  it should "mark a ballot as informal if number 1 is repeated" in {
    val ballot: SenateBallot = atl1Repeated

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder.isEmpty)
  }

  it should "correctly count formal preferences when the ballot contains a counting error" in {
    val ballot: SenateBallot = atlWithRepeatedNumbers

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.atl.normalisedBallotIfFormal.map(_.size).getOrElse(0) === 1)
  }

  "normalising btl preferences" should "reproduce the preferences expressed below the line" in {
    val ballot: SenateBallot = formalBtl

    val expectedOrder = candidateOrder("A0", "B1", "B0", "J0", "UG0", "UG1", "A1", "I0", "C1", "D0", "D1", "E0", "E1", "F1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 268A(1)(b)
  it should "mark a ballot as formal if at least the numbers from 1 to 6 have been marked without repetition" in {
    val ballot: SenateBallot = sixNumberedBtl

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 268A(1)(b)
  it should "mark a ballot as informal if a number between 1 and 6 has been repeated" in {
    val ballot: SenateBallot = btlRepeatedNumberBelow6

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder.isEmpty)
  }

  // Section 268A(1)(b)
  it should "mark a ballot as informal if a number between 1 and 6 has been missed" in {
    val ballot: SenateBallot = btlMissedNumberBelow6

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder.isEmpty)
  }

  // Section 268A(2)(a)
  it should "consider a tick in a square as equivalent to marking 1 in that square" in {
    val ballot: SenateBallot = tickedBtl

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 268A(2)(a)
  it should "consider a cross in a square as equivalent to marking 1 in that square" in {
    val ballot: SenateBallot = crossedBtl

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 268A(2)(b)
  it should "mark a ballot as formal if it has repeated numbers after 6" in {
    val ballot: SenateBallot = btlRepeatedNumberAfter6

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  // Section 268A(2)(b)
  it should "mark a ballot as formal if it has missed numbers after 6" in {
    val ballot: SenateBallot = btlMissedNumberAfter6

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === Some(expectedOrder))
  }

  it should "correctly count formal preferences when the ballot contains a counting error" in {
    val ballot: SenateBallot = btlMissedNumberAfter6

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.btl.normalisedBallotIfFormal.map(_.size).getOrElse(0) === 6)
  }
}

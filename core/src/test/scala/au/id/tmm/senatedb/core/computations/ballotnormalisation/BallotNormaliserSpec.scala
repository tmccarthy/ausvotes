package au.id.tmm.senatedb.core.computations.ballotnormalisation

import au.id.tmm.senatedb.core.fixtures._
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Ballot
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

// Section references in this spec refer to the Commonwealth Electoral Act 1918
class BallotNormaliserSpec extends ImprovedFlatSpec {

  private val ballots = Ballots.ACT

  private val candidates = Candidates.ACT.candidates

  private val sut = BallotNormaliser(SenateElection.`2016`, State.ACT, candidates)

  import ballots._
  import ballots.ballotMaker._

  // Section 269(2)
  "normalising either atl or btl preferences" should "use the btl preferences if they are formal" in {
    val ballot: Ballot = formalAtlAndBtl

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
    val ballot: Ballot = formalAtl

    val normalisedBallot = sut.normalise(ballot)

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1)(b)
  it should "mark a ballot as formal if at least 1 square is numbered with 1" in {
    val ballot: Ballot = oneAtl

    val expectedOrder = candidateOrder("A0", "A1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1A)(a)
  it should "consider a tick in a square as equivalent to marking 1 in that square" in {
    val ballot: Ballot = tickedAtl

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1A)(a)
  it should "consider a cross in a square as equivalent to marking 1 in that square" in {
    val ballot: Ballot = crossedAtl

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1A)(b)
  it should "mark a ballot as formal if it has repeated numbers after 1" in {
    val ballot: Ballot = atlWithRepeatedNumbers

    val expectedOrder = candidateOrder("A0", "A1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1A)(b)
  it should "mark a ballot as formal if it has missed numbers after 1" in {
    val ballot: Ballot = atlMissedNumbers

    val expectedOrder = candidateOrder("A0", "A1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1A)(b)
  it should "mark a ballot as informal if number 1 is repeated" in {
    val ballot: Ballot = atl1Repeated

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.isInformal)
  }

  it should "correctly count formal preferences when the ballot contains a counting error" in {
    val ballot: Ballot = atlWithRepeatedNumbers

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.atlFormalPreferenceCount === 1)
  }

  "normalising btl preferences" should "reproduce the preferences expressed below the line" in {
    val ballot: Ballot = formalBtl

    val expectedOrder = candidateOrder("A0", "B1", "B0", "J0", "UG0", "UG2", "A1", "I0", "C1", "D0", "D1", "E0", "E1", "F1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 268A(1)(b)
  it should "mark a ballot as formal if at least the numbers from 1 to 6 have been marked without repetition" in {
    val ballot: Ballot = sixNumberedBtl

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 268A(1)(b)
  it should "mark a ballot as informal if a number between 1 and 6 has been repeated" in {
    val ballot: Ballot = btlRepeatedNumberBelow6

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.isInformal)
  }

  // Section 268A(1)(b)
  it should "mark a ballot as informal if a number between 1 and 6 has been missed" in {
    val ballot: Ballot = btlMissedNumberBelow6

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.isInformal)
  }

  // Section 268A(2)(a)
  it should "consider a tick in a square as equivalent to marking 1 in that square" in {
    val ballot: Ballot = tickedBtl

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 268A(2)(a)
  it should "consider a cross in a square as equivalent to marking 1 in that square" in {
    val ballot: Ballot = crossedBtl

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 268A(2)(b)
  it should "mark a ballot as formal if it has repeated numbers after 6" in {
    val ballot: Ballot = btlRepeatedNumberAfter6

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 268A(2)(b)
  it should "mark a ballot as formal if it has missed numbers after 6" in {
    val ballot: Ballot = btlMissedNumberAfter6

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  it should "correctly count formal preferences when the ballot contains a counting error" in {
    val ballot: Ballot = btlMissedNumberAfter6

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.btlFormalPreferenceCount === 6)
  }
}

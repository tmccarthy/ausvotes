package au.id.tmm.senatedb.computations.ballotnormalisation

import au.id.tmm.senatedb.fixtures.{BallotMaker, Candidates, Divisions, PollingPlaces}
import au.id.tmm.senatedb.model.parsing.Ballot.{AtlPreferences, BtlPreferences}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

// Section references in this spec refer to the Commonwealth Electoral Act 1918
class BallotNormaliserSpec extends ImprovedFlatSpec {

  private val candidates = Candidates.ACT.candidates

  private val ballotMaker = BallotMaker(Candidates.ACT)

  private val sut = BallotNormaliser.forCandidates(candidates)

  import ballotMaker._

  private def makeBallot(atlPrefs: AtlPreferences,
                         btlPrefs: BtlPreferences
                        ) = ballotMaker.makeBallot(atlPrefs, btlPrefs, Divisions.ACT.CANBERRA, PollingPlaces.ACT.BARTON)

  // Section 269(2)
  "normalising either atl or btl preferences" should "use the btl preferences if they are formal" in {
    val atlPrefs = orderedAtlPreferences("A", "B", "C", "D", "E", "F")
    val btlPrefs = orderedBtlPreferences("J0", "J1", "I0", "I1", "H0", "H1", "G0", "G1", "F0", "F1", "E0", "E1")

    val ballot = makeBallot(atlPrefs, btlPrefs)

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === normalisedBallot.btlCandidateOrder)
  }

  // Section 269(2)
  it should "use the atl preferences if the btl preferences are informal" in {
    val atlPrefs = orderedAtlPreferences("A", "B", "C", "D", "E", "F")
    val btlPrefs = btlPreferences("J0" -> "42")

    val ballot = makeBallot(atlPrefs, btlPrefs)

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === normalisedBallot.atlCandidateOrder)
  }

  // Section 272
  "normalising atl preferences" should "distribute preferences to members of the group" in {
    val atlPrefs = orderedAtlPreferences("A", "B", "C", "D", "E", "F")

    val ballot = makeBallot(atlPrefs, Map.empty)

    val normalisedBallot = sut.normalise(ballot)

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1)(b)
  it should "mark a ballot as formal if at least 1 square is numbered with 1" in {
    val atlPrefs = orderedAtlPreferences("A")
    val ballot = makeBallot(atlPrefs, Map.empty)

    val expectedOrder = candidateOrder("A0", "A1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1A)(a)
  it should "consider a tick in a square as equivalent to marking 1 in that square" in {
    val atlPrefs = atlPreferences("A" -> "/", "B" -> "2", "C" -> "3", "D" -> "4", "E" -> "5", "F" -> "6")
    val ballot = makeBallot(atlPrefs, Map.empty)

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1A)(a)
  it should "consider a cross in a square as equivalent to marking 1 in that square" in {
    val atlPrefs = atlPreferences("A" -> "*", "B" -> "2", "C" -> "3", "D" -> "4", "E" -> "5", "F" -> "6")
    val ballot = makeBallot(atlPrefs, Map.empty)

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1A)(b)
  it should "mark a ballot as formal if it has repeated numbers after 1" in {
    val atlPrefs = atlPreferences("A" -> "1", "B" -> "2", "C" -> "2", "D" -> "3", "E" -> "4", "F" -> "5", "G" -> "6")
    val ballot = makeBallot(atlPrefs, Map.empty)

    val expectedOrder = candidateOrder("A0", "A1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1A)(b)
  it should "mark a ballot as formal if it has missed numbers after 1" in {
    val atlPrefs = atlPreferences("A" -> "1", "B" -> "3", "C" -> "4", "D" -> "5", "E" -> "6", "F" -> "7")
    val ballot = makeBallot(atlPrefs, Map.empty)

    val expectedOrder = candidateOrder("A0", "A1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 269(1A)(b)
  it should "mark a ballot as informal if number 1 is repeated" in {
    val atlPrefs = atlPreferences("A" -> "1", "B" -> "1", "C" -> "2", "D" -> "3", "E" -> "4", "F" -> "5", "G" -> "6")
    val ballot = makeBallot(atlPrefs, Map.empty)

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.isInformal)
  }

  it should "correctly count formal preferences when the ballot contains a counting error" in {
    val atlPrefs = atlPreferences("A" -> "1", "B" -> "2", "C" -> "2", "D" -> "3", "E" -> "4", "F" -> "5", "G" -> "6")
    val ballot = makeBallot(atlPrefs, Map.empty)

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.atlFormalPreferenceCount === 1)
  }

  "normalising btl preferences" should "reproduce the preferences expressed below the line" in {
    val btlPrefs = orderedBtlPreferences("A0", "B1", "B0", "J0", "UG0", "UG2", "A1", "I0", "C1", "D0", "D1", "E0", "E1", "F1")
    val ballot = makeBallot(Map.empty, btlPrefs)

    val expectedOrder = candidateOrder("A0", "B1", "B0", "J0", "UG0", "UG2", "A1", "I0", "C1", "D0", "D1", "E0", "E1", "F1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 268A(1)(b)
  it should "mark a ballot as formal if at least the numbers from 1 to 6 have been marked without repetition" in {
    val btlPrefs = orderedBtlPreferences("A0", "A1", "B0", "B1", "C0", "C1")
    val ballot = makeBallot(Map.empty, btlPrefs)

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 268A(1)(b)
  it should "mark a ballot as informal if a number between 1 and 6 has been repeated" in {
    val btlPrefs = btlPreferences("A0" -> "1", "A1" -> "2", "B0" -> "3", "B1" -> "3", "C0" -> "4", "C1" -> "5", "D0" -> "6")
    val ballot = makeBallot(Map.empty, btlPrefs)

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.isInformal)
  }

  // Section 268A(1)(b)
  it should "mark a ballot as informal if a number between 1 and 6 has been missed" in {
    val btlPrefs = btlPreferences("A0" -> "1", "A1" -> "2", "B0" -> "3", "B1" -> "5", "C0" -> "6", "C1" -> "7")
    val ballot = makeBallot(Map.empty, btlPrefs)

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.isInformal)
  }

  // Section 268A(2)(a)
  it should "consider a tick in a square as equivalent to marking 1 in that square" in {
    val btlPrefs = btlPreferences("A0" -> "/", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6")
    val ballot = makeBallot(Map.empty, btlPrefs)

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 268A(2)(a)
  it should "consider a cross in a square as equivalent to marking 1 in that square" in {
    val btlPrefs = btlPreferences("A0" -> "*", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6")
    val ballot = makeBallot(Map.empty, btlPrefs)

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 268A(2)(b)
  it should "mark a ballot as formal if it has repeated numbers after 6" in {
    val btlPrefs = btlPreferences("A0" -> "*", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6", "D0" -> "7", "D1" -> "7")
    val ballot = makeBallot(Map.empty, btlPrefs)

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  // Section 268A(2)(b)
  it should "mark a ballot as formal if it has missed numbers after 6" in {
    val btlPrefs = btlPreferences("A0" -> "*", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6", "D0" -> "8")
    val ballot = makeBallot(Map.empty, btlPrefs)

    val expectedOrder = candidateOrder("A0", "A1", "B0", "B1", "C0", "C1")
    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.canonicalOrder === expectedOrder)
  }

  it should "correctly count formal preferences when the ballot contains a counting error" in {
    val btlPrefs = btlPreferences("A0" -> "*", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6", "D0" -> "8")
    val ballot = makeBallot(Map.empty, btlPrefs)

    val normalisedBallot = sut.normalise(ballot)

    assert(normalisedBallot.btlFormalPreferenceCount === 6)
  }
}

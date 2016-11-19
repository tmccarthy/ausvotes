package au.id.tmm.senatedb.model.computation.flow

import au.id.tmm.senatedb.fixtures.Ballots.ACT
import au.id.tmm.senatedb.model.parsing.Group
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PreferenceFlowSpec extends ImprovedFlatSpec {

  private val ballotMaker = ACT.ballotMaker

  import ballotMaker.group

  private val leftTree1 = PreferenceTree[Group](group("A"), 5, Set())
  private val leftTree2 = PreferenceTree[Group](group("B"), 3, Set())
  private val leftTree3 = PreferenceTree[Group](group("C"), 1, Set())

  private val leftFlow = PreferenceFlow[Group](9, Set(leftTree1, leftTree2, leftTree3))

  private val rightTree1 = PreferenceTree[Group](group("C"), 4, Set())
  private val rightTree2 = PreferenceTree[Group](group("D"), 2, Set())

  private val rightFlow = PreferenceFlow[Group](6, Set(rightTree1, rightTree2))

  "a preference flow" can "be added to another" in {
    val expectedResult = PreferenceFlow[Group](
      15,
      Set(
        PreferenceTree[Group](group("A"), 5, Set()),
        PreferenceTree[Group](group("B"), 3, Set()),
        PreferenceTree[Group](group("C"), 5, Set()),
        PreferenceTree[Group](group("D"), 2, Set())
      )
    )

    assert(expectedResult === leftFlow + rightFlow)
  }

  it should "return itself if summed with an empty flow" in {
    assert((leftFlow + PreferenceFlow.empty) eq leftFlow)
  }

  it should "return the other flow when summed if it is empty" in {
    assert((PreferenceFlow.empty + rightFlow) eq rightFlow)
  }
}

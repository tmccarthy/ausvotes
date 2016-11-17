package au.id.tmm.senatedb.model.computation.flow

import au.id.tmm.senatedb.fixtures.Ballots.ACT
import au.id.tmm.senatedb.model.parsing.Group
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PreferenceTreeSpec extends ImprovedFlatSpec {

  private val ballotMaker = ACT.ballotMaker

  import ballotMaker.group

  private val subleaf1 = PreferenceTree[Group](group("I"), 1, Set())

  private val leaf1 = PreferenceTree[Group](group("A"), 3, Set())
  private val leaf2 = PreferenceTree[Group](group("B"), 2, Set(subleaf1))
  private val leaf3 = PreferenceTree[Group](group("C"), 1, Set())

  private val branch1 = PreferenceTree[Group](group("C"), 5, Set(leaf1, leaf2))
  private val branch2 = PreferenceTree[Group](group("A"), 1, Set(leaf3))

  private val testTree1 = PreferenceTree[Group](group("D"), 6, Set(branch1, branch2))

  private val leaf4 = PreferenceTree[Group](group("D"), 5, Set())
  private val branch3 = PreferenceTree[Group](group("B"), 15, Set(leaf4))
  private val testTree2 = PreferenceTree[Group](group("A"), 20, Set(branch3))

  "a preference tree" can "be constructed with a builder" in {
    val builder = PreferenceTree.Builder[Group]()

    builder += Vector(group("D"), group("A"), group("C"))

    builder += Vector(group("D"), group("C"), group("B"))
    builder += Vector(group("D"), group("C"), group("B"), group("I"))

    builder += Vector(group("D"), group("C"), group("A"))
    builder += Vector(group("D"), group("C"), group("A"))
    builder += Vector(group("D"), group("C"), group("A"))

    assert(builder.result() === testTree1)
  }

  it can "be summed with another preference tree with the same root key" in {
    val sum = testTree1 + PreferenceTree[Group](group("D"), 2, Set(leaf2))

    val expectedSumBuilder = PreferenceTree.Builder[Group]()

    expectedSumBuilder += Vector(group("D"), group("B"))
    expectedSumBuilder += Vector(group("D"), group("B"), group("I"))

    expectedSumBuilder += Vector(group("D"), group("A"), group("C"))

    expectedSumBuilder += Vector(group("D"), group("C"), group("B"))
    expectedSumBuilder += Vector(group("D"), group("C"), group("B"), group("I"))

    expectedSumBuilder += Vector(group("D"), group("C"), group("A"))
    expectedSumBuilder += Vector(group("D"), group("C"), group("A"))
    expectedSumBuilder += Vector(group("D"), group("C"), group("A"))

    assert(sum === expectedSumBuilder.result())
  }

  it can "not be summed with another preference tree with a different root key" in {
    intercept[IllegalArgumentException](testTree1 + testTree2)
  }

  it should "return itself if summed with an empty tree of the same key" in {
    assert((testTree1 + PreferenceTree.emptyFor(testTree1.key)) eq testTree1)
  }

  it should "return the other tree when summed if it is empty" in {
    assert((PreferenceTree.emptyFor(testTree1.key) + testTree1) eq testTree1)
  }

  it can "retrieve the tree at a certain path down the tree" in {
    val path = Vector(group("C"), group("B"))

    assert(testTree1.treeAt(path) === leaf2)
  }

  it should "return an empty tree if the path is not in the tree" in {
    val path = Vector(group("D"), group("E"))

    assert(testTree1.treeAt(path).isEmpty)
  }

  it can "be pruned based on a predicate" in {
    val pruned = testTree1.pruneWhere(_.tally < 3)

    val expectedBuilder = PreferenceTree.Builder[Group]()

    expectedBuilder += Vector(group("D"))

    expectedBuilder += Vector(group("D"), group("C"))
    expectedBuilder += Vector(group("D"), group("C"))

    expectedBuilder += Vector(group("D"), group("C"), group("A"))
    expectedBuilder += Vector(group("D"), group("C"), group("A"))
    expectedBuilder += Vector(group("D"), group("C"), group("A"))

    assert(pruned === expectedBuilder.result())
  }

  it should "reject trees whose tally is less than the sum of its children'" in {
    intercept[IllegalArgumentException] {
      val children = Set(PreferenceTree[Group](group("B"), 1, Set()), PreferenceTree[Group](group("C"), 1, Set()))

      PreferenceTree[Group](group("A"), 1, children)
    }
  }

  it should "reject children with duplicate keys" in {
    intercept[IllegalArgumentException] {
      val children = Set(PreferenceTree[Group](group("B"), 1, Set()), PreferenceTree[Group](group("B"), 2, Set()))

      PreferenceTree[Group](group("A"), 2, children)
    }
  }

  it should "reject reappearances of its key in its children" in {
    intercept[IllegalArgumentException] {
      val children = Set(PreferenceTree[Group](group("A"), 1, Set()), PreferenceTree[Group](group("B"), 1, Set()))

      PreferenceTree[Group](group("A"), 2, children)
    }
  }
}

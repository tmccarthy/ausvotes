package au.id.tmm.senatedb.model

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class GroupUtilsSpec extends ImprovedFlatSpec {

  "the group 'A'" should "have an index '0'" in {
    assert(GroupUtils.indexOfGroup("A") === 0)
  }

  "the group 'C'" should "have an index 2" in {
    assert(GroupUtils.indexOfGroup("C") === 2)
  }

  "the group 'AA'" should "have an index 26" in {
    assert(GroupUtils.indexOfGroup("AC") === 28)
  }

  "the group 'AC'" should "have an index 28" in {
    assert(GroupUtils.indexOfGroup("AC") === 28)
  }

  "the ungrouped" should "have the highest possible index" in {
    assert(GroupUtils.indexOfGroup("UG") === Int.MaxValue)
  }
}

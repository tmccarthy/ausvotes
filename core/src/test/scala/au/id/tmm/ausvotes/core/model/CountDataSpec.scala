package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.core.fixtures.CountDataTestUtils
import au.id.tmm.ausvotes.core.model.CountStep.InitialAllocation
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDataSpec extends ImprovedFlatSpec {

  import CountDataTestUtils.ACT._

  "a count data" should "not return a distribution step for step 1" in {
    intercept[IllegalArgumentException](countData.getDistributionStepForCount(1))
  }

  it should "include the initial allocation in its steps" in {
    assert(countData.steps.exists(_.isInstanceOf[InitialAllocation]))
  }

}

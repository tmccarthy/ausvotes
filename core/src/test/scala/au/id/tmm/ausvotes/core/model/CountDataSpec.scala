package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.core.fixtures.TestsCountData
import au.id.tmm.ausvotes.core.model.CountStep.InitialAllocation
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDataSpec extends ImprovedFlatSpec with TestsCountData {

  "a count data" should "not return a distribution step for step 1" in {
    intercept[IllegalArgumentException](countData.getDistributionStepForCount(1))
  }

  it should "include the initial allocation in its steps" in {
    assert(countData.steps.exists(_.isInstanceOf[InitialAllocation]))
  }

}

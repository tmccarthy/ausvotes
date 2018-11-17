package au.id.tmm.ausvotes.api.controllers

import au.id.tmm.ausvotes.api.model.diagnostics.VersionResponse
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.ausvotes.shared.io.test.testdata.ResourcesTestData
import au.id.tmm.ausvotes.shared.io.test.{BasicTestData, TestIO}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DiagnosticControllerSpec extends ImprovedFlatSpec {

  private def logicUnderTest = DiagnosticController.version[BasicTestIO]

  "the diagnostics route" should "respond with the contents of the version file" in {
    val testData = BasicTestData(resourcesTestData = ResourcesTestData(resources = Map("/version.txt" -> "1.0.0")))

    val TestIO.Output(_, versionOrError) = logicUnderTest.run(testData)

    assert(versionOrError === Right(VersionResponse("1.0.0")))
  }

  it should "respond with a server error if the version file is undefined" in {
    val testData = BasicTestData(resourcesTestData = ResourcesTestData(resources = Map.empty))

    val TestIO.Output(_, versionOrError) = logicUnderTest.run(testData)

    assert(versionOrError.left.map(_.getMessage) === Left("Version file missing"))
  }

}

package au.id.tmm.ausvotes.api.controllers

import au.id.tmm.ausvotes.api.model.diagnostics.VersionResponse
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DiagnosticControllerSpec extends ImprovedFlatSpec {

  private def logicUnderTest = DiagnosticController.version[BasicTestData.TestIO]

  "the diagnostics route" should "respond with the contents of the version file" in {
    val testData = BasicTestData(resources = Map("/version.txt" -> "1.0.0"))

    val (_, versionOrError) = logicUnderTest.run(testData)

    assert(versionOrError === Right(VersionResponse("1.0.0")))
  }

  it should "respond with a server error if the version file is undefined" in {
    val testData = BasicTestData(resources = Map.empty)

    val (_, versionOrError) = logicUnderTest.run(testData)

    assert(versionOrError.left.map(_.getMessage) === Left("Version file missing"))
  }

}

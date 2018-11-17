package au.id.tmm.ausvotes.shared.io.test.testdata

import au.id.tmm.ausvotes.shared.io.actions.Resources
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.Output

final case class ResourcesTestData(
                                    resources: Map[String, String],
                                  )

object ResourcesTestData {

  val empty = ResourcesTestData(Map.empty)

  def testIOInstance[D](resourcesField: D => ResourcesTestData): Resources[TestIO[D, +?, +?]] = new Resources[TestIO[D, +?, +?]] {
    override def resource(name: String): TestIO[D, Nothing, Option[String]] =
      TestIO(data => Output(data, Right(resourcesField(data).resources.get(name))))
  }
}

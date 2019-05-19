package au.id.tmm.ausvotes.shared.io.test.testdata

import java.io.IOException
import java.nio.charset.Charset

import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.Output
import au.id.tmm.bfect.extraeffects.Resources

final case class ResourcesTestData(
                                    resources: Map[String, String],
                                  )

object ResourcesTestData {

  val empty = ResourcesTestData(Map.empty)

  trait TestIOInstance[D] extends Resources[TestIO[D, +?, +?]] {
    protected def resourcesField(data: D): ResourcesTestData

    override def resourceAsString(resourceName: String, charset: Charset): TestIO[D, IOException, Option[String]] =
      TestIO(data => Output(data, Right(resourcesField(data).resources.get(resourceName))))
  }
}

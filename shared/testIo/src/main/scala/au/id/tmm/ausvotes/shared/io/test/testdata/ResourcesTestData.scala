package au.id.tmm.ausvotes.shared.io.test.testdata

import java.io.{ByteArrayInputStream, IOException, InputStream}
import java.nio.charset.Charset

import au.id.tmm.bfect.effects.extra.Resources
import au.id.tmm.bfect.effects.extra.Resources.ResourceStreamError
import au.id.tmm.bfect.testing.BState

final case class ResourcesTestData(
                                    resources: Map[String, String],
                                  )

object ResourcesTestData {

  val empty = ResourcesTestData(Map.empty)

  trait TestIOInstance[D] extends Resources[BState[D, +?, +?]] {
    protected def resourcesField(data: D): ResourcesTestData

    override def getResourceAsStream(resourceName: String): BState[D, Nothing, Option[InputStream]] =
      BState(data => (data, Right(resourcesField(data).resources.get(resourceName).map(s => new ByteArrayInputStream(s.getBytes("UTF-8"))))))

    override def resourceAsString(resourceName: String, charset: Charset): BState[D, ResourceStreamError[IOException], String] =
      BState(data => (data, resourcesField(data).resources.get(resourceName) match {
        case Some(resourceAsString) => Right(resourceAsString)
        case None => Left(ResourceStreamError.ResourceNotFound)
      }))
  }
}

package au.id.tmm.ausvotes.data_sources.common

import java.nio.file.Files

import au.id.tmm.bfect.ziointerop._
import au.id.tmm.utilities.codec.digest._
import au.id.tmm.utilities.codec.binarycodecs._
import au.id.tmm.utilities.testing.NeedsCleanDirectory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import zio.{DefaultRuntime, IO}

import scala.jdk.CollectionConverters._

class OnDiskJsonCacheSpec extends FlatSpec with NeedsCleanDirectory with DefaultRuntime with OneInstancePerTest {

  private val cacheUnderTest = JsonCache.OnDisk[IO](cleanDirectory)

  "the on-disk json cache" should "put a record" in {
    unsafeRun(cacheUnderTest.put("hello", "world"))

    val fileContent = Files.readAllLines(cleanDirectory.resolve("\"hello\"".sha256.asHexString + ".json")).asScala.head

    assert(fileContent === "\"world\"")
  }

  it can "fail to get a missing record" in {
    val possibleValue = unsafeRun(cacheUnderTest.get[String, String]("missing"))

    assert(possibleValue === None)
  }

  it should "retrieve a previously put record" in {
    val possibleValue = unsafeRun {
      for {
        _ <- cacheUnderTest.put("hello", "world")
        value <- cacheUnderTest.get[String, String]("hello")
      } yield value
    }

    assert(possibleValue === Some("world"))
  }

}

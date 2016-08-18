package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.download.TestingRawData
import org.scalatest.Suite

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration._

trait TestingPersistence extends TestingRawData { this: Suite =>

  val persistence = Persistence(Persistence.InMemoryH2("test"), testingRawDataDir)

  override def beforeEach(): Unit = {
    super.beforeEach()
    Await.result(persistence.destroyIfNeeded(), Inf)
    Await.result(persistence.initialise(), Inf)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    Await.result(persistence.destroyIfNeeded(), Inf)
  }

}

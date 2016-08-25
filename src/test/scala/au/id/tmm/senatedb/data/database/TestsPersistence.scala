package au.id.tmm.senatedb.data.database

import org.scalatest.{BeforeAndAfterEach, Suite}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration._

trait TestsPersistence extends BeforeAndAfterEach { this: Suite =>

  val persistence = Persistence(Persistence.InMemoryH2("test"))

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

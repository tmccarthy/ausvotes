package au.id.tmm.senatedb.data.database

import java.nio.file.Files

import org.scalatest.{BeforeAndAfterEach, OneInstancePerTest, Suite}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration._

trait TestsPersistence extends BeforeAndAfterEach { this: Suite with OneInstancePerTest =>

  val sqliteDBLocation = Files.createTempDirectory("persistenceTest").resolve("senateDB.db")
  val persistence = Persistence(Persistence.SQLite(sqliteDBLocation))

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

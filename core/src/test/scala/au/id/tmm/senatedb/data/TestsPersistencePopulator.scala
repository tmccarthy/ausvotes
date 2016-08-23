package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.TestsPersistence
import au.id.tmm.senatedb.data.rawdatastore.download.TestsRawData
import org.scalatest.Suite

import scala.concurrent.ExecutionContext.Implicits.global

trait TestsPersistencePopulator extends TestsPersistence with TestsRawData { this: Suite =>
  lazy val persistencePopulator = PersistencePopulator(persistence, rawDataStore)
}

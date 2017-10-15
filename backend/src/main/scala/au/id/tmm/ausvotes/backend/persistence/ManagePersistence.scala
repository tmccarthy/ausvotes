package au.id.tmm.ausvotes.backend.persistence

import org.flywaydb.core.Flyway
import scalikejdbc.ConnectionPool
import scalikejdbc.config.DBs

object ManagePersistence {

  def start(): Unit = {
    DBs.setup()
  }

  def migrateSchema(): Unit = {
    val flyway = new Flyway()
    flyway.setDataSource(ConnectionPool().dataSource)
    flyway.migrate()
  }

  def shutdown(): Unit = {
    DBs.close()
  }

}

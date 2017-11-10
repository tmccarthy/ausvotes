package au.id.tmm.ausvotes.backend.persistence

import java.sql.SQLTimeoutException
import java.time.Instant

import au.id.tmm.ausvotes.core.logging.{LoggedEvent, Logger}
import org.flywaydb.core.Flyway
import scalikejdbc.config.DBs
import scalikejdbc.{ConnectionPool, DB, _}

import scala.annotation.tailrec
import scala.concurrent.duration.Duration

object ManagePersistence {

  private implicit val logger: Logger = Logger()

  def start(): Unit = {
    LoggedEvent("DB_SETUP").logWithTimeOnceFinished {
      DBs.setup()
    }
  }

  def waitForDatabase(timeout: Duration): Unit = {

    @tailrec
    def attemptConnectionsUntil(instant: Instant): Unit = {
      try {
        DB.localTx { implicit session =>
          val statement = sql"SELECT 1"

          statement.map(_ => Unit)
            .execute()
            .apply()
        }
      } catch {
        case e: java.sql.SQLException => {
          if (Instant.now().isBefore(instant)) {
            Thread.sleep(250)
            attemptConnectionsUntil(instant)
          } else {
            throw new SQLTimeoutException(e)
          }
        }
      }
    }

    val timoutInstant = Instant.now().plusMillis(timeout.toMillis)

    attemptConnectionsUntil(timoutInstant)
  }

  def migrateSchema(): Unit = {
    val loggedEvent = LoggedEvent("FLYWAY_MIGRATE")

    loggedEvent.logWithTimeOnceFinished {
      val flyway = new Flyway()

      flyway.setDataSource(ConnectionPool().dataSource)

      val numMigrationsApplied = flyway.migrate()

      loggedEvent.kvPairs += "numMigrationsApplied" -> numMigrationsApplied
    }
  }

  def clearSchema(): Unit = {
    LoggedEvent("CLEAR_SCHEMA").logWithTimeOnceFinished {
      val flyway = new Flyway()

      flyway.setDataSource(ConnectionPool().dataSource)

      flyway.clean()
    }
  }

  def shutdown(): Unit = {
    LoggedEvent("DB_SHUTDOWN").logWithTimeOnceFinished {
      DBs.close()
    }
  }

}

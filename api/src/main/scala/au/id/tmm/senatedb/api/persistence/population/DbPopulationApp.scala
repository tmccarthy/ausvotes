package au.id.tmm.senatedb.api.persistence.population

import au.id.tmm.senatedb.api.Module
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.utilities.concurrent.FutureUtils.await
import com.google.inject.{Guice, Injector}
import net.codingwell.scalaguice.InjectorExtensions._
import org.flywaydb.core.Flyway
import scalikejdbc.ConnectionPool
import scalikejdbc.config.DBs

object DbPopulationApp {
  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(new Module())

    try {
      DBs.setupAll()

      flywayMigrate()

      doPopulation(injector)
    } finally {
      DBs.closeAll()
    }
  }

  private def flywayMigrate(): Unit = {
    val flyway = new Flyway()
    flyway.setDataSource(ConnectionPool().dataSource)
    flyway.migrate()
  }

  private def doPopulation(injector: Injector): Unit = {
    val dbPopulator = injector.instance[DbPopulator]

    await(dbPopulator.populateAsRequired(SenateElection.`2016`))
  }
}

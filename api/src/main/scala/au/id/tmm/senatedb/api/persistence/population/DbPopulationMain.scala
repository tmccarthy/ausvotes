package au.id.tmm.senatedb.api.persistence.population

import java.time.Instant

import au.id.tmm.senatedb.core.model.SenateElection
import play.api._
import scalikejdbc.ConnectionPool

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationLong}

object DbPopulationMain {

  def main(args: Array[String]): Unit = {
    val env = Environment(new java.io.File("."), this.getClass.getClassLoader, Mode.Prod)
    val context = ApplicationLoader.createContext(env)
    val loader = ApplicationLoader(context)
    val app = loader.load(context)

    Play.start(app)

    try {
      waitForConnectionPool()
      // TODO wait for flyway migration

      val dbPopulator = app.injector.instanceOf(classOf[DbPopulator])

      Await.result(dbPopulator.populateAsRequired(SenateElection.`2016`), Duration.Inf)
    } finally {
      Await.result(app.stop(), Duration.Inf)
    }
  }

  private def waitForConnectionPool(): Unit = {
    val sleepTime = 100.milliseconds
    val timeout = 15.seconds

    val timeoutTime = Instant.now() plusMillis timeout.toMillis

    do {
      if (ConnectionPool.isInitialized()) {
        return
      } else {
        Thread.sleep(sleepTime.toMillis)
      }
    } while (Instant.now isBefore timeoutTime)
  }
}

package au.id.tmm.senatedb.webapp.persistence.population

import au.id.tmm.senatedb.core.model.SenateElection
import org.flywaydb.play.PlayInitializer
import play.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DbPopulationMain {

  def main(args: Array[String]): Unit = {
    val env = Environment(new java.io.File("."), this.getClass.getClassLoader, Mode.Test)
    val context = ApplicationLoader.createContext(env)
    val loader = ApplicationLoader(context)
    val app = loader.load(context)

    Play.start(app)

    // TODO find a cleaner way of doing this
    Thread.sleep(4000) // Wait for the connection pool to start up

    try {
      val playFlywayInitialiser = app.injector.instanceOf(classOf[PlayInitializer])
      playFlywayInitialiser.onStart()

      val dbPopulator = app.injector.instanceOf(classOf[DbPopulator])

      Await.result(dbPopulator.populateAsNeeded(SenateElection.`2016`), Duration.Inf)

    } finally {
      Await.result(app.stop(), Duration.Inf)
    }
  }
}

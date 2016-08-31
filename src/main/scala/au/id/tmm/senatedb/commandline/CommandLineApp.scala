package au.id.tmm.senatedb.commandline

import au.id.tmm.senatedb.data.PersistencePopulator
import au.id.tmm.senatedb.data.database.Persistence
import au.id.tmm.senatedb.data.database.Persistence.{DbPlatform, MySql, SQLite}
import au.id.tmm.senatedb.data.rawdatastore.RawDataStore

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, ExecutionContext}

object CommandLineApp {

  def main(args: String): Unit = main(args.split("\\s+"))

  def main(args: Array[String]): Unit = {
    val parsedArgs = parseCommandLineArgs(args)
      .flatMap(validateCommandLineArgs)

    parsedArgs match {
      case a: CommandLineArgs => runWith(a)
      case e: CommandLineErrors => handle(e)
    }
  }

  private def runWith(args: CommandLineArgs) = {
    implicit val executionContext = ExecutionContext.global

    val allowDownload = !args.forbidDownload
    val forceReload = args.verb == Verb.RELOAD

    val persistence = Persistence(dbPlatformOf(args))
    val rawDataStore = RawDataStore(args.rawDataDirectory)

    val persistencePopulator = PersistencePopulator(persistence, rawDataStore)

    Await.result(persistencePopulator.loadBallotsForStates(args.election, args.statesToLoad, allowDownload, forceReload), Inf)
  }

  private def dbPlatformOf(args: CommandLineArgs): DbPlatform = {
    if (args.mySqlHost.isDefined && args.mySqlUser.isDefined && args.mySqlDatabase.isDefined) {
      val host = args.mySqlHost.get
      val user = args.mySqlUser.get
      val database = args.mySqlDatabase.get

      val password = System.console().readPassword(s"MySql password for $user@$host: ")

      MySql(host, user, database, password)
    } else {
      SQLite(args.sqliteLocation.get)
    }
  }


  private def handle(e: CommandLineErrors): Unit = {
    if (e.errors.contains(CommandLineError.HelpRequested)) {
      System.exit(0)
    } else {
      System.err.println("Invalid parameters:")
      e.errors.foreach(error => System.err.println("  " + error.message))
      System.err.println("Use --help to see the usage.")
    }
  }
}

package au.id.tmm.senatedb.commandline

import au.id.tmm.senatedb.data.PersistencePopulator
import au.id.tmm.senatedb.data.database.Persistence
import au.id.tmm.senatedb.data.database.Persistence.{DbPlatform, Postgres, SQLite}
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
    if (args.postgresHost.isDefined && args.postgresUser.isDefined && args.postgresDatabase.isDefined) {
      val host = args.postgresHost.get
      val user = args.postgresUser.get
      val database = args.postgresDatabase.get

      val password = args.postgresPassword getOrElse askUserForPassword(user, host)

      Postgres(host, user, database, password)
    } else {
      SQLite(args.sqliteLocation.get)
    }
  }

  private def askUserForPassword(user: String, host: String): Array[Char] =
    System.console().readPassword(s"Postgres password for $user@$host: ")

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

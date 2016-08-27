package au.id.tmm.senatedb.commandline

import java.io.File

import au.id.tmm.senatedb.model.{SenateElection, State}
import scopt.OptionParser

object parseCommandLineArgs {

  type ErrorOrArgs = Either[List[CommandLineError], CommandLineArgs]

  private val initialValue: ErrorOrArgs = Right(CommandLineArgs())

  def apply(argsString: String): ErrorOrArgs = parseCommandLineArgs(argsString.split("\\s+"))

  def apply(args: Array[String]): ErrorOrArgs = parser.parse(args, initialValue)
    .getOrElse(Left(List(CommandLineError.InvalidFlagProvided)))

  private val parser = new OptionParser[ErrorOrArgs]("SenateDB") {
    override def terminate(exitState: Either[String, Unit]): Unit = {}

    private def addError(accumulated: ErrorOrArgs, error: CommandLineError): ErrorOrArgs = {
      accumulated match {
        case Left(existingErrors) => Left(existingErrors :+ error)
        case Right(accumulatedArgs) => Left(List(error))
      }
    }

    head("SenateDB")

    note("Options:")

    help("help")
      .text("Prints this usage text.")
      .action {
        case (_, accumulatedArgs) => addError(accumulatedArgs, CommandLineError.HelpRequested)
      }

    arg[String]("verb")
      .action {
        case (verbName, accumulatedArgs) => {
          val parsedVerb = Verb.fromString(verbName)

          parsedVerb match {
            case Some(verb) => accumulatedArgs.right.map(_.copy(verb = verb))
            case None => addError(accumulatedArgs, CommandLineError.UnrecognisedVerb(verbName))
          }
        }
      }

    opt[Unit]("forbidDownload")
      .text("forbids the downloading of raw data, failing if this is required")
      .action {
        case (_, accumulatedArgs) => accumulatedArgs.right.map(_.copy(forbidDownload = true))
      }

    opt[File]("rawData")
      .text("specifies the raw data directory, which will be created if missing (defaults to './rawData')")
      .action {
        case (directory, accumulatedArgs) => accumulatedArgs.right.map(_.copy(rawDataDirectory = directory.toPath))
      }

    opt[File]("sqlite")
      .text("specifies the location of an sqlite database, into which data will be loaded (defaults to '.SenateDB.db')")
      .action {
        case (database, accumulatedArgs) => accumulatedArgs.right.map(_.copy(sqliteLocation = Some(database.toPath)))
      }

    opt[String]("election")
      .text("specifies the election for which data will be loaded (defaults to '2016')")
      .action {
        case (electionName, accumulatedArgs) => {
          val parsedElection = SenateElection.fromCommonName(electionName)

          parsedElection match {
            case Some(election) => accumulatedArgs.right.map(_.copy(election = election))
            case None => addError(accumulatedArgs, CommandLineError.UnrecognisedElection(electionName))
          }
        }
      }

    opt[Unit]("allStates")
      .text("requests that all states from the specified election be loaded")
      .action {
        case (_, accumulatedArgs) => accumulatedArgs.right.map(args => args.copy(statesToLoad = args.election.states))
      }

    arg[String]("stateNames")
      .unbounded()
      .optional()
      .action {
        case (stateName, accumulatedArgs) => {
          val parsedState = State.fromShortName(stateName)

          parsedState match {
            case Some(state) => accumulatedArgs.right.map(args => args.copy(statesToLoad = args.statesToLoad + state))
            case None => addError(accumulatedArgs, CommandLineError.UnrecognisedState(stateName))
          }
        }
      }

  }
}


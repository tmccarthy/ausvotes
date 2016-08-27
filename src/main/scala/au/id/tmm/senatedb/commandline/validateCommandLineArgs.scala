package au.id.tmm.senatedb.commandline

import java.nio.file.Files

import au.id.tmm.senatedb.commandline.Verb.UNSPECIFIED
import au.id.tmm.senatedb.model.SenateElection

object validateCommandLineArgs {

  val supportedElections = Set(SenateElection.`2016`)

  def apply(args: CommandLineArgs): ErrorsOrArgs = {
    val validationErrors = List(
      checkVerb(args),
      checkRawData(args),
      checkHasDatabase(args),
      checkStates(args),
      checkElection(args)
    ).flatten

    if (validationErrors.isEmpty) {
      args
    } else {
      CommandLineErrors(validationErrors)
    }
  }

  private def checkVerb(args: CommandLineArgs): Set[CommandLineError] = {
    if (args.verb == UNSPECIFIED) {
      Set(CommandLineError.MissingVerb)
    } else {
      Set()
    }
  }

  private def checkRawData(args: CommandLineArgs): Set[CommandLineError] = {
    val rawDataDir = args.rawDataDirectory

    if (!Files.isDirectory(rawDataDir) && Files.exists(rawDataDir)) {
      Set(CommandLineError.RawDataIsntDirectory(rawDataDir))
    } else {
      Set()
    }
  }

  private def checkHasDatabase(args: CommandLineArgs): Set[CommandLineError] = {
    if (args.sqliteLocation.isEmpty) {
      Set(CommandLineError.NoDatabaseSpecified)
    } else {
      Set()
    }
  }

  private def checkStates(args: CommandLineArgs): Set[CommandLineError] = {
    if (args.statesToLoad.isEmpty) {
      Set(CommandLineError.NoStatesSpecified)
    } else {
      Set()
    }
  }

  private def checkElection(args: CommandLineArgs): Set[CommandLineError] = {
    if (!supportedElections.contains(args.election)) {
      Set(CommandLineError.UnsupportedElection(args.election))
    } else {
      Set()
    }
  }

}

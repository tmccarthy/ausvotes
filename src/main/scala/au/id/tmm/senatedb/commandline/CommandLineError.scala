package au.id.tmm.senatedb.commandline

import java.nio.file.Path

import au.id.tmm.senatedb.model.SenateElection

sealed trait CommandLineError

object CommandLineError {
  case object NoStatesSpecified
    extends CommandLineError

  case object HelpRequested
    extends CommandLineError

  case class RawDataIsntDirectory(rawDataDirectory: Path)
    extends CommandLineError

  case object NoDatabaseSpecified
    extends CommandLineError

  case class UnsupportedElection(election: SenateElection)
    extends CommandLineError

  case class UnrecognisedElection(electionName: String)
    extends CommandLineError

  case object InvalidFlagProvided
    extends CommandLineError

  case class UnrecognisedState(stateName: String)
    extends CommandLineError

  case class UnrecognisedVerb(verbName: String)
    extends CommandLineError

  case object MissingVerb
    extends CommandLineError
}
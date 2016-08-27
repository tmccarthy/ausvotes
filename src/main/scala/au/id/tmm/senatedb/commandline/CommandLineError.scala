package au.id.tmm.senatedb.commandline

import java.nio.file.Path

import au.id.tmm.senatedb.model.SenateElection

sealed trait CommandLineError {
  def message: String
}

object CommandLineError {
  case object NoStatesSpecified
    extends CommandLineError {
    lazy val message = "No states were specified"
  }

  case object HelpRequested
    extends CommandLineError {
    lazy val message = ""
  }

  case class RawDataIsntDirectory(rawDataDirectory: Path)
    extends CommandLineError {
    lazy val message = s"The specified raw data directory '$rawDataDirectory' is not a directory"
  }

  case object NoDatabaseSpecified
    extends CommandLineError {
    lazy val message = "No database was specified"
  }

  case class UnsupportedElection(election: SenateElection)
    extends CommandLineError {
    lazy val message = "The specified election is not supported"
  }

  case class UnrecognisedElection(electionName: String)
    extends CommandLineError {
    lazy val message = s"The specified election '$electionName' was not recognisable"
  }

  case object InvalidFlagProvided
    extends CommandLineError {
    lazy val message = "There was a problem parsing the provided arguments. Use the --help flag to display usage."
  }

  case class UnrecognisedState(stateName: String)
    extends CommandLineError {
    lazy val message = s"The specified state '$stateName' was not recognisable"
  }

  case class UnrecognisedVerb(verbName: String)
    extends CommandLineError {
    lazy val message = s"The specified verb '$verbName' was not recognisable"
  }

  case object MissingVerb
    extends CommandLineError {
    lazy val message = "No verb was specified"
  }
}
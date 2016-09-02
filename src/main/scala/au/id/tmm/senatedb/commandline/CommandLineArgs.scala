package au.id.tmm.senatedb.commandline

import java.nio.file.{Path, Paths}

import au.id.tmm.senatedb.model.{SenateElection, State}

final case class CommandLineArgs(verb: Verb = Verb.UNSPECIFIED,
                                 rawDataDirectory: Path = Paths.get("rawData"),
                                 forbidDownload: Boolean = false,
                                 sqliteLocation: Option[Path] = Some(Paths.get("senateDB.db")),
                                 postgresHost: Option[String] = None,
                                 postgresUser: Option[String] = None,
                                 postgresDatabase: Option[String] = Some("senatedb"),
                                 postgresPassword: Option[Array[Char]] = None,
                                 election: SenateElection = SenateElection.`2016`,
                                 statesToLoad: Set[State] = Set()) extends ErrorsOrArgs {
  override def map(f: (CommandLineArgs) => CommandLineArgs): ErrorsOrArgs = f(this)

  override def flatMap(f: (CommandLineArgs) => ErrorsOrArgs): ErrorsOrArgs = f(this)

  override def isArgs: Boolean = true

  override def asArgs: CommandLineArgs = this
}

sealed trait ErrorsOrArgs {
  def map(f: CommandLineArgs => CommandLineArgs): ErrorsOrArgs
  def flatMap(f: CommandLineArgs => ErrorsOrArgs): ErrorsOrArgs
  def asArgs: CommandLineArgs = throw new IllegalStateException("Not args")
  def asErrors: CommandLineErrors = throw new IllegalStateException("Not errors")
  def isArgs: Boolean
  def isErrors: Boolean = !isArgs
}

final case class CommandLineErrors(errors: List[CommandLineError]) extends ErrorsOrArgs {
  override def map(f: (CommandLineArgs) => CommandLineArgs): ErrorsOrArgs = this

  override def flatMap(f: (CommandLineArgs) => ErrorsOrArgs): ErrorsOrArgs = this

  override def isArgs: Boolean = false

  override def asErrors: CommandLineErrors = this
}
package au.id.tmm.senatedb.commandline

import java.nio.file.Files

import au.id.tmm.senatedb.commandline.CommandLineError._
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class validateCommandLineArgsSpec extends ImprovedFlatSpec {

  private def assertHasError(result: ErrorsOrArgs, expectedError: CommandLineError): Unit = {
    assert(result.isErrors, result)
    assert(result.asErrors.errors.contains(expectedError))
  }

  private def assertMissingError(result: ErrorsOrArgs, error: CommandLineError): Unit = {
    if (result.isErrors) {
      assert(!result.asErrors.errors.contains(error))
    }
  }

  "the verb" must "be specified" in {
    val result = validateCommandLineArgs(CommandLineArgs(verb = Verb.UNSPECIFIED))

    assertHasError(result, MissingVerb)
  }

  "the raw data directory" must "not be a regular file" in {
    val regularFile = Files.createTempFile("notADirectory", ".tmp")

    val result = validateCommandLineArgs(CommandLineArgs(rawDataDirectory = regularFile))

    assertHasError(result, RawDataIsntDirectory(regularFile))
  }

  it can "be a missing file" in {
    val tempFile = Files.createTempFile("tempFile", ".tmp")
    Files.delete(tempFile)
    val missingFile = tempFile

    val result = validateCommandLineArgs(CommandLineArgs(rawDataDirectory = missingFile))

    assertMissingError(result, RawDataIsntDirectory(missingFile))
  }

  "the states" must "include at least one state" in {
    val result = validateCommandLineArgs(CommandLineArgs(statesToLoad = Set()))

    assertHasError(result, NoStatesSpecified)
  }

  "the election" must "be supported" in {
    val result = validateCommandLineArgs(CommandLineArgs(election = SenateElection.`2013`))

    assertHasError(result, UnsupportedElection(SenateElection.`2013`))
  }

  "a database connection" must "be specified" in {
    val result = validateCommandLineArgs(CommandLineArgs(sqliteLocation = None,
      postgresDatabase = None, postgresHost = None, postgresUser = None))

    assertHasError(result, NoDatabaseSpecified)
  }

  "a mysql connection" must "have the user specified" in {
    val result = validateCommandLineArgs(CommandLineArgs(sqliteLocation = None,
      postgresHost = Some("localhost"), postgresUser = None))

    assertHasError(result, PostgresUserNotSpecified)
  }

  it must "have the host specified" in {
    val result = validateCommandLineArgs(CommandLineArgs(sqliteLocation = None,
      postgresHost = None, postgresUser = Some("test")))

    assertHasError(result, PostgresHostNotSpecified)
  }

  it must "have the database specified" in {
    val result = validateCommandLineArgs(CommandLineArgs(sqliteLocation = None,
      postgresHost = Some("localhost"), postgresUser = Some("test"), postgresDatabase = None))

    assertHasError(result, PostgresDatabaseNotSpecified)
  }
}

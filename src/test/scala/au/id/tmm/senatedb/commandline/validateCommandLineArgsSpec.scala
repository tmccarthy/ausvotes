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

  "the sqlite location" must "be specified" in {
    val result = validateCommandLineArgs(CommandLineArgs(sqliteLocation = None))

    assertHasError(result, NoDatabaseSpecified)
  }

  "the states" must "include at least one state" in {
    val result = validateCommandLineArgs(CommandLineArgs(statesToLoad = Set()))

    assertHasError(result, NoStatesSpecified)
  }

  "the election" must "be supported" in {
    val result = validateCommandLineArgs(CommandLineArgs(election = SenateElection.`2013`))

    assertHasError(result, UnsupportedElection(SenateElection.`2013`))
  }
}

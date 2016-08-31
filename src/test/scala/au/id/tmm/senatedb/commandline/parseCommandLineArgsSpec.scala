package au.id.tmm.senatedb.commandline

import java.nio.file.Paths

import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class parseCommandLineArgsSpec extends ImprovedFlatSpec {

  behaviour of "the verb"

  it should "default to UNSPECIFIED" in {
    val result = parseCommandLineArgs("")

    assert(result.isArgs, result)
    assert(result.asArgs.verb === Verb.UNSPECIFIED)
  }

  it should "parse LOAD" in {
    val result = parseCommandLineArgs("load NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.verb === Verb.LOAD)
  }

  it should "parse RELOAD" in {
    val result = parseCommandLineArgs("reload NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.verb === Verb.RELOAD)
  }

  it should "not be case sensitive" in {
    val result = parseCommandLineArgs("lOAd")

    assert(result.isArgs, result)
    assert(result.asArgs.verb === Verb.LOAD)
  }

  behaviour of "the raw data location"

  it should "default to the rawData directory" in {
    val result = parseCommandLineArgs("load NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.rawDataDirectory === Paths.get("rawData"))
  }

  it should "be specified with the --rawData flag" in {
    val result = parseCommandLineArgs("load --rawData /tmp/rawData NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.rawDataDirectory === Paths.get("/tmp/rawData"))
  }

  behaviour of "the sqlite db location"

  it should "default to a file at senateDB.db" in {
    val result = parseCommandLineArgs("load NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.sqliteLocation === Some(Paths.get("senateDB.db")))
  }

  it should "be specified with the --sqlite flag" in {
    val result = parseCommandLineArgs("load --sqlite /tmp/db.db NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.sqliteLocation === Some(Paths.get("/tmp/db.db")))
  }

  behaviour of "the mysql host"

  it should "default to nothing" in {
    val result = parseCommandLineArgs("load NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.mySqlHost === None)
  }

  it should "be specified with the --mysql-host flag" in {
    val result = parseCommandLineArgs("load --mysql-host localhost NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.mySqlHost === Some("localhost"))
  }

  behaviour of "the mysql user"

  it should "default to nothing" in {
    val result = parseCommandLineArgs("load NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.mySqlUser === None)
  }

  it should "be specified with the --mysql-user flag" in {
    val result = parseCommandLineArgs("load --mysql-user test NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.mySqlUser === Some("test"))
  }

  behaviour of "the mysql database"

  it should "default to 'senatedb'" in {
    val result = parseCommandLineArgs("load NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.mySqlDatabase === Some("senatedb"))
  }

  it should "be specified with the --mysql-db flag" in {
    val result = parseCommandLineArgs("load --mysql-db thing NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.mySqlDatabase === Some("thing"))
  }

  behaviour of "the forbidDownload switch"

  it should "default to false" in {
    val result = parseCommandLineArgs("load NSW")

    assert(result.isArgs, result)
    assert(!result.asArgs.forbidDownload)
  }

  it should "be specified with the --forbidDownload flag" in {
    val result = parseCommandLineArgs("load --forbidDownload NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.forbidDownload)
  }

  behaviour of "the election"

  it should "default to the 2016 election" in {
    val result = parseCommandLineArgs("load NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.election === SenateElection.`2016`)
  }

  it should "be specified with the --election flag" in {
    val result = parseCommandLineArgs("load --election 2016 NSW")

    assert(result.isArgs, result)
    assert(result.asArgs.election === SenateElection.`2016`)
  }

  it should "return an error if the election cannot be parsed" in {
    val result = parseCommandLineArgs("load --election blah NSW")

    assert(result === CommandLineErrors(List(CommandLineError.UnrecognisedElection("blah"))))
  }

  behaviour of "the states"

  they should "be specified without flags" in {
    val result = parseCommandLineArgs("load NSW VIC SA")

    assert(result.isArgs, result)
    assert(result.asArgs.statesToLoad === Set(State.NSW, State.VIC, State.SA))
  }

  they should "be parsed as case insensitive" in {
    val result = parseCommandLineArgs("load NsW vIC Sa")

    assert(result.isArgs, result)
    assert(result.asArgs.statesToLoad === Set(State.NSW, State.VIC, State.SA))
  }

  they can "all be requested by the --allStates flag" in {
    val result = parseCommandLineArgs("load --allStates")

    assert(result.isArgs, result)
    assert(result.asArgs.statesToLoad === State.ALL_STATES)
  }

  "requesting help" should "prevent further parsing" in {
    val result = parseCommandLineArgs("--help load --allStates")

    assert(result === CommandLineErrors(List(CommandLineError.HelpRequested)))
  }
}

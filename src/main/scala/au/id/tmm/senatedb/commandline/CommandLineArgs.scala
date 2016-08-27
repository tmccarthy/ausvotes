package au.id.tmm.senatedb.commandline

import java.nio.file.{Path, Paths}

import au.id.tmm.senatedb.model.{SenateElection, State}

final case class CommandLineArgs(verb: Verb = Verb.UNSPECIFIED,
                                 rawDataDirectory: Path = Paths.get("rawData"),
                                 forbidDownload: Boolean = false,
                                 sqliteLocation: Option[Path] = Some(Paths.get("senateDB.db")),
                                 election: SenateElection = SenateElection.`2016`,
                                 statesToLoad: Set[State] = Set()) {
}
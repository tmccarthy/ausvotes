package au.id.tmm.senatedb.entrypoints

import java.nio.file.Paths

import au.id.tmm.senatedb.engine.ParsedDataStore
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.BallotGroup
import au.id.tmm.senatedb.rawdata.{AecResourceStore, RawDataStore}

object ListGroups {
  def main(args: Array[String]): Unit = {
    val aecResourceStore = AecResourceStore.at(Paths.get("rawData"))
    val rawDataStore = RawDataStore(aecResourceStore)
    val parsedDataStore = ParsedDataStore(rawDataStore)

    val groupsAndCandidates = parsedDataStore.groupsAndCandidatesFor(SenateElection.`2016`)

    val allGroups = groupsAndCandidates.groups

    val orderedGroupsPerState = allGroups.toStream
      .groupBy(_.state)
      .mapValues(_.sorted(BallotGroup.ordering).toVector)
      .toVector

    orderedGroupsPerState.foreach {
      case (state, groups) => {
        println(s"### ${state.abbreviation}")
        groups.foreach(group => println(s"${group.code} (index = ${group.index}): ${group.party.getOrElse("NO PARTY")}"))
        println()
      }
    }
  }
}

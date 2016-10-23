package au.id.tmm.senatedb.entrypoints

import java.nio.file.Paths

import au.id.tmm.senatedb.engine.ParsedDataStore
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.{AecResourceStore, RawDataStore}

object ListParties {

  def main(args: Array[String]): Unit = {
    val aecResourceStore = AecResourceStore.at(Paths.get("rawData"))
    val rawDataStore = RawDataStore(aecResourceStore)
    val parsedDataStore = ParsedDataStore(rawDataStore)

    val groupsAndCandidates = parsedDataStore.groupsAndCandidatesFor(SenateElection.`2016`)

    val partiesFromGroups = groupsAndCandidates.groups.flatMap(_.party)

    val partiesFromCandidates = groupsAndCandidates.candidates.flatMap(_.party)

    val allParties = partiesFromGroups ++ partiesFromCandidates

    val partyNameList = allParties.toStream
      .map(_.name)
      .sorted
      .toVector

    println(partyNameList.mkString("\n"))
  }
}

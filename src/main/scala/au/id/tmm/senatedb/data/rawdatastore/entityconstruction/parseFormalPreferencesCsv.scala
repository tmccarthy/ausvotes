package au.id.tmm.senatedb.data.rawdatastore.entityconstruction

import au.id.tmm.senatedb.data.BallotWithPreferences
import au.id.tmm.senatedb.data.database._
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.collection.CloseableIterator

import scala.collection.immutable.ListMap
import scala.io.Source
import scala.util.Try

private [data] object parseFormalPreferencesCsv {

  private val ignoredLineIndexes = Set(0, 1)

  def apply(election: SenateElection,
            state: State,
            allCandidates: Set[CandidatesRow],
            csvLines: Source): Try[CloseableIterator[BallotWithPreferences]] = Try {
    val numCandidatesPerGroup = computeNumCandidatesPerGroup(election, state, allCandidates)

    val lineIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, ignoredLineIndexes)

    lineIterator
      .filterNot(CsvParseUtil.lineIsBlank)
      .map(csvLine => formalPreferencesCsvLineToEntities(election, state, numCandidatesPerGroup, csvLine).get) // Any thrown exceptions will go up to the encompassing Try
  }

  private def computeNumCandidatesPerGroup(election: SenateElection, state: State, allCandidates: Set[CandidatesRow]) = {
    val relevantCandidates = allCandidates
      .toStream
      .filter(candidate => candidate.election == election.aecID && candidate.state == state.shortName)

    val candidatesPerGroup = relevantCandidates
      .groupBy(_.group)

    val numCandidatesPerGroup = candidatesPerGroup
      .toStream
      .map(pair => pair._1 -> pair._2.length)

    ListMap(numCandidatesPerGroup:_*)
  }
}

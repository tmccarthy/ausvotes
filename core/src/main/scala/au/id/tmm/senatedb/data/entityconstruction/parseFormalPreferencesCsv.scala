package au.id.tmm.senatedb.data.entityconstruction

import au.id.tmm.senatedb.data.database._
import au.id.tmm.senatedb.model.{SenateElection, State}

import scala.collection.immutable.ListMap
import scala.io.Source
import scala.util.Try

private [data] object parseFormalPreferencesCsv {

  private val ignoredLineIndexes = Set(0, 1)

  def apply(election: SenateElection,
            state: State,
            allCandidates: Set[CandidatesRow],
            csvLines: Source): Try[Iterator[Try[BallotWithPreferences]]] = Try {
    val numCandidatesPerGroup = computeNumCandidatesPerGroup(election, state, allCandidates)

    val lineIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, ignoredLineIndexes)

    lineIterator
      .filterNot(CsvParseUtil.lineIsBlank)
      .map(csvLine => formalPreferencesCsvLineToEntities(election, state, numCandidatesPerGroup, csvLine)) // Any thrown exceptions will go up to the encompassing Try
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

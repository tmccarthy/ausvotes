package au.id.tmm.senatedb.database.entityconstruction

import au.id.tmm.senatedb.database.model._
import au.id.tmm.senatedb.model.{SenateElection, State}

import scala.collection.immutable.ListMap
import scala.io.Source
import scala.util.Try

private [database] object parseFormalPreferencesCsv {

  private val ignoredLineIndexes = Set(0, 1)

  type BallotsAndPreferences = (Set[BallotRow], Set[AtlPreferencesRow], Set[BtlPreferencesRow])

  def apply(election: SenateElection,
            state: State,
            allCandidates: Set[CandidatesRow],
            csvLines: Source): Try[(Set[BallotRow], Set[AtlPreferencesRow], Set[BtlPreferencesRow])] = Try {
    val numCandidatesPerGroup = computeNumCandidatesPerGroup(election, state, allCandidates)

    val lineIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, ignoredLineIndexes)

    lineIterator
      .filterNot(CsvParseUtil.lineIsBlank)
      .map(csvLine => formalPreferencesCsvLineToEntities(election, state, numCandidatesPerGroup, csvLine).get) // Any thrown exceptions will go up to the encompassing Try
      .foldLeft(emptyAccumulator)(accumulate)
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

  private def emptyAccumulator: BallotsAndPreferences = (Set(), Set(), Set())

  private def accumulate(accumulator: BallotsAndPreferences,
                         toAdd: (BallotRow, Set[AtlPreferencesRow], Set[BtlPreferencesRow])): BallotsAndPreferences = {
    (accumulator._1 + toAdd._1, accumulator._2 ++ toAdd._2, accumulator._3 ++ toAdd._3)
  }

}

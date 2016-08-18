package au.id.tmm.senatedb.data.entityconstruction

import au.id.tmm.senatedb.data.database.{CandidatesRow, GroupsRow}
import au.id.tmm.senatedb.model.SenateElection

import scala.io.Source
import scala.util.Try

private[data] object parseFirstPreferencesCsv extends ((SenateElection, Source) => Try[(Set[GroupsRow], Set[CandidatesRow])]) {

  private val ignoredLineIndexes = Set(0, 1)

  private type CandidatesAndRows = (Set[GroupsRow], Set[CandidatesRow])

  override def apply(election: SenateElection, csvLines: Source): Try[(Set[GroupsRow], Set[CandidatesRow])] = Try {
    val lineIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, ignoredLineIndexes)

    lineIterator
      .filterNot(CsvParseUtil.lineIsBlank)
      .map(csvLine => firstPreferencesCsvLineToEntity(election, csvLine).get) // Any thrown exceptions will go up to the encompassing Try
      .foldLeft(emptyAccumulator)(accumulate)
  }

  private def emptyAccumulator: CandidatesAndRows = (Set(), Set())

  private def accumulate(accumulator: CandidatesAndRows, candidateOrRow: Either[GroupsRow, CandidatesRow]): CandidatesAndRows = {
    candidateOrRow match {
      case Left(group) => (accumulator._1 + group, accumulator._2)
      case Right(candidate) => (accumulator._1, accumulator._2 + candidate)
    }
  }

}

package au.id.tmm.senatedb.data.rawdatastore.entityconstruction

import au.id.tmm.senatedb.data.GroupsAndCandidates
import au.id.tmm.senatedb.data.database.model.{CandidatesRow, GroupsRow}
import au.id.tmm.senatedb.model.SenateElection

import scala.io.Source
import scala.util.Try

private[data] object parseFirstPreferencesCsv {

  def apply(election: SenateElection, csvLines: Source): Try[GroupsAndCandidates] = Try {
    val lineIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, numIgnoredLines = 2)

    lineIterator
      .filterNot(CsvParseUtil.lineIsBlank)
      .map(csvLine => firstPreferencesCsvLineToEntity(election, csvLine).get) // Any thrown exceptions will go up to the encompassing Try
      .foldLeft(GroupsAndCandidates())(accumulate)
  }

  private def accumulate(accumulator: GroupsAndCandidates, candidateOrRow: Either[GroupsRow, CandidatesRow]): GroupsAndCandidates = {
    candidateOrRow match {
      case Left(group) => accumulator.addGroup(group)
      case Right(candidate) => accumulator.addCandidate(candidate)
    }
  }
}

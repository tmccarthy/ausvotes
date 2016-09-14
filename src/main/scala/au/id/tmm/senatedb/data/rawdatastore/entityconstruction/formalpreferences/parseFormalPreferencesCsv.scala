package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.formalpreferences

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.expiry.ExhaustionCalculator
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.CsvParseUtil
import au.id.tmm.senatedb.data.{BallotWithPreferences, CountData, GroupsAndCandidates}
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.collection.CloseableIterator

import scala.io.Source
import scala.util.Try

private [data] object parseFormalPreferencesCsv {

  private val ignoredLineIndexes = Set(0, 1)

  def apply(election: SenateElection,
            state: State,
            groupsAndCandidates: GroupsAndCandidates,
            countData: CountData,
            csvLines: Source): Try[CloseableIterator[BallotWithPreferences]] = Try {
    val relevantGroupsAndCandidates = groupsAndCandidates.filteredTo(election, state)

    val rawPreferenceParser = new RawPreferenceParser(relevantGroupsAndCandidates)

    val exhaustionCalculator = ExhaustionCalculator(relevantGroupsAndCandidates.candidates, countData)
    val ballotNormaliser = BallotNormaliser.forCandidates(relevantGroupsAndCandidates.candidates)
    val ballotFactsCalculator = new BallotFactsCalculator(ballotNormaliser, exhaustionCalculator)

    val lineIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, ignoredLineIndexes)

    lineIterator
      .filterNot(CsvParseUtil.lineIsBlank)
      .map(csvLine => formalPreferencesCsvLineToEntities(election, state, rawPreferenceParser, ballotFactsCalculator,
        csvLine).get) // Any thrown exceptions will go up to the encompassing Try
  }

}
package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences

import au.id.tmm.senatedb.data.CountData
import au.id.tmm.senatedb.data.CountData.CountStepData
import au.id.tmm.senatedb.data.database.model._
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.CsvParseUtil
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences.TrackedCandidateStatus.{Determined, Undetermined}
import au.id.tmm.senatedb.model.{CandidatePosition, SenateElection, State}
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.collection.IteratorUtils.ImprovedIterator

import scala.annotation.tailrec
import scala.collection.mutable
import scala.io.Source
import scala.util.Try

object parseDistributionOfPreferencesCsv {

  private val ignoredLines = Set(0)

  def apply(election: SenateElection,
            state: State,
            allCandidates: Set[CandidatesRow],
            csvLines: Source): Try[CountData] = Try {
    val csvLinesIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, ignoredLines)

    val relevantCandidates = allCandidates
      .toStream
      .filter(_.election == election.aecID)
      .filter(_.state == state.shortName)
      .toSet

    parseRemainingCountSteps(election, state, relevantCandidates, csvLinesIterator)
  }

  @tailrec
  private def parseRemainingCountSteps(election: SenateElection,
                                       state: State,
                                       allCandidates: Set[CandidatesRow],
                                       csvLines: CloseableIterator[Seq[String]],
                                       firstTransfer: VoteTransferSummary = VoteTransferSummary.Initial,
                                       previousCountSteps: List[CountStepData] = Nil,
                                       candidateOutcomesSoFar: Map[CandidatePosition, TrackedCandidateStatus] = Map()
                                      ): CountData = {
    val count = previousCountSteps.lastOption.map(_.stepRow.count + 1).getOrElse(1)
    requireNotFinished(csvLines, count)

    val candidateIdPerPosition: Map[CandidatePosition, String] = allCandidates
      .map(candidate => candidate.position -> candidate.candidateId)
      .toMap

    val (firstCountStep, rawCandidateRows) = parseOneCount(election, state, candidateIdPerPosition, firstTransfer,
      count, csvLines)

    val nextTransfer = determineNextTransfer(firstTransfer, rawCandidateRows)

    val updatedCandidateOutcomes = updateCandidateOutcomes(count, rawCandidateRows, candidateOutcomesSoFar)

    if (csvLines.hasNext) {
      parseRemainingCountSteps(
        election,
        state,
        allCandidates,
        csvLines,
        nextTransfer,
        previousCountSteps :+ firstCountStep,
        updatedCandidateOutcomes)
    } else {
      val numExcludedSoFar = countExcludedCandidatesIn(rawCandidateRows)

      val outcomes = updatedCandidateOutcomes
        .toStream
        .map {
          case (position, trackedStatus: Determined) =>
            CountOutcomesPerCandidateRow(
              election = election.aecID,
              state = state.shortName,
              candidateId = candidateIdPerPosition(position),
              outcome = CandidateStatus.statusToOutcome(trackedStatus.status),
              outcomeOrder = trackedStatus.order,
              outcomeAtCount = trackedStatus.determinedAtCount)

          case (position, TrackedCandidateStatus.Undetermined) =>
            CountOutcomesPerCandidateRow(
              election = election.aecID,
              state = state.shortName,
              candidateId = candidateIdPerPosition(position),
              outcome = CandidateOutcome.EXCLUDED,
              outcomeOrder = numExcludedSoFar + 1,
              outcomeAtCount = count)
        }
        .toSet

      CountData(election, state, previousCountSteps :+ firstCountStep, outcomes)
    }
  }

  private def parseOneCount(election: SenateElection,
                            state: State,
                            candidateIdsPerPosition: Map[CandidatePosition, String],
                            transferSummary: VoteTransferSummary,
                            count: Int,
                            lines: CloseableIterator[Seq[String]]): (CountStepData, Vector[DopCsvRow]) = {
    val candidateTallyRows = readCandidateTallyRows(lines, numCandidates = candidateIdsPerPosition.size)

    requireNotFinished(lines, count)

    val exhaustedRow = new DopCsvRow(lines.next(), candidatePosition = None)

    def isExhaustedDataRow(row: DopCsvRow) = row.ballotPosition == 1001
    require(isExhaustedDataRow(exhaustedRow), s"Expected an exhausted ballots row, but one was missing for count $count")

    requireNotFinished(lines, count)

    val gainLossRow = new DopCsvRow(lines.next(), candidatePosition = None)

    def isGainLossDataRow(row: DopCsvRow) = row.ballotPosition == 1002
    require(isGainLossDataRow(gainLossRow), s"Expected a gain/loss row, but one was missing for count $count")

    val countStepData = composeCountStepDataFrom(election, state, transferSummary, candidateIdsPerPosition,
      candidateTallyRows, exhaustedRow, gainLossRow)

    (countStepData, candidateTallyRows)
  }

  private def readCandidateTallyRows(lines: CloseableIterator[Seq[String]], numCandidates: Int): Vector[DopCsvRow] = {
    var rowsToReturn = new mutable.ArrayBuffer[DopCsvRow](numCandidates)

    var currentGroup: Option[String] = None
    var positionInGroup = 0

    for (csvLine <- lines.readAtMost(numCandidates)) {
      val incompleteParsedLine = new IncompleteDopCsvRow(csvLine)

      if (currentGroup contains incompleteParsedLine.group) {
        positionInGroup = positionInGroup + 1
      } else {
        currentGroup = Some(incompleteParsedLine.group)
        positionInGroup = 0
      }

      rowsToReturn += incompleteParsedLine.completeWithPositionInGroup(positionInGroup)
    }

    rowsToReturn.toVector
  }

  private final class IncompleteDopCsvRow(private val row: Seq[String]) {
    def group = row(6).trim

    def completeWithPositionInGroup(positionInGroup: Int): DopCsvRow =
      new DopCsvRow(row, Some(CandidatePosition(group, positionInGroup)))
  }

  private def composeCountStepDataFrom(election: SenateElection,
                                       state: State,
                                       transferSummary: VoteTransferSummary,
                                       candidateIdsPerPosition: Map[CandidatePosition, String],
                                       rawCandidateTallyRows: Vector[DopCsvRow],
                                       exhaustedRow: DopCsvRow,
                                       gainLossRow: DopCsvRow): CountStepData = {
    val (candidateTransferRows, transferValue) = composeCandidateTransferRowsFrom(election, state,
      candidateIdsPerPosition, rawCandidateTallyRows)

    val numCandidatesElected = rawCandidateTallyRows.count(_.status == CandidateStatus.ELECTED)

    val stepRow = CountStepRow(
      election.aecID,
      state.shortName,
      exhaustedRow.count,
      transferValue,
      exhaustedRow.papers,
      exhaustedRow.votesTransferred,
      exhaustedRow.progressiveVoteTotal,
      gainLossRow.papers,
      gainLossRow.votesTransferred,
      gainLossRow.progressiveVoteTotal,
      transferSummary.stepType,
      transferSummary.fromCandidate.map(_.group),
      transferSummary.fromCandidate.map(_.positionInGroup),
      numCandidatesElected
    )

    CountStepData(stepRow, candidateTransferRows)
  }

  private def composeCandidateTransferRowsFrom(election: SenateElection,
                                               state: State,
                                               candidateIdsPerPosition: Map[CandidatePosition, String],
                                               candidateTallyRows: Vector[DopCsvRow]
                                              ): (Set[CountTransferPerCandidateRow], Double) = {
    var rowsToReturn = mutable.HashSet[CountTransferPerCandidateRow]()

    var currentGroup: Option[String] = None
    var positionInGroup = 0

    var transferValue = 1d

    for (csvRow <- candidateTallyRows) {

      if (currentGroup contains csvRow.group) {
        positionInGroup = positionInGroup + 1
      } else {
        currentGroup = Some(csvRow.group)
        positionInGroup = 0
      }

      rowsToReturn += CountTransferPerCandidateRow(
        election = election.aecID,
        state = state.shortName,
        count = csvRow.count,
        group = csvRow.group,
        positionInGroup = positionInGroup,
        candidateId = candidateIdsPerPosition(CandidatePosition(csvRow.group, positionInGroup)),
        papers = csvRow.papers,
        votesTransferred = csvRow.votesTransferred,
        votesTotal = csvRow.progressiveVoteTotal
      )

      if (csvRow.transferValue != 0) {
        transferValue = csvRow.transferValue
      }
    }

    (rowsToReturn.toSet, transferValue)
  }

  private def determineNextTransfer(previousTransfer: VoteTransferSummary,
                                    rawCandidateRows: Vector[DopCsvRow]): VoteTransferSummary = {
    val rawRowsForChangedCandidates = rawCandidateRows
      .filter(_.statusChanged)

    if (rawRowsForChangedCandidates.isEmpty) {
      previousTransfer
    } else if (rawRowsForChangedCandidates.size == 1) {
      val rawRowForChangedCandidate = rawRowsForChangedCandidates.head

      val nextStepDistributesFrom = rawRowForChangedCandidate.candidatePosition.get

      rawRowForChangedCandidate.status match {
        case CandidateStatus.ELECTED => VoteTransferSummary.FromElected(nextStepDistributesFrom)
        case CandidateStatus.EXCLUDED => VoteTransferSummary.FromExcluded(nextStepDistributesFrom)
        case _ => throw new IllegalArgumentException(s"A candidate was marked as changed, but they were neither " +
          s"elected nor excluded at count ${rawRowForChangedCandidate.count}")
      }
    } else {
      val explanatoryComment = rawCandidateRows.head.comment

      val parsedComment = VoteTransferComment.from(explanatoryComment)

      def electedTransferResultFor(candidateName: ShortCandidateName) =
        VoteTransferSummary.FromElected(findSingleMatchingCandidateRow(rawCandidateRows, candidateName).candidatePosition.get)

      parsedComment match {
        case VoteTransferComment.Excluded(numCandidatesExcluded, originatingCounts, transferValue) => previousTransfer
        case VoteTransferComment.ElectedWithSurplus(candidateName, _, _) => electedTransferResultFor(candidateName)
        case VoteTransferComment.ElectedWithQuotaNoSurplus(candidateName) => electedTransferResultFor(candidateName)
        case VoteTransferComment.ElectedLastRemaining => VoteTransferSummary.None
      }
    }
  }

  private def findSingleMatchingCandidateRow(candidateRows: Vector[DopCsvRow], candidateName: ShortCandidateName) = {
    val possiblyMatchingRows = candidateRows
      .toStream
      .filter(_.status == CandidateStatus.ELECTED)
      .filter(row => ShortCandidateName.fromCandidateCsvRow(row) == candidateName)
      .toSet

    if (possiblyMatchingRows.isEmpty) {
      throw new RuntimeException(s"Couldn't find a candidate matching the name $candidateName found in an " +
        s"explanatory comment")
    } else if (possiblyMatchingRows.size == 1) {
      possiblyMatchingRows.head
    } else {
      val fullNamesOfPossibleMatches = possiblyMatchingRows
        .map(row => s"${row.surname}, ${row.givenName}")

      throw new RuntimeException(s"The name $candidateName matches more than one " +
        s"candidate: ${fullNamesOfPossibleMatches.mkString("; ")}")
    }
  }

  private def updateCandidateOutcomes(count: Int,
                                      rawCandidateRows: Vector[DopCsvRow],
                                      candidateOutcomesSoFar: Map[CandidatePosition, TrackedCandidateStatus]) = {
    val numCandidatesPreviouslyExcluded = countExcludedCandidatesIn(rawCandidateRows)

    rawCandidateRows
      .toStream
      .map(row => {
        val position = row.candidatePosition.get

        if (candidateOutcomesSoFar.contains(position) && candidateOutcomesSoFar(position) != Undetermined) {
          position -> candidateOutcomesSoFar(position)
        } else if (row.status == CandidateStatus.ELECTED) {
          position -> TrackedCandidateStatus.Determined(CandidateStatus.ELECTED, row.orderElected, count)
        } else if (row.status == CandidateStatus.EXCLUDED) {
          position -> TrackedCandidateStatus.Determined(CandidateStatus.EXCLUDED, numCandidatesPreviouslyExcluded + 1, count)
        } else {
          position -> TrackedCandidateStatus.Undetermined
        }
      })
      .toMap
  }

  private def countExcludedCandidatesIn(rawCandidateRows: Vector[DopCsvRow]): Int =
    rawCandidateRows.count(_.status == CandidateStatus.EXCLUDED)

  private def requireNotFinished(lines: CloseableIterator[Seq[String]], count: Int): Unit = {
    require(lines.hasNext, s"Distribution of preferences file ended unexpectedly at count $count")
  }
}

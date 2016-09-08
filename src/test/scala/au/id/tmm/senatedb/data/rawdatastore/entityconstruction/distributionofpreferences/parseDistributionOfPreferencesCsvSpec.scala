package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences

import au.id.tmm.senatedb.data.TestData
import au.id.tmm.senatedb.data.database.model.CountStepRow.StepType
import au.id.tmm.senatedb.data.database.model.{CountStepRow, CountTransferPerCandidateRow}
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.io.Source

class parseDistributionOfPreferencesCsvSpec extends ImprovedFlatSpec {

  val testCsvResource = getClass.getResource("SenateStateDOPDownload-20499-ACT.csv")

  val testElection = SenateElection.`2016`
  val testState = State.ACT

  lazy val parsedCountData = parseDistributionOfPreferencesCsv(testElection, testState, TestData.allActCandidates,
    Source.fromURL(testCsvResource)).get

  "the parsed count data" should "have the correct election" in {
    assert(parsedCountData.election === testElection)
  }

  it should "have the correct state" in {
    assert(parsedCountData.state === testState)
  }

  "the initial step" should "have the correct metadat" in {
    val countSteps = parsedCountData.steps

    val initialCountStep = countSteps.head

    val expectedStep = CountStepRow(
      election = testElection.aecID,
      state = testState.shortName,
      count = 1,
      transferValue = 1,
      exhaustedPapers = 0,
      exhaustedVotesTransferred = 0,
      exhaustedProgressiveVoteTotal = 0,
      gainLossPapers = 0,
      gainLossVoteTransferred = 0,
      gainLossProgressiveVoteTotal = 0,
      stepType = StepType.INITIAL,
      votesDistributedFromGroup = None,
      votesDistributedFromPositionInGroup = None
    )

    assert(expectedStep === initialCountStep.stepRow)
  }

  it should "have the correct transfers per candidate" in {
    val countSteps = parsedCountData.steps

    val initialCountStep = countSteps.head

    val expectedVoteTransfers = Set(
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"A", 0, "29611", 7371, 7371, 7371),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"A", 1, "29612", 89, 89, 89),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"B", 0, "28933", 1322, 1322, 1322),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"B", 1, "28937", 56, 56, 56),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"C", 0, "28147", 95749, 95749, 95749),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"C", 1, "28149", 918, 918, 918),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"D", 0, "29514", 2455, 2455, 2455),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"D", 1, "29518", 68, 68, 68),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"E", 0, "28468", 2557, 2557, 2557),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"E", 1, "28469", 121, 121, 121),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"F", 0, "28773", 82932, 82932, 82932),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"F", 1, "28782", 1683, 1683, 1683),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"G", 0, "28254", 4150, 4150, 4150),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"G", 1, "28256", 101, 101, 101),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"H", 0, "28306", 40424, 40424, 40424),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"H", 1, "28308", 582, 582, 582),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"I", 0, "28760", 3011, 3011, 3011),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"I", 1, "28763", 76, 76, 76),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"J", 0, "29390", 9744, 9744, 9744),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"J", 1, "29391", 352, 352, 352),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"UG", 0, "29520", 698, 698, 698),
      CountTransferPerCandidateRow(testElection.aecID, testState.shortName, 1 ,"UG", 1, "28150", 308, 308, 308)
    )

    assert(expectedVoteTransfers === initialCountStep.transfers)
  }

  // TODO more tests
}

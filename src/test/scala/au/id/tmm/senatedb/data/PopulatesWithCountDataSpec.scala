package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.model.CountStepRow
import au.id.tmm.senatedb.data.database.model.CountStepRow.StepType
import au.id.tmm.senatedb.data.rawdatastore.download.DataMissingDownloadDisallowedException
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

class PopulatesWithCountDataSpec extends ImprovedFlatSpec with TestsPersistencePopulator {

  def assertCountDataLoadedCorrectly(): Unit = {
    val numCountSteps = Await.result(persistence.runQuery(persistence.dal.countSteps.size), Inf)
    val numCandidateOutcomes = Await.result(persistence.runQuery(persistence.dal.outcomesPerCandidate.size), Inf)
    val numVoteTransfers = Await.result(persistence.runQuery(persistence.dal.countTransfersPerCandidate.size), Inf)

    assert(numCountSteps === 29)
    assert(numCandidateOutcomes === 22)
    assert(numVoteTransfers === 638)
  }

  "loadCountData" can "load the count data for the ACT at the 2016 election" in {
    Await.result(persistencePopulator.loadCountData(SenateElection.`2016`, State.ACT), Inf)

    assertCountDataLoadedCorrectly()
  }

  it should "not reload the data if a step is already loaded for that state and election" in {
    val dummyRow = CountStepRow(
      election = SenateElection.`2016`.aecID,
      state = State.ACT.shortName,
      count = 1,
      transferValue = 1,
      exhaustedPapers = 1,
      exhaustedVotesTransferred = 1,
      exhaustedProgressiveVoteTotal = 1,
      gainLossPapers = 1,
      gainLossVoteTransferred = 1,
      gainLossProgressiveVoteTotal = 1,
      stepType = StepType.INITIAL,
      votesDistributedFromGroup = None,
      votesDistributedFromPositionInGroup = None,
      progressiveNumCandidatesElected = 0)

    Await.result(persistence.execute(persistence.dal.insertCountStep(dummyRow)), Inf)

    Await.result(persistencePopulator.loadCountData(SenateElection.`2016`, State.ACT, forceReload = false), Inf)

    val storedCountSteps = Await.result(persistence.runQuery(persistence.dal.countSteps), Inf)

    assert(storedCountSteps === Seq(dummyRow))
  }

  it should "reload the count data if requested" in {
    val dummyRow = CountStepRow(
      election = SenateElection.`2016`.aecID,
      state = State.ACT.shortName,
      count = 1,
      transferValue = 1,
      exhaustedPapers = 1,
      exhaustedVotesTransferred = 1,
      exhaustedProgressiveVoteTotal = 1,
      gainLossPapers = 1,
      gainLossVoteTransferred = 1,
      gainLossProgressiveVoteTotal = 1,
      stepType = StepType.INITIAL,
      votesDistributedFromGroup = None,
      votesDistributedFromPositionInGroup = None,
      progressiveNumCandidatesElected = 0)

    Await.result(persistence.execute(persistence.dal.insertCountStep(dummyRow)), Inf)

    Await.result(persistencePopulator.loadCountData(SenateElection.`2016`, State.ACT, forceReload = true), Inf)

    Await.result(persistence.runQuery(persistence.dal.countSteps), Inf)

    assertCountDataLoadedCorrectly()
  }

  it should "fail if no raw data has been downloaded and downloading is forbidden" in {
    intercept[DataMissingDownloadDisallowedException] {
      Await.result(persistencePopulator.loadCountData(SenateElection.`2016`, State.ACT, allowDownloading = false), Inf)
    }
  }

}

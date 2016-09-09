package au.id.tmm.senatedb.data.database

import au.id.tmm.senatedb.data.TestData
import au.id.tmm.senatedb.data.database.model.{CandidateOutcome, CountOutcomesPerCandidateRow}
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences.UsesDopData
import au.id.tmm.senatedb.model.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

class StoresCountDataSpec extends ImprovedFlatSpec with TestsPersistence with UsesDopData {

  "storing count data" should "store the step metadata" in {
    Await.result(persistence.storeCountData(parsedActCountData), Inf)

    val storedCountStepMetadata = Await.result(persistence.runQuery(persistence.dal.countSteps), Inf)
    val expectedCountStepMetadata = parsedActCountData.steps.map(_.stepRow)

    assert(storedCountStepMetadata === expectedCountStepMetadata)
  }

  it should "store the candidate transfers" in {
    Await.result(persistence.storeCountData(parsedActCountData), Inf)

    val storedTransfers = Await.result(persistence.runQuery(persistence.dal.countTransfersPerCandidate), Inf)
    val expectedTransfers = parsedActCountData.steps.flatMap(_.transfers)

    assert(storedTransfers === expectedTransfers)
  }

  it should "store the candidate outcomes" in {
    Await.result(persistence.storeCountData(parsedActCountData), Inf)

    val storedOutcomes = Await.result(persistence.runQuery(persistence.dal.outcomesPerCandidate), Inf).toSet
    val expectedOutcomes = parsedActCountData.outcomes

    assert(storedOutcomes === expectedOutcomes)
  }

  "the has count data check" should "return false if no count data has been loaded" in {
    val checkOutcome = Await.result(persistence.hasCountDataFor(testElection, State.ACT), Inf)

    assert(!checkOutcome)
  }

  it should "return true if count data has been loaded" in {
    Await.result(persistence.storeCountData(parsedActCountData), Inf)

    val checkOutcome = Await.result(persistence.hasCountDataFor(testElection, State.ACT), Inf)

    assert(checkOutcome)
  }

  "retrieving count data for a state at an election" should "retrieve what's been stored" in {
    Await.result(persistence.storeCountData(parsedActCountData), Inf)

    val storedCountData = Await.result(persistence.retrieveCountDataFor(testElection, State.ACT), Inf)

    assert(storedCountData === parsedActCountData)
  }

  it should "throw a NoSuchElementException if no data has been loaded" in {
    intercept[NoSuchElementException]{
      Await.result(persistence.retrieveCountDataFor(testElection, State.ACT), Inf)
    }
  }

  "retrieving the count outcome for a candidate" should "retrieve that candidate's outcome" in {
    Await.result(persistence.storeCountData(parsedActCountData), Inf)

    val zedsId = TestData.allActCandidates.filter(_.name == "SESELJA, Zed").head.candidateId

    val actualOutcome = Await.result(persistence.retrieveOutcomeFor(testElection, State.ACT, zedsId), Inf)
    val expectedOutcome = CountOutcomesPerCandidateRow(testElection.aecID, "ACT", zedsId, CandidateOutcome.ELECTED, 2, 29)

    assert(expectedOutcome === actualOutcome)
  }

  it should "throw a NoSuchElementException if no data has been loaded" in {
    val zedsId = TestData.allActCandidates.filter(_.name == "SESELJA, Zed").head.candidateId

    intercept[NoSuchElementException] {
      Await.result(persistence.retrieveOutcomeFor(testElection, State.ACT, zedsId), Inf)
    }
  }

  "deleting the count data" should "delete the count data" in {
    Await.result(persistence.storeCountData(parsedActCountData), Inf)

    Await.result(persistence.deleteCountDataFor(testElection, State.ACT), Inf)

    intercept[NoSuchElementException]{
      Await.result(persistence.retrieveCountDataFor(testElection, State.ACT), Inf)
    }
  }
}

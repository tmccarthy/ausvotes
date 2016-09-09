package au.id.tmm.senatedb.data.database

import java.util.NoSuchElementException

import au.id.tmm.senatedb.data.CountData
import au.id.tmm.senatedb.data.CountData.CountStepData
import au.id.tmm.senatedb.data.database.model.CountOutcomesPerCandidateRow
import au.id.tmm.senatedb.model.{SenateElection, State}

import scala.concurrent.Future

trait StoresCountData { this: Persistence =>

  import dal.driver.api._

  def storeCountData(countData: CountData): Future[Unit] = {
    val insertionQuery = (dal.countSteps ++= countData.steps.map(_.stepRow)) andThen
      (dal.countTransfersPerCandidate ++= countData.steps.flatMap(_.transfers)) andThen
      (dal.outcomesPerCandidate ++= countData.outcomes)

    execute(insertionQuery.transactionally).map(_ => Unit)
  }

  def hasCountDataFor(election: SenateElection, state: State): Future[Boolean] = {
    val query = dal.countStepsFor(election.aecID, state.shortName).exists

    runQuery(query)
  }

  def retrieveCountDataFor(election: SenateElection, state: State): Future[CountData] = {
    for {
      stepsMetadata <- runQuery(dal.countStepsFor(election.aecID, state.shortName))
      allTransfers <- runQuery(dal.countTransfersFor(election.aecID, state.shortName))
      outcomes <- runQuery(dal.outcomesPerCandidate)
    } yield {
      if (stepsMetadata.isEmpty) {
        throw new NoSuchElementException(s"No count data found for $state at $election")
      }

      val numCounts = stepsMetadata.map(_.count).max

      val lookupStepMetadataByCount = stepsMetadata.groupBy(_.count)
      val candidateTransfersByCount = allTransfers.groupBy(_.count)

      val steps = (1 to numCounts).toList.map(count => {
        val stepMetadata = lookupStepMetadataByCount(count).head
        val transfers = candidateTransfersByCount(count).toSet

        CountStepData(stepMetadata, transfers)
      })

      CountData(election, state, steps, outcomes.toSet)
    }
  }

  def retrieveOutcomeFor(election: SenateElection,
                         state: State,
                         candidateId: String
                        ): Future[CountOutcomesPerCandidateRow] = {
    runQuery(dal.outcomeForCandidate(election.aecID, state.shortName, candidateId))
      .map(_.headOption.getOrElse(throw new NoSuchElementException(
        s"Outcome not loaded for candidate with id $candidateId in $state at $election"))
      )
  }

  def deleteCountDataFor(election: SenateElection, state: State): Future[Unit] = {
    val deletionStatement = dal.countStepsFor(election.aecID, state.shortName).delete andThen
      dal.countTransfersFor(election.aecID, state.shortName).delete andThen
      dal.outcomesAtElection(election.aecID, state.shortName).delete

    execute(deletionStatement.transactionally).map(_ => Unit)
  }
}

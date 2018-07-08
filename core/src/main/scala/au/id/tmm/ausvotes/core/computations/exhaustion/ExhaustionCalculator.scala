package au.id.tmm.ausvotes.core.computations.exhaustion

import au.id.tmm.ausvotes.core.model.CountData
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion.NotExhausted
import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, NormalisedBallot}
import au.id.tmm.ausvotes.core.model.parsing.{Ballot, CandidatePosition}
import au.id.tmm.countstv.model.countsteps.{AllocationAfterIneligibles, DistributionCountStep, InitialAllocation}
import au.id.tmm.countstv.model.values.{Count, TransferValue}

import scala.annotation.tailrec

object ExhaustionCalculator {

  def exhaustionsOf(countData: CountData, ballots: Vector[(Ballot, NormalisedBallot)]): Map[Ballot, BallotExhaustion] = {

    val trackedBallots = ballots.map { case (ballot, normalisedBallot) => new TrackedBallot(ballot, normalisedBallot) }

    for (List(previousCountStep, countStep) <- countData.completedCount.countSteps.sliding(size = 2)) {
      assert(previousCountStep.count.increment == countStep.count)

      countStep match {
        case countStep: InitialAllocation[CandidatePosition] => {}
        case countStep: AllocationAfterIneligibles[CandidatePosition] => {
          trackedBallots.foreach { trackedBallot =>
            if (trackedBallot.currentPreference.exists(countStep.candidateStatuses.ineligibleCandidates)) {
              allocateToNextPreference(
                trackedBallot,
                countStep.count,
                countStep.candidateStatuses.ineligibleForPreferenceFlows,
                numElectedCandidates = 0,
              )
            }
          }
        }
        case countStep: DistributionCountStep[CandidatePosition] => {
          for {
            trackedBallot <- trackedBallots
          } {
            val distributionSource = countStep.distributionSource
            if (
              trackedBallot.currentPreference.contains(distributionSource.candidate) &&
                distributionSource.sourceCounts.contains(trackedBallot.allocatedAtCount)
            ) {
              val candidatesElectedThisStep = countStep.candidateStatuses.electedCandidates diff
                previousCountStep.candidateStatuses.electedCandidates

              trackedBallot.transferValue = distributionSource.transferValue
              allocateToNextPreference(
                trackedBallot,
                countStep.count,
                countStep.candidateStatuses.ineligibleForPreferenceFlows -- candidatesElectedThisStep,
                numElectedCandidates = countStep.candidateStatuses.electedCandidates.size,
              )
            }
          }
        }
      }
    }

    trackedBallots
      .map { trackedBallot =>
        trackedBallot.ballot -> trackedBallot.exhaustion
      }
      .toMap
  }

  @tailrec
  private def allocateToNextPreference(ballot: TrackedBallot,
                                       count: Count,
                                       candidatesIneligibleForPreferenceFlows: Set[CandidatePosition],
                                       numElectedCandidates: Int): Unit = {
    ballot.exhaustion match {
      case NotExhausted => {
        ballot.incrementCurrentPreference(count)

        ballot.currentPreference match {
          case Some(c) => {
            if (candidatesIneligibleForPreferenceFlows contains c) {
              allocateToNextPreference(ballot, count, candidatesIneligibleForPreferenceFlows, numElectedCandidates)
            }
          }
          case None => {
            ballot.exhaustion = BallotExhaustion.Exhausted(count, ballot.transferValue, numElectedCandidates)
          }
        }
      }
      case _ =>
    }

  }

  private final case class Allocation(candidatePosition: CandidatePosition, fromCount: Count)

  private final class TrackedBallot(val ballot: Ballot,
                                    val normalisedBallot: NormalisedBallot,

                                    var currentPreferenceIndex: Int = 0,
                                    var allocatedAtCount: Count = Count.ofIneligibleCandidateHandling,

                                    var transferValue: TransferValue = TransferValue(1d),

                                    var exhaustion: BallotExhaustion = BallotExhaustion.NotExhausted,
                                   ) {
    def currentAllocation: Option[Allocation] = currentPreference.map(Allocation(_, allocatedAtCount))

    def currentPreference: Option[CandidatePosition] = normalisedBallot.canonicalOrder.lift(currentPreferenceIndex)

    def incrementCurrentPreference(currentCount: Count): Unit = {
      currentPreferenceIndex = currentPreferenceIndex + 1
      allocatedAtCount = currentCount
    }
  }
}

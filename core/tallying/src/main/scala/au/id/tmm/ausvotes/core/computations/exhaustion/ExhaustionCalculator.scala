package au.id.tmm.ausvotes.core.computations.exhaustion

import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion.NotExhausted
import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, NormalisedBallot}
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallot, SenateCandidate, SenateCountData}
import au.id.tmm.countstv.model.countsteps._
import au.id.tmm.countstv.model.values.{Count, TransferValue}

import scala.annotation.tailrec

object ExhaustionCalculator {

  def exhaustionsOf(
                     countData: SenateCountData,
                     ballots: Vector[(SenateBallot, NormalisedBallot)],
                   ): Map[SenateBallot, BallotExhaustion] = {

    val trackedBallots = ballots.map { case (ballot, normalisedBallot) => new TrackedBallot(ballot, normalisedBallot) }

    for (List(previousCountStep, countStep) <- countData.completedCount.countSteps.sliding(size = 2)) {
      assert(previousCountStep.count.increment == countStep.count)

      countStep match {
        case countStep: InitialAllocation[SenateCandidate] => {}
        case countStep: AllocationAfterIneligibles[SenateCandidate] => {
          val ineligibleCandidates = countStep.candidateStatuses.ineligibleCandidates
          val ineligibleForPreferenceFlows = countStep.candidateStatuses.ineligibleForPreferenceFlows

          trackedBallots.foreach { trackedBallot =>
            if (trackedBallot.currentPreference.exists(ineligibleCandidates)) {
              allocateToNextPreference(
                trackedBallot,
                countStep.count,
                ineligibleForPreferenceFlows,
                numElectedCandidates = 0,
              )
            }
          }
        }
        case countStep: DistributionCountStep[SenateCandidate] => {
          val candidatesElectedThisStep = countStep.candidateStatuses.electedCandidates.toSet diff
            previousCountStep.candidateStatuses.electedCandidates.toSet

          val candidatesIneligibleForPreferenceFlows = countStep.candidateStatuses.ineligibleForPreferenceFlows -- candidatesElectedThisStep

          for {
            trackedBallot <- trackedBallots
          } {
            val distributionSource = countStep.distributionSource
            if (
              trackedBallot.currentPreference.contains(distributionSource.candidate) &&
                distributionSource.sourceCounts.contains(trackedBallot.allocatedAtCount)
            ) {

              trackedBallot.transferValue = distributionSource.transferValue
              allocateToNextPreference(
                trackedBallot,
                countStep.count,
                candidatesIneligibleForPreferenceFlows,
                numElectedCandidates = countStep.candidateStatuses.electedCandidates.size,
              )
            }
          }
        }
        case ElectedNoSurplusCountStep(_, _, _, _, _) | ExcludedNoVotesCountStep(_, _, _, _) =>
          //noinspection NotImplementedCode
          ??? // TODO this needs to be accounted for
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
                                       candidatesIneligibleForPreferenceFlows: Set[SenateCandidate],
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

  private final case class Allocation(candidatePosition: SenateCandidate, fromCount: Count)

  private final class TrackedBallot(val ballot: SenateBallot,
                                    val normalisedBallot: NormalisedBallot,

                                    var currentPreferenceIndex: Int = 0,
                                    var allocatedAtCount: Count = Count.ofIneligibleCandidateHandling,

                                    var transferValue: TransferValue = TransferValue(1d),

                                    var exhaustion: BallotExhaustion = BallotExhaustion.NotExhausted,
                                   ) {
    def currentAllocation: Option[Allocation] = currentPreference.map(Allocation(_, allocatedAtCount))

    def currentPreference: Option[SenateCandidate] =
      if (currentPreferenceIndex < normalisedBallot.canonicalOrder.size) {
        Some(normalisedBallot.canonicalOrder.apply(currentPreferenceIndex))
      } else {
        None
      }

    def incrementCurrentPreference(currentCount: Count): Unit = {
      currentPreferenceIndex = currentPreferenceIndex + 1
      allocatedAtCount = currentCount
    }
  }
}

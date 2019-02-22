package au.id.tmm.ausvotes.core.computations.exhaustion

import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion
import au.id.tmm.ausvotes.core.model.computation.BallotExhaustion.NotExhausted
import au.id.tmm.ausvotes.model.stv.{Ballot, CountData, NormalisedBallot, StvCandidate}
import au.id.tmm.countstv.model.countsteps._
import au.id.tmm.countstv.model.values.{Count, TransferValue}

import scala.annotation.tailrec

object ExhaustionCalculator {

  def exhaustionsOf[E, J, I](
                              countData: CountData[E],
                              ballots: Vector[(Ballot[E, J, I], NormalisedBallot[E])],
                            ): Map[Ballot[E, J, I], BallotExhaustion] = {

    val trackedBallots = ballots.map { case (ballot, normalisedBallot) => new TrackedBallot(ballot, normalisedBallot) }

    for (List(previousCountStep, countStep) <- countData.completedCount.countSteps.sliding(size = 2)) {
      assert(previousCountStep.count.increment == countStep.count)

      countStep match {
        case countStep: InitialAllocation[StvCandidate[E]] => {}
        case countStep: AllocationAfterIneligibles[StvCandidate[E]] => {
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
        case countStep: DistributionCountStep[StvCandidate[E]] => {
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
  private def allocateToNextPreference[E, J, I](ballot: TrackedBallot[E, J, I],
                                                count: Count,
                                                candidatesIneligibleForPreferenceFlows: Set[StvCandidate[E]],
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

  private final case class Allocation[E](candidatePosition: StvCandidate[E], fromCount: Count)

  private final class TrackedBallot[E, J, I](val ballot: Ballot[E, J, I],
                                             val normalisedBallot: NormalisedBallot[E],

                                             var currentPreferenceIndex: Int = 0,
                                             var allocatedAtCount: Count = Count.ofIneligibleCandidateHandling,

                                             var transferValue: TransferValue = TransferValue(1d),

                                             var exhaustion: BallotExhaustion = BallotExhaustion.NotExhausted,
                                            ) {
    private val canonicalOrder = normalisedBallot.canonicalOrder.getOrElse(Vector.empty)

    def currentAllocation: Option[Allocation[E]] = currentPreference.map(Allocation(_, allocatedAtCount))

    def currentPreference: Option[StvCandidate[E]] =
      if (currentPreferenceIndex < canonicalOrder.size) {
        Some(canonicalOrder.apply(currentPreferenceIndex))
      } else {
        None
      }

    def incrementCurrentPreference(currentCount: Count): Unit = {
      currentPreferenceIndex = currentPreferenceIndex + 1
      allocatedAtCount = currentCount
    }
  }
}

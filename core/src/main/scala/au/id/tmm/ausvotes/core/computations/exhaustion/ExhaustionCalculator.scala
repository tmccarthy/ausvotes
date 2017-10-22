package au.id.tmm.ausvotes.core.computations.exhaustion

import au.id.tmm.ausvotes.core.model.CountData
import au.id.tmm.ausvotes.core.model.CountStep.DistributionStep
import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, NormalisedBallot}
import au.id.tmm.ausvotes.core.model.parsing.{Ballot, CandidatePosition}

import scala.annotation.tailrec
import scala.collection.mutable

object ExhaustionCalculator {

  def exhaustionsOf(countData: CountData, ballots: Vector[(Ballot, NormalisedBallot)]): Map[Ballot, BallotExhaustion] = {
    val trackedBallots = ballots.map {
      case (ballot, normalised) =>
        val trackedBallot = new TrackedBallot(ballot, normalised)

        skipIneligibleFirstPreferences(countData.ineligibleCandidates, trackedBallot)

        trackedBallot
    }

    val ballotsPerAllocation = new mutable.HashMap[Allocation, mutable.Set[TrackedBallot]]
      with mutable.MultiMap[Allocation, TrackedBallot]

    trackedBallots.toStream
      .filter(_.currentAllocation.isDefined)
      .foreach(ballot => ballotsPerAllocation.addBinding(ballot.currentAllocation.get, ballot))

    trackBallotsThroughDistributionSteps(
      countData.ineligibleCandidates,
      countData.distributionSteps,
      ballotsPerAllocation,
    )

    trackedBallots.toStream
      .map { trackedBallot =>
        trackedBallot.ballot -> trackedBallot.exhaustion
      }
      .toMap
  }

  @tailrec
  private def skipIneligibleFirstPreferences(ineligibleCandidates: Set[CandidatePosition],
                                             trackedBallot: TrackedBallot): Unit = {
    trackedBallot.currentPreference match {
      case Some(currentPreference) => {
        if (ineligibleCandidates contains currentPreference) {
          trackedBallot.currentPreferenceIndex = trackedBallot.currentPreferenceIndex + 1

          skipIneligibleFirstPreferences(ineligibleCandidates, trackedBallot)
        }
      }
      case None => {
        // We've gone through all this ballot's initial preferences and they're all ineligible
        trackedBallot.exhaustion = BallotExhaustion.ExhaustedBeforeInitialAllocation
      }
    }
  }

  @tailrec
  private def trackBallotsThroughDistributionSteps(ineligibleCandidates: Set[CandidatePosition],
                                                   distributionSteps: Vector[DistributionStep],
                                                   ballotsPerAllocation: mutable.MultiMap[Allocation, TrackedBallot]): Unit = {
    if (distributionSteps.isEmpty) {
      return
    }

    val step = distributionSteps.head

    val allocationsReallocatedAtThisStep = step.source.sourceCounts.map(count => Allocation(step.source.sourceCandidate, count))

    val ballotsReallocatedAtThisStep = allocationsReallocatedAtThisStep.flatMap(allocation => {
      ballotsPerAllocation.getOrElse(allocation, mutable.Set())
    })

    allocationsReallocatedAtThisStep.foreach(ballotsPerAllocation.remove)

    val candidatesIneligibleForPreferenceFlows = ineligibleCandidates ++
      step.excluded ++
      (step.elected diff step.electedThisCount)

    ballotsReallocatedAtThisStep.foreach(ballot => {
      ballot.transferValue = step.source.transferValue

      allocateToNextPreference(ballot, step.count, candidatesIneligibleForPreferenceFlows, step.elected.size)
    })

    ballotsReallocatedAtThisStep.foreach(ballot => {
      ballot.currentAllocation.foreach(allocation => ballotsPerAllocation.addBinding(allocation, ballot))
    })

    trackBallotsThroughDistributionSteps(ineligibleCandidates, distributionSteps.tail, ballotsPerAllocation)
  }

  @tailrec
  private def allocateToNextPreference(ballot: TrackedBallot,
                                       count: Int,
                                       candidatesIneligibleForPreferenceFlows: Set[CandidatePosition],
                                       numElectedCandidates: Int): Unit = {
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

  private final case class Allocation(candidatePosition: CandidatePosition, fromCount: Int)

  private final class TrackedBallot(val ballot: Ballot,
                                    val normalisedBallot: NormalisedBallot,

                                    var currentPreferenceIndex: Int = 0,
                                    var allocatedAtCount: Int = 1,

                                    var transferValue: Double = 1d,

                                    var exhaustion: BallotExhaustion = BallotExhaustion.NotExhausted,
                                   ) {
    def currentAllocation: Option[Allocation] = currentPreference.map(Allocation(_, allocatedAtCount))

    def currentPreference: Option[CandidatePosition] = normalisedBallot.canonicalOrder.lift(currentPreferenceIndex)

    def incrementCurrentPreference(currentCount: Int): Unit = {
      currentPreferenceIndex = currentPreferenceIndex + 1
      allocatedAtCount = currentCount
    }
  }
}

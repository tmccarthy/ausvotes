package au.id.tmm.senatedb.computations.exhaustion

import au.id.tmm.senatedb.model.CountData
import au.id.tmm.senatedb.model.CountStep.DistributionStep
import au.id.tmm.senatedb.model.computation.{BallotExhaustion, NormalisedBallot}
import au.id.tmm.senatedb.model.parsing.{Ballot, CandidatePosition}

import scala.annotation.tailrec
import scala.collection.mutable

object ExhaustionCalculator {

  def exhaustionsOf(countData: CountData, ballots: Vector[(Ballot, NormalisedBallot)]): Map[Ballot, BallotExhaustion] = {
    val trackedBallots = ballots.map {
      case (ballot, normalised) => new TrackedBallot(ballot, normalised)
    }

    val ballotsPerAllocation = new mutable.HashMap[Allocation, mutable.Set[TrackedBallot]]
      with mutable.MultiMap[Allocation, TrackedBallot]

    trackedBallots.toStream
      .filter(_.currentAllocation.isDefined)
      .foreach(ballot => ballotsPerAllocation.addBinding(ballot.currentAllocation.get, ballot))

    trackBallotsThroughDistributionSteps(countData.distributionSteps, ballotsPerAllocation)

    trackedBallots.toStream
      .map { trackedBallot =>
        trackedBallot.ballot -> trackedBallot.toExhaustion
      }
      .toMap
  }

  @tailrec
  private def trackBallotsThroughDistributionSteps(distributionSteps: Vector[DistributionStep],
                                                   ballotsPerAllocation: mutable.MultiMap[Allocation, TrackedBallot]): Unit = {
    val step = distributionSteps.head

    val allocationsReallocatedAtThisStep = step.source.sourceCounts.map(count => Allocation(step.source.sourceCandidate, count))

    val ballotsReallocatedAtThisStep = allocationsReallocatedAtThisStep.flatMap(allocation => {
      ballotsPerAllocation.getOrElse(allocation, mutable.Set())
    })

    allocationsReallocatedAtThisStep.foreach(ballotsPerAllocation.remove)

    ballotsReallocatedAtThisStep.foreach(ballot => {
      ballot.transferValue = step.source.transferValue

      allocateToNextPreference(ballot, step.count, step.elected, step.excluded)
    })

    ballotsReallocatedAtThisStep.foreach(ballot => {
      ballot.currentAllocation.foreach(allocation => ballotsPerAllocation.addBinding(allocation, ballot))
    })

    val remainingSteps = distributionSteps.tail

    if (remainingSteps.nonEmpty) {
      trackBallotsThroughDistributionSteps(remainingSteps, ballotsPerAllocation)
    }
  }

  @tailrec
  private def allocateToNextPreference(ballot: TrackedBallot,
                                       count: Int,
                                       electedCandidates: Set[CandidatePosition],
                                       excludedCandidates: Set[CandidatePosition]): Unit = {
    ballot.currentPreferenceIndex = ballot.currentPreferenceIndex + 1
    ballot.allocatedAtCount = count

    ballot.currentPreference match {
      case Some(c) => {
        if (electedCandidates.contains(c) || excludedCandidates.contains(c)) {
          allocateToNextPreference(ballot, count, electedCandidates, excludedCandidates)
        }
      }
      case None => {
        ballot.isExhausted = true
        ballot.exhaustedAtCount = Some(count)
        ballot.candidatesElectedAtExhaustion = Some(electedCandidates.size)
      }
    }
  }

  private final case class Allocation(candidatePosition: CandidatePosition, fromCount: Int)

  private final class TrackedBallot(val ballot: Ballot,
                                    val normalisedBallot: NormalisedBallot,

                                    var currentPreferenceIndex: Int = 0,
                                    var allocatedAtCount: Int = 1,

                                    var transferValue: Double = 1d,

                                    var isExhausted: Boolean = false,
                                    var exhaustedAtCount: Option[Int] = None,
                                    var candidatesElectedAtExhaustion: Option[Int] = None
                                   ) {
    def currentAllocation: Option[Allocation] = currentPreference.map(Allocation(_, allocatedAtCount))

    def currentPreference: Option[CandidatePosition] = normalisedBallot.canonicalOrder.lift(currentPreferenceIndex)

    def toExhaustion: BallotExhaustion = {
      if (isExhausted) {
        BallotExhaustion.Exhausted(exhaustedAtCount.get, transferValue, candidatesElectedAtExhaustion.get)
      } else {
        BallotExhaustion.NotExhausted
      }
    }
  }
}

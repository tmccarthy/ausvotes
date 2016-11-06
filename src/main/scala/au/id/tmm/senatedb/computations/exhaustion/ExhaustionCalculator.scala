package au.id.tmm.senatedb.computations.exhaustion

import au.id.tmm.senatedb.model.computation.{BallotExhaustion, NormalisedBallot}
import au.id.tmm.senatedb.model.parsing.{Ballot, CandidatePosition}
import au.id.tmm.senatedb.model.{CountData, CountStep}

import scala.annotation.tailrec

object ExhaustionCalculator {

  def exhaustionsOf(countData: CountData, ballots: Vector[(Ballot, NormalisedBallot)]): Map[Ballot, BallotExhaustion] = {
    val trackedBallots = ballots.map {
      case (ballot, normalised) => new TrackedBallot(ballot, normalised)
    }

    countData.steps.foreach { step =>
      trackedBallots.foreach(handleBallotForCountStep(step, _))
    }

    trackedBallots.toStream
      .map { trackedBallot =>
        trackedBallot.ballot -> trackedBallot.toExhaustion
      }
      .toMap
  }

  private def handleBallotForCountStep(countStep: CountStep.DistributionStep,
                                       ballot: TrackedBallot): Unit = {
    if (ballot isDistributedBy countStep) {
      ballot.transferValue = countStep.source.transferValue

      allocateToNextPreference(ballot, countStep.count, countStep.elected, countStep.excluded)
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

  private final class TrackedBallot(val ballot: Ballot,
                                    val normalisedBallot: NormalisedBallot,

                                    var currentPreferenceIndex: Int = 0,
                                    var allocatedAtCount: Int = 1,

                                    var transferValue: Double = 1d,

                                    var isExhausted: Boolean = false,
                                    var exhaustedAtCount: Option[Int] = None,
                                    var candidatesElectedAtExhaustion: Option[Int] = None
                                   ) {
    def currentPreference: Option[CandidatePosition] = normalisedBallot.canonicalOrder.lift(currentPreferenceIndex)

    def isDistributedBy(distributionStep: CountStep.DistributionStep): Boolean = {
      (currentPreference contains distributionStep.source.sourceCandidate) &&
        (distributionStep.source.sourceCounts contains allocatedAtCount)
    }

    def toExhaustion: BallotExhaustion = {
      if (isExhausted) {
        BallotExhaustion.Exhausted(exhaustedAtCount.get, transferValue, candidatesElectedAtExhaustion.get)
      } else {
        BallotExhaustion.NotExhausted
      }
    }
  }
}

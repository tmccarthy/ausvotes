package au.id.tmm.ausvotes.tasks.compare_recounts

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.tasks.compare_recounts.CountComparison.Mismatch
import au.id.tmm.countstv.model
import au.id.tmm.countstv.model.countsteps.DistributionCountStep
import au.id.tmm.countstv.model.values.Count
import au.id.tmm.countstv.model.{CandidateVoteCounts, CompletedCount, VoteCount}
import au.id.tmm.utilities.geo.australia.State

import scala.collection.immutable.SortedSet

final case class CountComparison(
                                  election: SenateElection,
                                  state: State,

                                  canonicalCount: CompletedCount[Candidate],
                                  computedCount: CompletedCount[Candidate],

                                  // TODO really need a way of indicating if a category of mismatch doesn't exist, ie it was correct
                                  mismatches: SortedSet[Mismatch],
                                ) {
  def mismatchSignificance: Int = mismatches.map(Mismatch.importanceOf).sum
}

object CountComparison {

  sealed trait Mismatch

  object Mismatch {
    final case class CandidateStatusType(candidate: Candidate, statusInCanonical: model.CandidateStatus, statusInComputed: model.CandidateStatus) extends Mismatch
    final case class CandidateStatus(candidate: Candidate, statusInCanonical: model.CandidateStatus, statusInComputed: model.CandidateStatus) extends Mismatch

    final case class FinalRoundingError(canonicalRoundingError: VoteCount, roundingErrorInComputed: VoteCount) extends Mismatch
    final case class FinalExhausted(canonicalRoundingError: VoteCount, roundingErrorInComputed: VoteCount) extends Mismatch

    final case class ActionAtCount(count: Count, canonicalAction: Option[ActionAtCount.Action], computedAction: Option[ActionAtCount.Action]) extends Mismatch
    object ActionAtCount {
      sealed trait Action
      object Action {
        case object InitialAllocation extends Action
        case object AllocationAfterIneligibles extends Action
        final case class Distribution(source: DistributionCountStep.Source[Candidate]) extends Action
        final case class ExcludedNoVotes(source: Candidate) extends Action
        final case class ElectedNoSurplus(source: Candidate, sourceCounts: Set[Count]) extends Action
      }
    }

    final case class VoteCountAtCount(
                                       misallocatedBallotsPerCount: Map[Count, VoteCount],
                                       firstBadCount: Count,
                                       canonicalCandidateVoteCountsAtFirstBadCount: CandidateVoteCounts[Candidate],
                                       candidateVoteCountsInComputedAtFirstBadCount: CandidateVoteCounts[Candidate],
                                     ) extends Mismatch

    def importanceOf(mismatch: Mismatch): Int = mismatch match {
      case _: CandidateStatusType => 1000
      case _: CandidateStatus => 500
      case _: FinalRoundingError => 250
      case _: FinalExhausted => 125
      case _: ActionAtCount => 60
      case _: VoteCountAtCount => 30
    }

    implicit val ordering: Ordering[Mismatch] = Ordering.by(importanceOf)
  }

}

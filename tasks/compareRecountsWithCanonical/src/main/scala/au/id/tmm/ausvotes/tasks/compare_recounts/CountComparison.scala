package au.id.tmm.ausvotes.tasks.compare_recounts

import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateElectionForState}
import au.id.tmm.ausvotes.tasks.compare_recounts.CountComparison.Mismatch
import au.id.tmm.ausvotes.tasks.compare_recounts.CountComparison.Mismatch.VoteCountAtCount.VoteCountMisallocationSummary
import au.id.tmm.countstv.model
import au.id.tmm.countstv.model.countsteps.DistributionCountStep
import au.id.tmm.countstv.model.values.Count
import au.id.tmm.countstv.model.{CandidateVoteCounts, CompletedCount, VoteCount}

import scala.collection.immutable.{SortedMap, SortedSet}

final case class CountComparison(
                                  election: SenateElectionForState,

                                  canonicalCount: CompletedCount[SenateCandidate],
                                  computedCount: CompletedCount[SenateCandidate],

                                  candidateStatusTypeMismatches: SortedSet[Mismatch.CandidateStatusType],
                                  candidateStatusMismatches: SortedSet[Mismatch.CandidateStatus],

                                  finalRoundingErrorMismatch: Option[Mismatch.FinalRoundingError],
                                  finalExhaustedMismatch: Option[Mismatch.FinalExhausted],

                                  actionAtCountMismatch: SortedSet[Mismatch.ActionAtCount],
                                  voteCountAtCountMismatch: SortedSet[Mismatch.VoteCountAtCount],
                                ) {

  def mismatches: Set[Mismatch] = candidateStatusTypeMismatches ++ candidateStatusMismatches ++
    finalRoundingErrorMismatch ++ finalExhaustedMismatch ++ actionAtCountMismatch ++ voteCountAtCountMismatch

  def mismatchSignificance: Int = mismatches.map(Mismatch.importanceOf).sum
}

object CountComparison {

  sealed trait Mismatch

  object Mismatch {
    final case class CandidateStatusType(candidate: SenateCandidate, statusInCanonical: model.CandidateStatus, statusInComputed: model.CandidateStatus) extends Mismatch
    object CandidateStatusType {
      implicit val ordering: Ordering[CandidateStatusType] = Ordering.by(_.candidate)
    }
    final case class CandidateStatus(candidate: SenateCandidate, statusInCanonical: model.CandidateStatus, statusInComputed: model.CandidateStatus) extends Mismatch
    object CandidateStatus {
      implicit val ordering: Ordering[CandidateStatus] = Ordering.by(_.candidate)
    }

    final case class FinalRoundingError(canonicalRoundingError: VoteCount, roundingErrorInComputed: VoteCount) extends Mismatch
    final case class FinalExhausted(canonicalExhausted: VoteCount, exhaustedInComputed: VoteCount) extends Mismatch

    final case class ActionAtCount(count: Count, canonicalAction: Option[ActionAtCount.Action], computedAction: Option[ActionAtCount.Action]) extends Mismatch
    object ActionAtCount {
      sealed trait Action
      object Action {
        case object InitialAllocation extends Action
        case object AllocationAfterIneligibles extends Action
        final case class Distribution(source: DistributionCountStep.Source[SenateCandidate]) extends Action
        final case class ExcludedNoVotes(source: SenateCandidate) extends Action
        final case class ElectedNoSurplus(source: SenateCandidate, sourceCounts: Set[Count]) extends Action
      }

      implicit val ordering: Ordering[ActionAtCount] = Ordering.by(_.count)
    }

    final case class VoteCountAtCount(
                                       misallocatedBallotsPerCount: SortedMap[Count, VoteCountMisallocationSummary],
                                       firstBadCount: Count,
                                       canonicalCandidateVoteCountsAtFirstBadCount: CandidateVoteCounts[SenateCandidate],
                                       candidateVoteCountsInComputedAtFirstBadCount: CandidateVoteCounts[SenateCandidate],
                                     ) extends Mismatch {
      def diff: CandidateVoteCounts[SenateCandidate] =
        candidateVoteCountsInComputedAtFirstBadCount - canonicalCandidateVoteCountsAtFirstBadCount
    }
    object VoteCountAtCount {
      implicit val ordering: Ordering[VoteCountAtCount] = Ordering.by(_.firstBadCount)

      final case class VoteCountMisallocationSummary(
                                                      totalMisallocation: VoteCount,
                                                      worstMismatchCandidate: SenateCandidate,
                                                      mismatchForWorstCandidate: VoteCount,
                                                      mismatchForExhausted: VoteCount,
                                                      mismatchForRoundingError: VoteCount,
                                                    )
    }

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

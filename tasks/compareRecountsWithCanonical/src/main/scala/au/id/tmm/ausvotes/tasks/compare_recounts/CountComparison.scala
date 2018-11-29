package au.id.tmm.ausvotes.tasks.compare_recounts

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.tasks.compare_recounts.CountComparison.Mismatch
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes}
import au.id.tmm.countstv.model.{CandidateVoteCounts, CompletedCount, VoteCount}
import au.id.tmm.utilities.geo.australia.State

import scala.collection.immutable.SortedSet

final case class CountComparison(
                                  election: SenateElection,
                                  state: State,

                                  canonicalCount: CompletedCount[Candidate],
                                  computedCount: CompletedCount[Candidate],

                                  mismatches: SortedSet[Mismatch],
                                )

object CountComparison {

  sealed trait Mismatch

  object Mismatch {
    final case class CandidateStatusType(candidate: Candidate, statusInCanonical: CandidateStatus, statusInComputed: CandidateStatus) extends Mismatch
    final case class CandidateStatus(candidate: Candidate, statusInCanonical: CandidateStatus, statusInComputed: CandidateStatus) extends Mismatch

    final case class FinalRoundingError(canonicalRoundingError: VoteCount, roundingErrorInComputed: VoteCount) extends Mismatch
    final case class FinalExhausted(canonicalRoundingError: VoteCount, roundingErrorInComputed: VoteCount) extends Mismatch

    final case class CandidateVoteCountsBallots(
                                                 misallocatedBallotsPerCount: Map[Count, NumPapers],
                                                 firstBadCount: Count,
                                                 canonicalCandidateVoteCountsAtFirstBadCount: CandidateVoteCounts[Candidate],
                                                 candidateVoteCountsInComputedAtFirstBadCount: CandidateVoteCounts[Candidate],
                                               ) extends Mismatch

    final case class CandidateVoteCountsVotes(
                                               misallocatedVotesPerCount: Map[Count, NumVotes],
                                               firstBadCount: Count,
                                               canonicalCandidateVoteCountsAtFirstBadCount: CandidateVoteCounts[Candidate],
                                               candidateVoteCountsInComputedAtFirstBadCount: CandidateVoteCounts[Candidate],
                                             ) extends Mismatch

    private def importanceOf(mismatch: Mismatch): Int = mismatch match {
      case _: CandidateStatusType => 1
      case _: CandidateStatus => 2
      case _: FinalRoundingError => 3
      case _: FinalExhausted => 4
      case _: CandidateVoteCountsBallots => 5
      case _: CandidateVoteCountsVotes => 6
    }

    implicit val ordering: Ordering[Mismatch] = Ordering.by(importanceOf)
  }

}

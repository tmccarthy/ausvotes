package au.id.tmm.ausvotes.tasks.compare_recounts

import au.id.tmm.ausvotes.model.federal.senate.SenateCandidate
import au.id.tmm.ausvotes.tasks.compare_recounts.CountComparison.Mismatch
import au.id.tmm.ausvotes.tasks.compare_recounts.CountComparison.Mismatch.ActionAtCount.Action
import au.id.tmm.countstv.model.values.{Count, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateVoteCounts, VoteCount}

object RenderCountComparison {

  private def indent(strings: List[String]): List[String] = strings.map("  " + _)

  def render(countComparison: CountComparison): List[String] = {
    List(
      s"Election: ${countComparison.election.election.name}",
      s"State: ${countComparison.election.state.toNiceString.capitalize}"
    ) ++ indent(
      renderMismatchesOrSuccess("Candidate status types", countComparison.candidateStatusTypeMismatches.toList) ++
        renderMismatchesOrSuccess("Candidate statuses", countComparison.candidateStatusMismatches.toList) ++
        renderMismatchesOrSuccess("Rounding error", countComparison.finalRoundingErrorMismatch.toList) ++
        renderMismatchesOrSuccess("Exhausted votes", countComparison.finalExhaustedMismatch.toList) ++
        renderMismatchesOrSuccess("Count actions", countComparison.actionAtCountMismatch.toList) ++
        renderMismatchesOrSuccess("Vote counts", countComparison.voteCountAtCountMismatch.toList)
    )
  }

  private def renderMismatchesOrSuccess(name: String, mismatches: List[Mismatch]): List[String] = mismatches match {
    case Nil => List(good(name))
    case mismatches @ _ :: _ => List(bad(name)) ++ indent(mismatches.flatMap(mismatchDescription))
  }

  private def good(string: String): String = s"✅ $string"
  private def bad(string: String): String = s"❌ $string"

  private def mismatchDescription(mismatch: Mismatch): List[String] = mismatch match {
    case Mismatch.CandidateStatusType(candidate, statusInCanonical, statusInComputed) =>
      List(
        s"Candidate: ${candidateDescription(candidate)}",
        s"Expected status: ${statusTypeDescription(statusInCanonical)}",
        s"Computed status: ${statusTypeDescription(statusInComputed)}",
      )

    case Mismatch.CandidateStatus(candidate, statusInCanonical, statusInComputed) =>
      List(
        s"Candidate: ${candidateDescription(candidate)}",
        s"Expected status: ${statusDescription(statusInCanonical)}",
        s"Computed status: ${statusDescription(statusInComputed)}",
      )

    case Mismatch.FinalRoundingError(canonicalRoundingError, roundingErrorInComputed) =>
      List(
        s"Expected rounding error: ${renderVoteCount(canonicalRoundingError)}",
        s"Computed rounding error: ${renderVoteCount(roundingErrorInComputed)}",
      )

    case Mismatch.FinalExhausted(canonicalExhausted, roundingErrorInExhausted) =>
      List(
        s"Expected exhausted: ${renderVoteCount(canonicalExhausted)}",
        s"Computed exhausted: ${renderVoteCount(roundingErrorInExhausted)}",
      )

    case Mismatch.ActionAtCount(count, canonicalAction, computedAction) =>
      List(s"Count ${count.asInt}") ++
        List("Expected:") ++ indent(actionDescription(canonicalAction)) ++
        List("Computed:") ++ indent(actionDescription(computedAction))

    case m @ Mismatch.VoteCountAtCount(misallocatedBallotsPerCount, firstBadCount, _, _) =>
      List(s"First bad count ${firstBadCount.asInt}") ++
        List("Diffs at first bad count") ++ indent(renderDiff(m.diff)) ++
        List("Misallocated ballots per count") ++
        indent(
          misallocatedBallotsPerCount
            .toList
            .filter { case (_, voteCountMisallocationSummary) => voteCountMisallocationSummary.totalMisallocation != VoteCount.zero }
            .map { case (count, voteCountMisallocationSummary) => renderVoteCountMismatchSummary(count, voteCountMisallocationSummary) }
        )
  }

  private def candidateDescription(candidate: SenateCandidate): String =
    s"${candidate.candidateDetails.id.asInt} (${candidate.candidateDetails.name.surname}, ${candidate.candidateDetails.name.givenNames})"

  private def statusTypeDescription(status: CandidateStatus): String = status match {
    case CandidateStatus.Remaining => "Remaining"
    case CandidateStatus.Ineligible => "Ineligible"
    case _: CandidateStatus.Elected => "Elected"
    case _: CandidateStatus.Excluded => "Excluded"
  }

  private def statusDescription(status: CandidateStatus): String = status match {
    case CandidateStatus.Remaining => "Remaining"
    case CandidateStatus.Ineligible => "Ineligible"
    case CandidateStatus.Elected(ordinalElected, electedAtCount) => s"Elected ${ordinalDescription(ordinalElected)} at count ${electedAtCount.asInt}"
    case CandidateStatus.Excluded(ordinalExcluded, excludedAtCount) => s"Excluded ${ordinalDescription(ordinalExcluded)} at count ${excludedAtCount.asInt}"
  }

  private def ordinalDescription(ordinal: Ordinal): String = {
    val ordinalOneIndexed = ordinal.asInt + 1

    val suffix = ordinalOneIndexed % 10 match {
      case 1 => "st"
      case 2 => "nd"
      case 3 => "rd"
      case _ => "th"
    }

    s"$ordinalOneIndexed$suffix"
  }

  private def renderVoteCountMismatchSummary(count: Count, summary: Mismatch.VoteCountAtCount.VoteCountMisallocationSummary): String =
    s"${renderVoteCount(summary.totalMisallocation)} at count ${count.asInt} (${renderVoteCount(summary.mismatchForWorstCandidate)} for ${renderCandidate(summary.worstMismatchCandidate)}, ${renderVoteCount(summary.mismatchForExhausted)} for exhausted, ${renderVoteCount(summary.mismatchForRoundingError)} for rounding error)"

  private def renderCandidate(candidate: SenateCandidate): String =
    s"${candidate.candidateDetails.name.surname}, ${candidate.candidateDetails.name.givenNames}"

  private def renderVoteCount(count: VoteCount): String = s"${count.numPapers.asLong} papers, ${count.numVotes.asDouble} votes"

  private def actionDescription(action: Option[Mismatch.ActionAtCount.Action]): List[String] = action match {
    case Some(Action.InitialAllocation) => List("Initial allocation")
    case Some(Action.AllocationAfterIneligibles) => List("Allocation away from ineligibles")
    case Some(Action.Distribution(source)) =>
      List(
        "Distribution",
        s"Source: ${candidateDescription(source.candidate)}",
        s"Reason: ${source.candidateDistributionReason}",
        s"Transfer value: ${source.transferValue.factor}",
        s"Source counts: ${source.sourceCounts.toList.map(_.asInt).sorted.mkString(", ")}",
      )

    case Some(Action.ExcludedNoVotes(source)) =>
      List(
        "Exclusion with no votes",
        s"Source: ${candidateDescription(source)}",
      )

    case Some(Action.ElectedNoSurplus(source, sourceCounts)) =>
      List(
        "Election with no votes",
        s"Source: ${candidateDescription(source)}",
        s"Source counts: ${sourceCounts.toList.map(_.asInt).sorted.mkString(", ")}",
      )

    case None => List("None")
  }

  private def renderDiff(diff: CandidateVoteCounts[SenateCandidate]): List[String] =
    diff.perCandidate.toList
      .filter { case (_, voteCount) => voteCount != VoteCount.zero }
      .sortBy { case (candidate, _) => candidate.position }
      .map { case (candidate, voteCount) => s"${renderVoteCount(voteCount)} for ${candidateDescription(candidate)}" } ++
      List(
        s"${renderVoteCount(diff.roundingError)} for rounding error",
        s"${renderVoteCount(diff.exhausted)} for exhausted",
      )

}

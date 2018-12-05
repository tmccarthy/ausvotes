package au.id.tmm.ausvotes.core.parsing.countdata

import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.core.parsing.countdata.DistributionComment.{ElectedLastRemaining, ElectedWithQuotaNoSurplus, ElectedWithSurplus, Excluded}
import au.id.tmm.countstv.model.CandidateDistributionReason
import au.id.tmm.countstv.model.countsteps.{CountSteps, DistributionCountStep}
import au.id.tmm.countstv.model.values.{Count, TransferValue}

private[countdata] class DistributionSourceCalculator(candidates: Set[Candidate]) {

  private val candidatesByShortName: Map[ShortCandidateName, Set[Candidate]] = candidates
    .groupBy(ShortCandidateName.fromCandidate)

  def calculateFor(
                    rawDistributionComment: String,
                    precedingCountSteps: CountSteps[Candidate],
                  ): Option[DistributionCountStep.Source[Candidate]] = {
    val parsedComment = DistributionComment.from(rawDistributionComment)

    parsedComment match {
      case Excluded(1, originatingCounts, transferValue) => {

        val lastExcludedCandidate = precedingCountSteps.last.candidateStatuses.excludedCandidates
          .lastOption
          .getOrElse(throw new IllegalStateException("Comment says a candidate was excluded, but none have been excluded yet"))

        Some(DistributionCountStep.Source(
          candidate = lastExcludedCandidate,
          candidateDistributionReason = CandidateDistributionReason.Exclusion,
          sourceCounts = originatingCounts.map(Count(_)),
          transferValue = TransferValue(transferValue)
        ))
      }
      case Excluded(_, _, _) => {
        throw new UnsupportedOperationException("Cannot handle distribution from multiple excluded candidates")
      }
      case ElectedWithSurplus(candidate, distributionCount, originatingCounts, transferValue) => {
        Some(DistributionCountStep.Source(
          candidate = identifyCandidateFromSurplusDistribution(candidate, precedingCountSteps),
          candidateDistributionReason = CandidateDistributionReason.Election,
          sourceCounts = originatingCounts.map(Count(_)),
          transferValue = TransferValue(transferValue)
        ))
      }
      case ElectedWithQuotaNoSurplus(candidate) => None
      case ElectedLastRemaining(candidatesElected) => None
    }
  }

  private def identifyCandidateFromSurplusDistribution(
                                                        candidateShortName: ShortCandidateName,
                                                        precedingCountSteps: CountSteps[Candidate],
                                                      ): Candidate = {
    val candidatesWithMatchingName = candidatesByShortName.getOrElse(candidateShortName, Set())

    if (candidatesWithMatchingName.isEmpty) {
      throw new IllegalStateException(s"Count step mentions unknown candidate ${candidateShortName}")
    } else if (candidatesWithMatchingName.size == 1) {
      candidatesWithMatchingName.head
    } else {
      // This should be good enough
      val electedCandidatePositions = precedingCountSteps.last.candidateStatuses.electedCandidates

      val electedCandidatesWithMatchingName = candidatesWithMatchingName.filter(c => electedCandidatePositions.contains(c))

      if (electedCandidatesWithMatchingName.size == 1) {
        electedCandidatesWithMatchingName.head
      } else {
        throw new IllegalStateException(s"Multiple elected candidates with the same short " +
          s"name ${candidateShortName}: $electedCandidatePositions")
      }
    }
  }
}

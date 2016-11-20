package au.id.tmm.senatedb.core.parsing.countdata

import au.id.tmm.senatedb.core.model.CountStep
import au.id.tmm.senatedb.core.model.CountStep.{DistributionReason, DistributionSource}
import au.id.tmm.senatedb.core.model.parsing.Candidate
import au.id.tmm.senatedb.core.parsing.countdata.DistributionComment.{ElectedLastRemaining, ElectedWithQuotaNoSurplus, ElectedWithSurplus, Excluded}

private[countdata] class DistributionSourceCalculator(candidates: Set[Candidate]) {

  private val candidatesByShortName: Map[ShortCandidateName, Set[Candidate]] = candidates
    .groupBy(ShortCandidateName.fromCandidate)

  def calculateFor(rawDistributionComment: String,
                   precedingCountSteps: Vector[CountStep]
                  ): Option[DistributionSource] = {
    val parsedComment = DistributionComment.from(rawDistributionComment)

    parsedComment match {
      case Excluded(1, originatingCounts, transferValue) => {
        val lastExcludedCandidate = precedingCountSteps.toStream
          .flatMap(_.excludedThisCount)
          .lastOption
          .getOrElse(throw new IllegalStateException("Comment says a candidate was excluded, but none have been excluded yet"))

        Some(DistributionSource(
          sourceCandidate = lastExcludedCandidate,
          distributionReason = DistributionReason.EXCLUSION,
          sourceCounts = originatingCounts,
          transferValue = transferValue
        ))
      }
      case Excluded(_, _, _) => {
        throw new UnsupportedOperationException("Cannot handle distribution from multiple excluded candidates")
      }
      case ElectedWithSurplus(candidate, distributionCount, originatingCounts, transferValue) => {
        Some(DistributionSource(
          sourceCandidate = identifyCandidateFromSurplusDistribution(candidate, precedingCountSteps).btlPosition,
          distributionReason = DistributionReason.ELECTION,
          sourceCounts = originatingCounts,
          transferValue = transferValue
        ))
      }
      case ElectedWithQuotaNoSurplus(candidate) => None
      case ElectedLastRemaining(candidatesElected) => None
    }
  }

  private def identifyCandidateFromSurplusDistribution(candidateShortName: ShortCandidateName,
                                                       precedingCountSteps: Vector[CountStep]
                                                      ): Candidate = {
    val candidatesWithMatchingName = candidatesByShortName.getOrElse(candidateShortName, Set())

    if (candidatesWithMatchingName.isEmpty) {
      throw new IllegalStateException(s"Count step mentions unknown candidate ${candidateShortName}")
    } else if (candidatesWithMatchingName.size == 1) {
      candidatesWithMatchingName.head
    } else {
      // This should be good enough
      val electedCandidatePositions = precedingCountSteps.last.elected

      val electedCandidatesWithMatchingName = candidatesWithMatchingName.filter(c => electedCandidatePositions.contains(c.btlPosition))

      if (electedCandidatesWithMatchingName.size == 1) {
        electedCandidatesWithMatchingName.head
      } else {
        throw new IllegalStateException(s"Multiple elected candidates with the same short " +
          s"name ${candidateShortName}: $electedCandidatePositions")
      }
    }
  }
}

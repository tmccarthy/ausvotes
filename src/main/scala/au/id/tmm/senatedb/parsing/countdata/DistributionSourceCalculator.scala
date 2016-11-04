package au.id.tmm.senatedb.parsing.countdata

import au.id.tmm.senatedb.model.CountStep
import au.id.tmm.senatedb.model.CountStep.{DistributionReason, DistributionSource}
import au.id.tmm.senatedb.model.parsing.Candidate
import au.id.tmm.senatedb.parsing.countdata.DistributionComment.{ElectedLastRemaining, ElectedWithQuotaNoSurplus, ElectedWithSurplus, Excluded}

private[countdata] class DistributionSourceCalculator (candidates: Set[Candidate]) {

  private val candidateByShortName: Map[ShortCandidateName, Candidate] = candidates
    .groupBy(ShortCandidateName.fromCandidate)
    .map {
      case (name, candidatesWithSameShortName)  => {
        if (candidatesWithSameShortName.size > 1) {
          throw new IllegalStateException(s"More than one candidate with name $name")
        } else {
          name -> candidatesWithSameShortName.head
        }
      }
    }

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
          sourceOutcome = DistributionReason.EXCLUSION,
          sourceCounts = originatingCounts,
          transferValue = transferValue
        ))
      }
      case Excluded(_, _, _) => {
        throw new UnsupportedOperationException("Cannot handle distribution from multiple excluded candidates")
      }
      case ElectedWithSurplus(candidate, distributionCount, originatingCounts, transferValue) => {
        Some(DistributionSource(
          sourceCandidate = candidateByShortName(candidate).btlPosition,
          sourceOutcome = DistributionReason.ELECTION,
          sourceCounts = originatingCounts,
          transferValue = transferValue
        ))
      }
      case ElectedWithQuotaNoSurplus(candidate) => None
      case ElectedLastRemaining(candidatesElected) => None
    }
  }
}

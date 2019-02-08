package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data.DistributionComment._
import au.id.tmm.ausvotes.model.federal.senate.SenateCandidate
import au.id.tmm.countstv.model.CandidateDistributionReason
import au.id.tmm.countstv.model.countsteps.{CountSteps, DistributionCountStep}
import au.id.tmm.countstv.model.values.{Count, TransferValue}

class DistributionSourceCalculator(candidates: Set[SenateCandidate]) {

  private val candidatesByShortName: Map[ShortCandidateName, Set[SenateCandidate]] = candidates
    .groupBy(ShortCandidateName.fromCandidate)

  def calculateFor(
                    rawDistributionComment: String,
                    precedingCountSteps: CountSteps[SenateCandidate],
                  ): Either[Exception, Option[DistributionCountStep.Source[SenateCandidate]]] = {
    DistributionComment.from(rawDistributionComment).flatMap {
      case Excluded(1, originatingCounts, transferValue) => {

        for {
          lastExcludedCandidate <- precedingCountSteps.last.candidateStatuses.excludedCandidates
            .lastOption
            .toRight(new IllegalStateException("Comment says a candidate was excluded, but none have been excluded yet"))
        } yield Some(DistributionCountStep.Source(
          candidate = lastExcludedCandidate,
          candidateDistributionReason = CandidateDistributionReason.Exclusion,
          sourceCounts = originatingCounts.map(Count(_)),
          transferValue = TransferValue(transferValue)
        ))
      }
      case Excluded(_, _, _) => {
        Left(new UnsupportedOperationException("Cannot handle distribution from multiple excluded candidates"))
      }
      case ElectedWithSurplus(candidate, distributionCount, originatingCounts, transferValue) => {
        identifyCandidateFromSurplusDistribution(candidate, precedingCountSteps).map { candidateFromSurplusDistribution =>
          Some(DistributionCountStep.Source(
            candidate = candidateFromSurplusDistribution,
            candidateDistributionReason = CandidateDistributionReason.Election,
            sourceCounts = originatingCounts.map(Count(_)),
            transferValue = TransferValue(transferValue)
          ))
        }
      }
      case ElectedWithQuotaNoSurplus(candidate) => Right(None)
      case ElectedLastRemaining(candidatesElected) => Right(None)
    }
  }

  private def identifyCandidateFromSurplusDistribution(
                                                        candidateShortName: ShortCandidateName,
                                                        precedingCountSteps: CountSteps[SenateCandidate],
                                                      ): Either[Exception, SenateCandidate] = {
    val candidatesWithMatchingName = candidatesByShortName.getOrElse(candidateShortName, Set())

    if (candidatesWithMatchingName.isEmpty) {
      Left(new IllegalStateException(s"Count step mentions unknown candidate ${candidateShortName}"))
    } else if (candidatesWithMatchingName.size == 1) {
      Right(candidatesWithMatchingName.head)
    } else {
      // This should be good enough
      val electedCandidatePositions = precedingCountSteps.last.candidateStatuses.electedCandidates

      val electedCandidatesWithMatchingName = candidatesWithMatchingName.filter(c => electedCandidatePositions.contains(c))

      if (electedCandidatesWithMatchingName.size == 1) {
        Right(electedCandidatesWithMatchingName.head)
      } else {
        Left(new IllegalStateException(s"Multiple elected candidates with the same short " +
          s"name ${candidateShortName}: $electedCandidatePositions"))
      }
    }
  }
}

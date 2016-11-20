package au.id.tmm.senatedb.core.parsing.countdata

private [countdata] sealed trait DistributionComment

private [countdata] object DistributionComment {
  private val excludedPattern = ("Preferences with a transfer value of ([\\d\\.]+) will be distributed in " +
    "count # (\\d+) after the exclusion of (\\d+) candidate\\(s\\). Preferences received at count\\(s\\) ([\\d,]+)\\.")
    .r("transferValue", "distributionCount", "numExcluded", "originatingCounts")

  private val electedWithSurplusPattern = ("([\\w'\\-\\s]+) ,(\\w) has (\\d+) surplus vote\\(s\\) to be distributed in " +
    "count # (\\d+) at a transfer value of ([\\d\\.]+)\\. (\\d+) papers are involved from count number\\(s\\) ([\\d,]+)\\.")
    .r("surname", "initial", "surplusVotes", "count", "transferValue", "numPapers", "originatingCounts")

  private val electedWithQuotaNoSurplusPattern = "(\\w+), (\\w) has been elected at count (\\d+) with (\\d+) votes\\."
    .r("surname", "initial", "count", "votes")

  private val electedLastRemainingPattern = "(.*) have been elected to the remaining positions\\."
    .r("names")

  def from(rawComment: String): DistributionComment = {
    rawComment match {
      case excludedPattern(transferValue, distributionCount, numExcluded, originatingCounts) =>
        Excluded(
          numCandidatesExcluded = numExcluded.toInt,
          originatingCounts = splitOriginatingCounts(originatingCounts),
          transferValue = transferValue.toDouble
        )

      case electedWithSurplusPattern(surname, initial, surplusVotes, count, transferValue, numPapers, originatingCounts) =>
        ElectedWithSurplus(
          candidate = ShortCandidateName(surname, initial),
          distributionCount = count.toInt,
          originatingCounts = splitOriginatingCounts(originatingCounts),
          transferValue = transferValue.toDouble
        )

      case electedWithQuotaNoSurplusPattern(surname, initial, count, votes) =>
        ElectedWithQuotaNoSurplus(ShortCandidateName(surname, initial))

      case electedLastRemainingPattern(rawNames) => {
        val shortNamePattern = "(\\w+), (\\w)".r("surname", "initial")

        val names = shortNamePattern.findAllMatchIn(rawNames)
          .map(m => ShortCandidateName(m.group("surname"), m.group("initial")))
          .toSet

        ElectedLastRemaining(names)
      }

      case _ => throw new IllegalArgumentException(s"Couldn't parse comment $rawComment")
    }
  }

  private def splitOriginatingCounts(originatingCountsString: String): Set[Int] = originatingCountsString
    .split(',')
    .toSet
    .map((s: String) => s.toInt)

  final case class Excluded(numCandidatesExcluded: Int, originatingCounts: Set[Int], transferValue: Double)
    extends DistributionComment

  final case class ElectedWithSurplus(candidate: ShortCandidateName, distributionCount: Int, originatingCounts: Set[Int], transferValue: Double)
    extends DistributionComment

  final case class ElectedWithQuotaNoSurplus(candidate: ShortCandidateName)
    extends DistributionComment

  final case class ElectedLastRemaining(candidates: Set[ShortCandidateName]) extends DistributionComment
}
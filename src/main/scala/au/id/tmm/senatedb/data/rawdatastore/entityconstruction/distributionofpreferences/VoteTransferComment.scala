package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences

private[distributionofpreferences] sealed trait VoteTransferComment

private[distributionofpreferences] object VoteTransferComment {
  private val excludedPattern = ("Preferences with a transfer value of ([\\d\\.]+) will be distributed in " +
    "count # (\\d+) after the exclusion of (\\d+) candidate\\(s\\). Preferences received at count\\(s\\) ([\\d,]+)\\.")
    .r("transferValue", "count", "numExcluded", "originatingCounts")

  private val electedWithSurplusPattern = ("(\\w+) ,(\\w) has (\\d+) surplus vote\\(s\\) to be distributed in " +
    "count # (\\d+) at a transfer value of ([\\d\\.]+)\\. (\\d+) papers are involved from count number\\(s\\) (\\d+)\\.")
    .r("surname", "initial", "surplusVotes", "count", "transferValue", "numPapers", "originatingCount")

  private val electedWithQuotaNoSurplusPattern = "(\\w+), (\\w) has been elected at count (\\d+) with (\\d+) votes\\."
    .r("surname", "initial", "count", "votes")

  private val electedLastRemainingPattern = ".* have been elected to the remaining positions\\."
    .r("surname", "initial")

  def from(rawComment: String): VoteTransferComment = {
    rawComment match {
      case excludedPattern(transferValue, count, numExcluded, originatingCounts) =>
        Excluded(numExcluded.toInt, originatingCounts.split(',').toStream.map(_.toInt).toSet, transferValue.toDouble)

      case electedWithSurplusPattern(surname, initial, surplusVotes, count, transferValue, numPapers, originatingCount) =>
        ElectedWithSurplus(ShortCandidateName(surname, initial), count.toInt, transferValue.toDouble)

      case electedWithQuotaNoSurplusPattern(surname, initial, count, votes) =>
        ElectedWithQuotaNoSurplus(ShortCandidateName(surname, initial))

      case electedLastRemainingPattern() => ElectedLastRemaining

      case _ => throw new IllegalArgumentException(s"Couldn't parse comment $rawComment")
    }
  }

  final case class Excluded(numCandidatesExcluded: Int, originatingCounts: Set[Int], transferValue: Double)
    extends VoteTransferComment

  final case class ElectedWithSurplus(candidate: ShortCandidateName, originatingCount: Int, transferValue: Double)
    extends VoteTransferComment

  final case class ElectedWithQuotaNoSurplus(candidate: ShortCandidateName)
    extends VoteTransferComment

  case object ElectedLastRemaining extends VoteTransferComment
}
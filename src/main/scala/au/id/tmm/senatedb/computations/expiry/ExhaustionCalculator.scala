package au.id.tmm.senatedb.computations.expiry

import au.id.tmm.senatedb.computations.expiry.ExhaustionCalculator.Expiry
import au.id.tmm.senatedb.data.CountData
import au.id.tmm.senatedb.data.database.model.{CandidatesRow, CountOutcomesPerCandidateRow}
import au.id.tmm.senatedb.model.{CandidatePosition, NormalisedBallot}

class ExhaustionCalculator private(candidates: Set[CandidatesRow], countData: CountData) {

  // Not so much when a candidate "expires" (this is meaningless), but when the candidate is elected or excluded
  private lazy val expiryPerCandidatePosition: Map[CandidatePosition, Expiry] = {

    val lastCount = countData.steps.last.count

    val positionPerCandidateId = candidates.groupBy(_.candidateId)
      .mapValues(_.head)
      .mapValues(_.position)

    // Candidates excluded at the last count were not excluded during the count.
    def excludedAtLastCount(outcome: CountOutcomesPerCandidateRow) = outcome.outcomeAtCount == lastCount

    val candidatesElectedAtCount: Map[Int, Int] = countData.steps
      .map(_.stepRow)
      .map(stepRow => stepRow.count -> stepRow.progressiveNumCandidatesElected)
      .toMap

    countData.outcomes
      .filterNot(excludedAtLastCount)
      .map {
        case CountOutcomesPerCandidateRow(_, _, candidateId, _, _, countWhenExcludedOrElected) =>
          positionPerCandidateId(candidateId) -> Expiry(countWhenExcludedOrElected, candidatesElectedAtCount(countWhenExcludedOrElected))
      }
      .toMap
  }

  def computeExhaustionOf(ballot: NormalisedBallot): Option[Expiry] = {
    val expiries = ballot.candidateOrder
      .toStream
      .map(position => expiryPerCandidatePosition.get(position))
      .toSet

    expiries
      .max(ExhaustionCalculator.orderExpiryFavouringUnexpired)
  }

}

object ExhaustionCalculator {
  def apply(candidates: Set[CandidatesRow], countData: CountData): ExhaustionCalculator = new ExhaustionCalculator(candidates, countData)

  final case class Expiry(atCount: Int, candidatesElected: Int) extends Ordered[Expiry] {
    override def compare(that: Expiry): Int = this.atCount compare that.atCount
  }

  private val orderExpiryFavouringUnexpired: Ordering[Option[Expiry]] = new Ordering[Option[Expiry]] {
    override def compare(left: Option[Expiry], right: Option[Expiry]): Int = {
      if (left.isEmpty) {
        1
      } else if (right.isEmpty) {
        -1
      } else {
        left compare right
      }
    }
  }
}
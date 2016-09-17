package au.id.tmm.senatedb.computations.expiry

import au.id.tmm.senatedb.computations.expiry.ExhaustionCalculator.Count
import au.id.tmm.senatedb.data.CountData
import au.id.tmm.senatedb.data.database.model.CandidatesRow
import au.id.tmm.senatedb.model.{CandidatePosition, NormalisedBallot}

class ExhaustionCalculator private(candidates: Set[CandidatesRow], countData: CountData) {

  val steps = countData.steps.map(_.stepRow).toVector

  // Not so much when a candidate "expires" (this is meaningless), but when the candidate is elected or excluded
  private lazy val expiryPerCandidatePosition: Map[CandidatePosition, Count] = {

    val lastCount = steps.last.count

    val positionPerCandidateId = candidates.groupBy(_.candidateId)
      .mapValues(_.head)
      .mapValues(_.position)

    val candidatesElectedAtCount: Map[Int, Int] = steps
      .map(stepRow => stepRow.count -> stepRow.progressiveNumCandidatesElected)
      .toMap

    steps
      .flatMap(stepRow => {
        val count = stepRow.count
        val distributingFromCandidate = stepRow.votesDistributedFromPosition

        distributingFromCandidate.map(position => position -> Count(count, candidatesElectedAtCount(count)))
      })
      .toMap
  }

  def computeExhaustionOf(ballot: NormalisedBallot): Option[Count] = {
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

  final case class Count(ordinal: Int, candidatesElected: Int) extends Ordered[Count] {
    override def compare(that: Count): Int = this.ordinal compare that.ordinal
  }

  private val orderExpiryFavouringUnexpired: Ordering[Option[Count]] = new Ordering[Option[Count]] {
    override def compare(left: Option[Count], right: Option[Count]): Int = {
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
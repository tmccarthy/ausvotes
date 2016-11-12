package au.id.tmm.senatedb.model

import au.id.tmm.senatedb.model.CountData.CountOutcome
import au.id.tmm.senatedb.model.CountStep.DistributionReason.DistributionReason
import au.id.tmm.senatedb.model.CountStep.{CountStepTransfer, DistributionStep, InitialAllocation}
import au.id.tmm.senatedb.model.parsing.CandidatePosition
import au.id.tmm.utilities.collection.OrderedSet
import au.id.tmm.utilities.geo.australia.State

final case class CountData(election: SenateElection,
                           state: State,
                           totalFormalPapers: Long,
                           quota: Long,
                           initialAllocation: InitialAllocation,
                           distributionSteps: Vector[DistributionStep],
                           outcomes: Map[CandidatePosition, CountOutcome]) {
  override def toString: String = super.toString

  def getDistributionStepForCount(count: Int): DistributionStep = {
    require(count >= 2, "Can't have a distribution count at count 1 or below")

    distributionSteps(count - 2)
  }

  val steps: Vector[CountStep] = Vector(initialAllocation) ++ distributionSteps
}

object CountData {
  sealed trait CountOutcome

  object CountOutcome {

    final case class Excluded(orderExcluded: Int, excludedAtCount: Int) extends CountOutcome

    final case class Elected(orderElected: Int, electedAtCount: Int) extends CountOutcome

    case object Remainder extends CountOutcome
  }
}

sealed trait CountStep {
  def count: Int

  def candidateTransfers: Map[CandidatePosition, CountStepTransfer]

  def exhaustedTransfer: CountStepTransfer
  def gainLossTransfer: CountStepTransfer

  def electedThisCount: OrderedSet[CandidatePosition]
  def excludedThisCount: Option[CandidatePosition]

  def elected: OrderedSet[CandidatePosition]
  def excluded: OrderedSet[CandidatePosition]
}

object CountStep {
  final case class InitialAllocation(candidateTransfers: Map[CandidatePosition, CountStepTransfer],

                                     exhaustedTransfer: CountStepTransfer,
                                     gainLossTransfer: CountStepTransfer,

                                     elected: OrderedSet[CandidatePosition]
                                    ) extends CountStep {
    override val count = 1

    override val excludedThisCount = None

    override val electedThisCount = elected
    override val excluded = OrderedSet[CandidatePosition]()
  }

  final case class DistributionStep(count: Int,

                                    source: DistributionSource,

                                    candidateTransfers: Map[CandidatePosition, CountStepTransfer],

                                    exhaustedTransfer: CountStepTransfer,
                                    gainLossTransfer: CountStepTransfer,

                                    electedThisCount: OrderedSet[CandidatePosition],
                                    excludedThisCount: Option[CandidatePosition],

                                    elected: OrderedSet[CandidatePosition],
                                    excluded: OrderedSet[CandidatePosition]
                                   ) extends CountStep

  final case class DistributionSource(sourceCandidate: CandidatePosition,
                                      distributionReason: DistributionReason,
                                      sourceCounts: Set[Int],
                                      transferValue: Double)

  final case class CountStepTransfer(papers: Long,
                                     votesTransferred: Long,
                                     votesTotal: Long)

  object DistributionReason extends Enumeration {
    type DistributionReason = Value

    val ELECTION, EXCLUSION = Value
  }
}
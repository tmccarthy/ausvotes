package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences

import au.id.tmm.senatedb.data.CandidatePosition
import au.id.tmm.senatedb.data.database.model.CountStepRow.StepType
import au.id.tmm.senatedb.data.database.model.CountStepRow.StepType.StepType

private[this] sealed trait VoteTransferSummary {
  def fromCandidate: Option[CandidatePosition]
  def stepType: StepType
}

private[this] object VoteTransferSummary {
  case object Initial extends VoteTransferSummary {
    val fromCandidate = None
    val stepType = StepType.INITIAL
  }

  case class FromElected(electedCandidate: CandidatePosition) extends VoteTransferSummary {
    def fromCandidate = Some(electedCandidate)
    val stepType = StepType.DISTRIBUTED_FROM_ELECTED
  }

  case class FromExcluded(excludedCandidate: CandidatePosition) extends VoteTransferSummary {
    val fromCandidate = Some(excludedCandidate)
    val stepType = StepType.DISTRIBUTED_FROM_EXCLUDED
  }
}
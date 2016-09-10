package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences

import au.id.tmm.senatedb.data.CandidatePosition
import au.id.tmm.senatedb.data.database.model.CountStepRow.StepType
import au.id.tmm.senatedb.data.database.model.CountStepRow.StepType.StepType

private[distributionofpreferences] sealed trait VoteTransferSummary {
  def fromCandidate: Option[CandidatePosition]
  def stepType: StepType
}

private[distributionofpreferences] object VoteTransferSummary {
  case object Initial extends VoteTransferSummary {
    val fromCandidate = scala.None
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

  /**
    * Used for the special case of where a Vote Transfer Summary is meaningless, eg "what is the vote transfer after all
    * candidates have been elected"
    */
  case object None extends VoteTransferSummary {
    val fromCandidate = scala.None
    def stepType = throw new UnsupportedOperationException
  }
}
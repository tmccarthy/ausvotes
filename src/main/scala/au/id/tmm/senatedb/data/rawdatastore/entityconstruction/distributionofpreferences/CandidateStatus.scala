package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences

import au.id.tmm.senatedb.data.database.model.CandidateOutcome
import au.id.tmm.senatedb.data.database.model.CandidateOutcome.CandidateOutcome

private[distributionofpreferences] object CandidateStatus extends Enumeration {
  type CandidateStatus = Value

  val ELECTED, EXCLUDED, UNDETERMINED = Value

  def parsedFrom(string: String): CandidateStatus = string.trim.toLowerCase match {
    case "elected" => ELECTED
    case "excluded" => EXCLUDED
    case "" => UNDETERMINED
    case _ => throw new IllegalArgumentException(s"Unrecognised candidate status '$string'")
  }

  def statusToOutcome(status: CandidateStatus): CandidateOutcome = status match {
    case ELECTED => CandidateOutcome.ELECTED
    case EXCLUDED | UNDETERMINED => CandidateOutcome.EXCLUDED
  }
}
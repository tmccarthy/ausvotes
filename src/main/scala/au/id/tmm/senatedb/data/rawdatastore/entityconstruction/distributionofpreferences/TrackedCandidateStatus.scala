package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences

import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences.CandidateStatus.CandidateStatus

private[distributionofpreferences] sealed trait TrackedCandidateStatus

private[distributionofpreferences] object TrackedCandidateStatus {
  case object Undetermined extends TrackedCandidateStatus
  case class Determined(status: CandidateStatus, order: Int, determinedAtCount: Int) extends TrackedCandidateStatus
}
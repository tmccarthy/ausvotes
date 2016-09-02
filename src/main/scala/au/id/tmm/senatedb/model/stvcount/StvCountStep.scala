package au.id.tmm.senatedb.model.stvcount

sealed trait StvCountStep {
  def subsequentSnapshot: StvCountSnapshot
}

case class InitialAllocation(initialSnapshot: StvCountSnapshot)

case class DistributeSurplus(subsequentSnapshot: StvCountSnapshot,
                             candidate: Candidate,
                             transferValue: Double,
                             votesTransferringPerCandidate: Map[Candidate, Int],
                             votesExhausting: Int)

case class ExcludeCandidate(subsequentSnapshot: StvCountSnapshot,
                            candidate: Candidate,
                            votesTransferringPerCandidate: Map[Candidate, Int],
                            votesExhausting: Int)
package au.id.tmm.senatedb.model.stvcount

final case class StvCountSnapshot(votesPerCandidate: Map[Candidate, Int],
                                  transferValuePerCandidate: Map[Candidate, Double],
                                  candidatesElectedOrder: List[Candidate],
                                  excludedCandidates: Set[Candidate]) {

}

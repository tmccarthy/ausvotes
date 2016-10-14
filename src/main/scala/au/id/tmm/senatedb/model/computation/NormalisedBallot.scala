package au.id.tmm.senatedb.model.computation

import au.id.tmm.senatedb.model.parsing.CandidatePosition

final case class NormalisedBallot(atlCandidateOrder: Vector[CandidatePosition],
                                  atlFormalPreferenceCount: Int,
                                  btlCandidateOrder: Vector[CandidatePosition],
                                  btlFormalPreferenceCount: Int,
                                  canonicalOrder: Vector[CandidatePosition]) {
  def isInformal = canonicalOrder.isEmpty
}

package au.id.tmm.senatedb.core.model.computation

import au.id.tmm.senatedb.core.model.parsing.{CandidatePosition, Group}

final case class NormalisedBallot(atlGroupOrder: Vector[Group],
                                  atlCandidateOrder: Vector[CandidatePosition],
                                  atlFormalPreferenceCount: Int,
                                  btlCandidateOrder: Vector[CandidatePosition],
                                  btlFormalPreferenceCount: Int,
                                  canonicalOrder: Vector[CandidatePosition]) {
  def isInformal = canonicalOrder.isEmpty
  def isFormal = !isInformal
  def isNormalisedToAtl = isFormal && canonicalOrder == atlCandidateOrder
  def isNormalisedToBtl = isFormal && canonicalOrder == btlCandidateOrder
  def isFormalAtl = atlFormalPreferenceCount > 0
  def isFormalBtl = btlFormalPreferenceCount > 0
}

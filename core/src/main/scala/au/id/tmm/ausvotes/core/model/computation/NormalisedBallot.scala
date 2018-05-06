package au.id.tmm.ausvotes.core.model.computation

import au.id.tmm.ausvotes.core.model.parsing.{CandidatePosition, Group}

final case class NormalisedBallot(atlGroupOrder: Vector[Group],
                                  atlCandidateOrder: Vector[CandidatePosition],
                                  atlFormalPreferenceCount: Int,
                                  btlCandidateOrder: Vector[CandidatePosition],
                                  btlFormalPreferenceCount: Int,
                                  canonicalOrder: Vector[CandidatePosition]) { // TODO this should be an option
  def isInformal: Boolean = canonicalOrder.isEmpty
  def isFormal: Boolean = !isInformal

  def isNormalisedToAtl: Boolean = isFormal && canonicalOrder == atlCandidateOrder
  def isNormalisedToBtl: Boolean = isFormal && canonicalOrder == btlCandidateOrder

  def isFormalAtl: Boolean = atlFormalPreferenceCount > 0
  def isFormalBtl: Boolean = btlFormalPreferenceCount > 0
}

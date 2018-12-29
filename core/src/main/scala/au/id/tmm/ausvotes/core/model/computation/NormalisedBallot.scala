package au.id.tmm.ausvotes.core.model.computation

import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateGroup}

final case class NormalisedBallot(atlGroupOrder: Vector[SenateGroup],
                                  atlCandidateOrder: Vector[SenateCandidate],
                                  atlFormalPreferenceCount: Int,
                                  btlCandidateOrder: Vector[SenateCandidate],
                                  btlFormalPreferenceCount: Int,
                                  canonicalOrder: Vector[SenateCandidate]) { // TODO this should be an option
  def isInformal: Boolean = canonicalOrder.isEmpty
  def isFormal: Boolean = !isInformal

  def isNormalisedToAtl: Boolean = isFormal && canonicalOrder == atlCandidateOrder
  def isNormalisedToBtl: Boolean = isFormal && canonicalOrder == btlCandidateOrder

  def isFormalAtl: Boolean = atlFormalPreferenceCount > 0
  def isFormalBtl: Boolean = btlFormalPreferenceCount > 0
}

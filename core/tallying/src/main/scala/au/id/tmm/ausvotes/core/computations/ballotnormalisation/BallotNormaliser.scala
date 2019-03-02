package au.id.tmm.ausvotes.core.computations.ballotnormalisation

import au.id.tmm.ausvotes.model.instances.BallotNormalisationResultInstances.Ops
import au.id.tmm.ausvotes.model.stv._
import au.id.tmm.countstv.normalisation.BallotNormalisation

class BallotNormaliser[E] private(
                                   rules: StvNormalisationRules,

                                   election: E,
                                   candidatesPerGroup: Group[E] => Vector[StvCandidate[E]],
                                 ) {

  def normalise(ballot: Ballot[E, _, _]): NormalisedBallot[E] = {
    val atl = BallotNormalisation.normalise(rules.atlMandatoryRules, rules.atlOptionalRules)(ballot.groupPreferences)
    val btl = BallotNormalisation.normalise(rules.btlMandatoryRules, rules.btlOptionalRules)(ballot.candidatePreferences)

    val atlCandidateOrder = atl.normalisedBallotIfFormal.map(_.flatMap(candidatesPerGroup))

    val canonicalOrder = btl.normalisedBallotIfFormal orElse atlCandidateOrder

    NormalisedBallot(atl, atlCandidateOrder, btl, canonicalOrder)
  }

}

object BallotNormaliser {

  def apply[E](rules: StvNormalisationRules, election: E, candidatesPerGroup: Group[E] => Vector[StvCandidate[E]]): BallotNormaliser[E] =
    new BallotNormaliser(rules, election, candidatesPerGroup)

}

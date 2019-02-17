package au.id.tmm.ausvotes.core.computations.ballotnormalisation

import au.id.tmm.ausvotes.data_sources.aec.federal.extras.CountRules
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotGroup, SenateCandidate, SenateElectionForState}
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

  // TODO move this upward
  def forSenate(
                 election: SenateElectionForState,
                 candidates: Set[SenateCandidate],
               ): BallotNormaliser[SenateElectionForState] = {
    val relevantCandidates = candidates.toStream
      .filter(_.election == election)

    val candidatesPerGroup: Map[SenateBallotGroup, Vector[SenateCandidate]] =
      relevantCandidates
        .sorted
        .toVector
        .groupBy(_.position.group)

    BallotNormaliser(CountRules.normalisationRulesFor(election.election), election, candidatesPerGroup)
  }

}

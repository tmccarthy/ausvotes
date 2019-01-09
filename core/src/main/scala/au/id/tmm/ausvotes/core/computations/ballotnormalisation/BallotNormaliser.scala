package au.id.tmm.ausvotes.core.computations.ballotnormalisation

import au.id.tmm.ausvotes.core.model.computation.NormalisedBallot
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.countstv.normalisation.BallotNormalisation.Result
import au.id.tmm.countstv.normalisation.{BallotNormalisation, BallotNormalisationRule, BallotNormalisationRules, Preference}

class BallotNormaliser private (
                                 election: SenateElectionForState,
                                 candidates: Set[SenateCandidate],
                               ) {

  private val relevantCandidates = candidates.toStream
    .filter(_.election == election)

  private val candidatesPerGroup: Map[SenateBallotGroup, Vector[SenateCandidate]] =
    relevantCandidates
      .sorted
      .toVector
      .groupBy(_.position.group)

  def normalise(ballot: SenateBallot): NormalisedBallot = {
    val (atlGroupOrder, atlCandidateOrder, atlFormalPrefCount) = normaliseAtl(ballot.groupPreferences)
    val (btlCandidateOrder, btlFormalPrefCount) = normaliseBtl(ballot.candidatePreferences)

    val canonicalCandidateOrder = if (btlCandidateOrder.nonEmpty) {
      btlCandidateOrder
    } else {
      atlCandidateOrder
    }

    NormalisedBallot(atlGroupOrder, atlCandidateOrder, atlFormalPrefCount, btlCandidateOrder, btlFormalPrefCount, canonicalCandidateOrder)
  }

  private def normaliseAtl(atlPreferences: AtlPreferences): (Vector[SenateGroup], Vector[SenateCandidate], Int) = {
    val groupsInPreferenceOrder = BallotNormalisation.normalise(
      mandatoryRules = BallotNormalisationRules(Set(
        BallotNormalisationRule.MinimumPreferences(1),
      )),
      optionalRules = BallotNormalisationRules(Set(
        BallotNormalisationRule.MinimumPreferences(6),
        BallotNormalisationRule.CountingErrorsForbidden,
        BallotNormalisationRule.TicksForbidden,
        BallotNormalisationRule.CrossesForbidden,
      )),
    )(atlPreferences) match {
      case Result.Formal(normalisedBallot) => normalisedBallot
      case Result.Saved(normalisedBallot, _) => normalisedBallot
      case Result.Informal(normalisedBallot, _, _) => Vector.empty
    }

    val formalPreferenceCount = groupsInPreferenceOrder.size

    val candidateOrder = distributeToCandidatePositions(groupsInPreferenceOrder)

    (groupsInPreferenceOrder, candidateOrder, formalPreferenceCount)
  }

  private def distributeToCandidatePositions(groupsInPreferenceOrder: Vector[SenateGroup]): Vector[SenateCandidate] =
    groupsInPreferenceOrder.flatMap(candidatesPerGroup)

  private def normaliseBtl(btlPreferences: Map[SenateCandidate, Preference]): (Vector[SenateCandidate], Int) = {
    val candidateOrder = BallotNormalisation.normalise(
      mandatoryRules = BallotNormalisationRules(Set(
        BallotNormalisationRule.MinimumPreferences(6),
      )),
      optionalRules = BallotNormalisationRules(Set(
        BallotNormalisationRule.MinimumPreferences(12),
        BallotNormalisationRule.CountingErrorsForbidden,
        BallotNormalisationRule.TicksForbidden,
        BallotNormalisationRule.CrossesForbidden,
      )),
    )(btlPreferences) match {
      case Result.Formal(normalisedBallot) => normalisedBallot
      case Result.Saved(normalisedBallot, _) => normalisedBallot
      case Result.Informal(normalisedBallot, _, _) => Vector.empty
    }

    val formalPreferenceCount = candidateOrder.size

    (candidateOrder, formalPreferenceCount)
  }

}

object BallotNormaliser {
  def apply(election: SenateElectionForState, candidates: Set[SenateCandidate]): BallotNormaliser =
    new BallotNormaliser(election, candidates)
}

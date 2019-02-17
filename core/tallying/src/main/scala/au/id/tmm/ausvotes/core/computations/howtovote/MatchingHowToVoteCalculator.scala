package au.id.tmm.ausvotes.core.computations.howtovote

import au.id.tmm.ausvotes.model.federal.senate.{AtlPreferences, SenateBallot, SenateGroup, SenateHtv}
import au.id.tmm.countstv.normalisation.Preference
import au.id.tmm.utilities.geo.australia.State

// TODO this should be generalised to not be specific to the Senate
final class MatchingHowToVoteCalculator private(howToVoteCards: Set[SenateHtv]) {

  private val quickCandidateCardLookup: Map[(State, SenateGroup), Set[SenateHtv]] = {
    howToVoteCards.groupBy(htvCard => (htvCard.election.state, htvCard.issuer))
  }.withDefaultValue(Set())

  private val atlPrefsFor: Map[SenateHtv, AtlPreferences] = {
    def computeAtlPrefsFor(howToVoteCard: SenateHtv): AtlPreferences = {
      howToVoteCard.suggestedOrder.zipWithIndex.map {
        case (group, index) => group -> Preference.Numbered(index + 1)
      }.toMap
    }

    howToVoteCards.map(card => card -> computeAtlPrefsFor(card)).toMap
  }

  def findMatchingHowToVoteCard(ballot: SenateBallot): Option[SenateHtv] = {
    if (ballot.candidatePreferences.nonEmpty) {
      return None
    }

    val firstPreferencedGroup = ballot.groupPreferences.find {
      case (group, preference) => preference == Preference.Numbered(1)
    } map {
      case (group, preference) => group
    }

    val candidateHtvCards = firstPreferencedGroup.map(quickCandidateCardLookup(ballot.election.state, _))
      .getOrElse(Set())

    candidateHtvCards.find(matches(ballot, _))
  }

  private def matches(ballot: SenateBallot, card: SenateHtv): Boolean = ballot.groupPreferences == atlPrefsFor(card)
}

object MatchingHowToVoteCalculator {
  def apply(howToVoteCards: Set[SenateHtv]): MatchingHowToVoteCalculator
  = new MatchingHowToVoteCalculator(howToVoteCards)
}

package au.id.tmm.senatedb.core.computations.howtovote

import au.id.tmm.senatedb.core.model.HowToVoteCard
import au.id.tmm.senatedb.core.model.parsing.Ballot.AtlPreferences
import au.id.tmm.senatedb.core.model.parsing.{Ballot, Group, Preference}
import au.id.tmm.utilities.geo.australia.State

final class MatchingHowToVoteCalculator private(howToVoteCards: Set[HowToVoteCard]) {

  private val quickCandidateCardLookup: Map[(State, Group), Set[HowToVoteCard]] = {
    howToVoteCards.groupBy(htvCard => (htvCard.state, htvCard.group))
  }.withDefaultValue(Set())

  private val atlPrefsFor: Map[HowToVoteCard, AtlPreferences] = {
    def computeAtlPrefsFor(howToVoteCard: HowToVoteCard): AtlPreferences = {
      howToVoteCard.groupOrder.zipWithIndex.map {
        case (group, index) => group -> Preference.Numbered(index + 1)
      }.toMap
    }

    howToVoteCards.map(card => card -> computeAtlPrefsFor(card)).toMap
  }

  def findMatchingHowToVoteCard(ballot: Ballot): Option[HowToVoteCard] = {
    if (ballot.btlPreferences.nonEmpty) {
      return None
    }

    val firstPreferencedGroup = ballot.atlPreferences.find {
      case (group, preference) => preference == Preference.Numbered(1)
    } map {
      case (group, preference) => group
    }

    val candidateHtvCards = firstPreferencedGroup.map(quickCandidateCardLookup(ballot.state, _))
      .getOrElse(Set())

    candidateHtvCards.find(matches(ballot, _))
  }

  private def matches(ballot: Ballot, card: HowToVoteCard): Boolean = ballot.atlPreferences == atlPrefsFor(card)
}

object MatchingHowToVoteCalculator {
  def apply(howToVoteCards: Set[HowToVoteCard]): MatchingHowToVoteCalculator
  = new MatchingHowToVoteCalculator(howToVoteCards)
}
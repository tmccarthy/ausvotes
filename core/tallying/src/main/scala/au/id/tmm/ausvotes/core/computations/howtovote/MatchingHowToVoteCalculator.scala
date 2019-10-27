package au.id.tmm.ausvotes.core.computations.howtovote

import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator.QuickLookupKey
import au.id.tmm.ausvotes.model.HowToVoteCard

import scala.collection.immutable.ArraySeq

final class MatchingHowToVoteCalculator[E, C] private (howToVoteCards: Set[HowToVoteCard[E, C]]) {

  private val quickCandidateCardLookup: QuickLookupKey[E, C] => Set[HowToVoteCard[E, C]] =
    howToVoteCards
      .groupBy(htvCard => QuickLookupKey(htvCard.election, htvCard.suggestedOrder.head))
      .withDefaultValue(Set())

  def findMatchingHowToVoteCard(ballot: ArraySeq[C], electionForBallot: E): Option[HowToVoteCard[E, C]] =
    ballot.headOption.flatMap { firstPreferencedCandidate =>
      val quickLookupKey = QuickLookupKey(electionForBallot, firstPreferencedCandidate)

      val candidateHtvCards = quickCandidateCardLookup(quickLookupKey)

      candidateHtvCards.find(htvCard => matches(ballot, electionForBallot, htvCard))
    }

  private def matches(
                       ballot: ArraySeq[C],
                       electionForBallot: E,
                       card: HowToVoteCard[E, C],
                     ): Boolean = electionForBallot == card.election && ballot == card.suggestedOrder.toVector
}

object MatchingHowToVoteCalculator {

  def apply[E, C](howToVoteCards: Set[HowToVoteCard[E, C]]): MatchingHowToVoteCalculator[E, C] =
    new MatchingHowToVoteCalculator(howToVoteCards)

  private final case class QuickLookupKey[E, C](election: E, firstPreferencedCandidate: C)
}

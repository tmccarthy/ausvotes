package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.computations.donkeyvotes.DonkeyVoteDetector
import au.id.tmm.ausvotes.core.computations.exhaustion.ExhaustionCalculator
import au.id.tmm.ausvotes.core.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.ausvotes.core.computations.howtovote.MatchingHowToVoteCalculator
import au.id.tmm.ausvotes.core.computations.savings.SavingsComputation
import au.id.tmm.ausvotes.model.HowToVoteCard
import au.id.tmm.ausvotes.model.stv._
import au.id.tmm.countstv.normalisation.BallotNormalisation

object BallotFactsComputation {

  def computeFactsFor[E : Ordering, J, I](
                                           election: E,

                                           howToVoteCards: Set[HowToVoteCard[E, Group[E]]],
                                           countData: CountData[E],

                                           matchingHowToVoteCalculator: MatchingHowToVoteCalculator[E, Group[E]],
                                           ballotNormaliser: BallotNormaliser[E],

                                           ballots: Iterable[Ballot[E, J, I]],
                                         ): Vector[StvBallotWithFacts[E, J, I]] = {

    val ballotsWithNormalised: Vector[(Ballot[E, J, I], NormalisedBallot[E])] = ballots.map(ballot => ballot -> ballotNormaliser.normalise(ballot)).toVector

    val exhaustionsPerBallot = ExhaustionCalculator
      .exhaustionsOf(countData, ballotsWithNormalised)

    ballotsWithNormalised.map {
      case (ballot, normalisedBallot) =>
        StvBallotWithFacts[E, J, I](
          ballot,
          normalisedBallot,
          DonkeyVoteDetector.isDonkeyVote(ballot),
          FirstPreferenceCalculator.firstPreferenceOf(normalisedBallot),
          matchingHowToVote = normalisedBallot match {
            case NormalisedBallot(BallotNormalisation.Result.Formal(groupOrder), _, _, _) =>
              matchingHowToVoteCalculator.findMatchingHowToVoteCard(groupOrder, election)

            case _ => None
          },
          exhaustionsPerBallot(ballot),
          SavingsComputation.savingsProvisionsUsedBy(normalisedBallot)
        )
    }
  }

}

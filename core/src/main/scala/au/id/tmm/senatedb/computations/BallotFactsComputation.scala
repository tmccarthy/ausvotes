package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.computations.donkeyvotes.DonkeyVoteDetector
import au.id.tmm.senatedb.computations.exhaustion.ExhaustionCalculator
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.utilities.geo.australia.State

object BallotFactsComputation {

  def computeFactsFor(election: SenateElection,
                      state: State,
                      computationInputData: ComputationInputData,
                      computationTools: ComputationTools,
                      ballots: Iterable[Ballot]): Iterable[BallotWithFacts] = {

    val ballotsWithNormalised = ballots.map(ballot => ballot -> computationTools.normaliser.normalise(ballot)).toVector

    val exhaustionsPerBallot = ExhaustionCalculator
      .exhaustionsOf(computationInputData.countDataForState, ballotsWithNormalised)

    ballotsWithNormalised.map {
      case (ballot, normalisedBallot) => {
        BallotWithFacts(
          ballot = ballot,
          normalisedBallot = normalisedBallot,
          isDonkeyVote = DonkeyVoteDetector.isDonkeyVote(ballot),
          firstPreference = computationTools.firstPreferenceCalculator.firstPreferenceOf(normalisedBallot),
          matchingHowToVote = computationTools.matchingHowToVoteCalculator.findMatchingHowToVoteCard(ballot),
          exhaustionsPerBallot(ballot)
        )
      }
    }
  }

}

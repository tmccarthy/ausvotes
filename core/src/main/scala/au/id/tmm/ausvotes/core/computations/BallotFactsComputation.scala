package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.computations.donkeyvotes.DonkeyVoteDetector
import au.id.tmm.ausvotes.core.computations.exhaustion.ExhaustionCalculator
import au.id.tmm.ausvotes.core.computations.savings.SavingsComputation
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Ballot
import au.id.tmm.utilities.geo.australia.State

object BallotFactsComputation {

  def computeFactsFor(election: SenateElection,
                      state: State,
                      computationInputData: ComputationInputData,
                      computationTools: ComputationTools,
                      ballots: Iterable[Ballot]): Iterable[BallotWithFacts] = {

    val ballotNormaliser = computationTools.stateLevel.ballotNormaliser

    val ballotsWithNormalised = ballots.map(ballot => ballot -> ballotNormaliser.normalise(ballot)).toVector

    val exhaustionsPerBallot = ExhaustionCalculator
      .exhaustionsOf(computationInputData.stateLevel.countData, ballotsWithNormalised)

    ballotsWithNormalised.map {
      case (ballot, normalisedBallot) => {
        BallotWithFacts(
          ballot = ballot,
          normalisedBallot = normalisedBallot,
          isDonkeyVote = DonkeyVoteDetector.isDonkeyVote(ballot),
          firstPreference = computationTools.stateLevel.firstPreferenceCalculator.firstPreferenceOf(normalisedBallot),
          matchingHowToVote = computationTools.electionLevel.matchingHowToVoteCalculator.findMatchingHowToVoteCard(ballot),
          exhaustion            = exhaustionsPerBallot(ballot),
          savingsProvisionsUsed = SavingsComputation.savingsProvisionsUsedBy(ballot, normalisedBallot)
        )
      }
    }
  }

}

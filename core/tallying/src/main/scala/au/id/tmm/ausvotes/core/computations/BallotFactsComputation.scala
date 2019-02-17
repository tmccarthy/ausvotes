package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.computations.donkeyvotes.DonkeyVoteDetector
import au.id.tmm.ausvotes.core.computations.exhaustion.ExhaustionCalculator
import au.id.tmm.ausvotes.core.computations.firstpreference.FirstPreferenceCalculator
import au.id.tmm.ausvotes.core.computations.savings.SavingsComputation
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallot, SenateElectionForState}

object BallotFactsComputation {

  def computeFactsFor(election: SenateElectionForState,
                      computationInputData: ComputationInputData,
                      computationTools: ComputationTools,
                      ballots: Iterable[SenateBallot]): Iterable[BallotWithFacts] = {

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
          firstPreference = FirstPreferenceCalculator.firstPreferenceOf(normalisedBallot),
          matchingHowToVote = computationTools.electionLevel.matchingHowToVoteCalculator.findMatchingHowToVoteCard(ballot),
          exhaustion            = exhaustionsPerBallot(ballot),
          savingsProvisionsUsed = SavingsComputation.savingsProvisionsUsedBy(ballot, normalisedBallot)
        )
      }
    }
  }

}

package au.id.tmm.senatedb.computations

import au.id.tmm.senatedb.computations.donkeyvotes.DonkeyVoteDetector
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.utilities.geo.australia.State

object BallotFactsComputation {

  def computeFactsFor(election: SenateElection,
                      state: State,
                      groupsAndCandidates: GroupsAndCandidates,
                      divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                      computationTools: ComputationTools,
                      ballots: Iterable[Ballot]): Iterable[BallotWithFacts] = {

    ballots.map(ballot => {
      val normalisedBallot = computationTools.normaliser.normalise(ballot)

      BallotWithFacts(
        ballot = ballot,
        normalisedBallot = normalisedBallot,
        isDonkeyVote = DonkeyVoteDetector.isDonkeyVote(ballot),
        firstPreferencedParty = computationTools.firstPreferenceCalculator.firstPreferencedPartyOf(normalisedBallot),
        matchingHowToVote = computationTools.matchingHowToVoteCalculator.findMatchingHowToVoteCard(ballot)
      )
    })
  }

}
